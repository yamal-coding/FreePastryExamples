package com.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import com.observer.CoreObserver;
import com.observer.GUIObserver;

import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.past.ContentHashPastContent;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastImpl;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.leafset.LeafSet;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.persistence.LRUCache;
import rice.persistence.MemoryStorage;
import rice.persistence.PersistentStorage;
import rice.persistence.Storage;
import rice.persistence.StorageManagerImpl;

/**
 * Esta clase coordina la logica del programa. Por eso se llama Core (Nucleo)
 * Recibe ordenes del controlador, realiza la logica y notifica a la vista los cambios
 * @author yamal
 *
 */
public class Core implements CoreObserver {
	//Clase que nos permite virtualizar Pastry en la misma JVM
	private Environment env;
	//Factoria de Ids de nodos
	private NodeIdFactory nidFactory;
	//Factoria de Nodos de la red FreePastry
	private PastryNodeFactory factory;
	//Nodo de la red FreePastry sobre el que trabaja el programa
	private PastryNode node;
	
	//Objeto de la clase Messenger para poder enviar y recibir mensajes de otros nodos
	private Messenger menssenger;
	
	//Objeto GUIObserver para notificar cambios a la GUI
	private GUIObserver guiObserver;
	
	//Factoria que genera Ids usando SHA-1 que se usaran como claves para la DHT
	private PastryIdFactory idf;
	
	//Objeto de la clase Past para poder realizar las operaciones de put/insert o get/lookup
	private Past pastApp;
	
	//Set de objetos KeyMessages para guardar las claves de los contenidos almacenados en la DHT
	//desde que el nodo asociado a este objeto Core (this) se unio a la red
	//private List<KeyMessage> storedKeys;
	private Set<KeyMessage> storedKeys;
	
	/**
	 * Se inicializa a vacia la lista de claves de la DHT guardadas
	 * @param env Objeto Environment necesario para que funcione FreePastry
	 */
	public Core(Environment env){
		this.env = env;
		//this.storedKeys = new ArrayList<KeyMessage>();
		this.storedKeys = new HashSet<KeyMessage>();
	}
	
	/**
	 * Metodo para aniadir a this el observador de la GUI
	 * @param o
	 */
	public void addGUIObserver(GUIObserver o){
		this.guiObserver = o;
	}
	
	/**
	 * Metodo para crear un nodo en el programa y conectarlo a la red FreePastry
	 * @param bindport Puerto con el que se va a conectar el nodo
	 * @param bootAddress Direcccion del nodo que se usara para conectarse a la red
	 * @param bootport Puerto del nodo que se usara para conectarse a la red
	 */
	public void connect(int bindport, String bootAddress, int bootport){
		try {
			InetAddress bootInetAddress = InetAddress.getByName(bootAddress);
			InetSocketAddress bootINetSocketAddress = new InetSocketAddress(bootInetAddress,bootport);
			
			//Instanciamos la factoria de Ids de nodos
			nidFactory = new RandomNodeIdFactory(env);
			
			//Instanciamos la factoria de nodos
			factory = new SocketPastryNodeFactory(nidFactory, bindport, env);
			
			//Instanciamos nuestro nodo sobre el que trabajara el programa y que conectaremos a la red FreePastry
			node = factory.newNode();
			
			//Instanciamos nuestro objeto Messenger que usaremos para enviar y recibir mensajes
			menssenger = new Messenger(node, guiObserver, this);
			
			//Instanciamos la factoria de ids que se usaran como claves de la DHT (usa SHA1 para generar estos Ids)
			idf = new PastryIdFactory(env);
			
			//Ruta del directorio de almacenamiento de contenido Past de nuestro nodo
			String storageDirectory = "./storage" + node.getId().hashCode();
			
			//Instanciamos un objeto Storage con las caracteristicas de memoria especificas que queramos poner
			
			//PersistentStorage utiliza disco duro
			Storage stor = new PersistentStorage(idf, storageDirectory, 4 * 1024 * 1024, node.getEnvironment());
			
			//MemoryStorage utiliza RAM
			//Storage stor = new MemoryStorage(idf);
			
			//Instanciamos la iterfaz Past como PastImpl. Este objeto nos servira para realizar las operaciones de put y get de la DHT
			//Recibe el nodo sobre el que trabaja, el almacenamiento, el numero de replicas de cada almacenamiento entre otros.
			pastApp = new PastImpl(node, new StorageManagerImpl(idf, stor, new LRUCache(new MemoryStorage(idf), 512 * 1204, node.getEnvironment())), 3, "");
			
			//Conectamos nuestro nodo con la direccion de boot que se genero a partir de los parametros de la funcion
			node.boot(bootINetSocketAddress);
		    
		    //Se realizan varios intentos hasta que el nodo se conecta a la red
		    synchronized(node) {
		      while(!node.isReady() && !node.joinFailed()) {
		        //retardo para no colapsar el proceso de conexion
		        node.wait(500);
		        
		        // Si ha habido algun fallo se aborta la conexion y se lanza una excepcion que se captura en el catch
		        if (node.joinFailed()) {
		          throw new IOException("No se pudo conextar a la red FreePastry. Razon: "+node.joinFailedReason()); 
		        }
		      }       
		    }
		    //Se notifica a la GUI el exito de la conexion y del Id del nodo sobre el que trabaja el programa
		    guiObserver.notifyConectionSucces();
		    guiObserver.notifyNodeId(node.getId().toString());
		} catch (Exception e) {
			//Si hubo un error en la conexion se captura en este catch y se notifica de ello a la GUI
			guiObserver.newNotification("Error al conectarse\n");
		}
	    
	}
	
	/**
	 * Metodo para almacenar un contenido en la DHT
	 * @param content Contenido a almacenar
	 */
	public void put(String content){
		//Generamos un hash con idf (PastryIdFactory) usando SHA1 con el contenido. Sera la clave de la DHT
		Id id = idf.buildId(content);
		//Creamos un objeto KeyMessage para poder compartir mas adelante informacion sobre la clave y el nodo que la creo
		KeyMessage key = new KeyMessage(id, node.getId());
		//Instanciamos el contenido Past que se usara para almacenar, con la clase MiContenidoPast
		ContentHashPastContent pastContent = new MiContenidoPast(id, content);
		
		//llamada a insert con el objeto pastApp (Interfaz Past)
		//recibe el contenido a almacenar y una implementacion de la interfaz Continuation (permite el almacenamiento
		//en otro hilo y no esperar a recibir la confirmacion de fin de operacion)
		pastApp.insert(pastContent, new Continuation<Boolean[], Exception>(){
			//Metodo que es llamado cuando ha habido algun error en el insert
			@Override
			public void receiveException(Exception arg0) {
				//Se notifica a la GUI del error
				guiObserver.newNotification("Error al almacenar el contenido \" " + content + "\".\n");
			}
			
			//Metodo que es llamado cuando se ha finalizado el almacenamiento del contenido. Recibe
			//un array de Boolean para saber cuantas de las replicas se han almacenado y cuantas no
			//true -> almacenado; false -> no almacenado
			@Override
			public void receiveResult(Boolean[] arg0) {
				//Contamos cuantas veces se ha almacenado el contenido
				int successfullStores = 0;
				for (int i = 0; i < arg0.length; i++){
					if (arg0[i].booleanValue())
						successfullStores++;
				}
				//Aniadimos a nuestro propio Set (storedKeys) de claves de DHT la clave del contenido recien almacenado
				storedKeys.add(key);
				
				//Enviamos la clave y nuestro Id encapsulados en un objeto KeyMessage a nuestros vecinos
				
				//Recuperamos el conjutno de vecinos del nodo como un objeto LeafSet
				LeafSet leafSet = node.getLeafSet();
				
				//Bucle para enviar mensajes a los vecinos del leafSet.
				for (int i = 0; i <= leafSet.ccwSize(); i++){
			      if (i != 0) { //Para no enviarse el mensaje a si mismo
			    	//Recuperamos el NodeHandler del nodo vecino i. Contiene informacion del nodo.
			        NodeHandle nh = leafSet.get(i);
			        
			        //Mediante el objeto Messenger compartimos la clave con el nodo vecino i
			        menssenger.shareKey(nh, key);
			        
			        
			      //Retardo de 1 segundo
			        try {
						env.getTimeSource().sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			      }
			    }
				
			    //Notificacion a la GUI del exito del almacenamiento del contenido con el numero de veces que se ha replicado este
				guiObserver.newNotification("Contenido \"" + content + "\" almacenado " + successfullStores + " veces.\n");
			}
			
		});
	}
	
	/**
	 * Metodo para recuperar de la DHT el contenido asociado a las claves que hay almacenadas en
	 * storedKeys
	 */
	public void get(){
		//Por cada clave que se tiene guardada en storedKeys se hace un lookup (get) en la DHT de su
		//contenido asociado
		for (KeyMessage key : storedKeys){
			//Usando el objeto pastApp (Interfaz Past) llamamos a la funcion lookup que recibe la clave
			//del contenido y un objeto que implemente a la interfaz Continuation (Interfaz que permite
			//dejar a la espera el proceso de lookup).
			pastApp.lookup(key.getKey(), new Continuation<PastContent, Exception>(){
				//Metodo que se llama cuando ha habido algun error en el lookup de un contenido en la DHT
				@Override
				public void receiveException(Exception arg0) {
					//Se notifica el error a la GUI
					guiObserver.newNotification("Error al recuperar contenido\n.");
				}
				//Metodo que se llama cuando el resultado a sido devuelto por la DHT
				@Override
				public void receiveResult(PastContent arg0) {
					//Se notifica el contenido recuperado a la GUI
					guiObserver.notifyGetContent(arg0.toString());
				}
				
			});
		}
			
	}
	
	/**
	 * Metodo para enviar un mensaje a todos los vecino del nodo sobre el que trabaja el programa
	 * @param content Contenido del mensaje en un String
	 */
	public void enviarMensaje(String content){
		//Recuperamos el conjutno de vecinos del nodo como un objeto LeafSet
		LeafSet leafSet = node.getLeafSet();
	    
		//Bucle para enviar mensajes a los vecinos del leafSet.
	    for (int i= 0; i <= leafSet.cwSize(); i++) {
	      if (i != 0) {//Para no enviarse el mensaje a si mismo
	        //Recuperamos el NodeHandler del nodo vecino i. Contiene informacion del nodo.
	        NodeHandle nh = leafSet.get(i);
	        
	        //Mediante el objeto Messenger enviamos el mensaje al nodo vecino i
	        menssenger.sendMessage(nh, content);
	        
	        //Retardo de 1 segundo
	        try {
				env.getTimeSource().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	      }
	    }
	}

	/**
	 * Este metodo recibe de Messenger una notificacion con un Objeto de la clase KeyMessage
	 * y Core lo almacena en su lista de claves de la DHT.
	 * Despues, Core notifica esta operacion a la GUI 
	 */
	@Override
	public void notifySharedKey(KeyMessage key) {
		storedKeys.add(key);
		guiObserver.newNotification("Recibida nueva clave de " + key.getNodo() + ".\n");
	}
}