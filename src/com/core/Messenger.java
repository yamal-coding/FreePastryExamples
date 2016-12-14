package com.core;

import com.observer.CoreObserver;
import com.observer.GUIObserver;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;

/**
 * Esta clase implementa la interfaz Application de FreePastry.
 * Se utiliza para enviar y recibir mensajes de otros nodos
 * @author yamal
 *
 */
public class Messenger implements Application {
	//Endpoint de nuestro nodo
	private Endpoint endpoint;
	//Objeto GUIOBserver para notificar a la vista de mensajes recibidos
	private GUIObserver guiObserver;
	//Objeto CoreObserver para enviar al nucleo de la aplicacion las claves de los contenidos que se acaban de guardar
	//en la DHT por otros nodos
	private CoreObserver coreObserver;
	
	/**
	 * Construtor que a partir del nodo sobre el que trabaja el programa construye su endpoint
	 * @param node
	 * @param guiObserver
	 * @param coreObserver
	 */
	public Messenger(Node node, GUIObserver guiObserver, CoreObserver coreObserver){
		this.guiObserver = guiObserver;
		this.coreObserver = coreObserver;
		//Solo se podran recibir y enviar mensajes a aplicaciones cuyo endpoint se haya construido con
		//el mismo string de instancia
		this.endpoint = node.buildEndpoint(this, "myinstance");
		this.endpoint.register();
	}
	
	/**
	 * Metodo que sirve para enviar un mensaje a un nodo concreto
	 * @param nh NodeHandler: este objeto se utiliza para manejar un nodo (contiene informacion acerca del nodo)
	 * @param content Contenido que se quiere enviar
	 */
	public void sendMessage(NodeHandle nh, String content) {  
	    Message msg = new MiMensaje(endpoint.getId(), nh.getId(), content);
	    endpoint.route(null, msg, nh);
	}
	
	/**
	 * Metodo para compartir un objeto de la clase KeyMessage con un nodo concreto
	 * @param nh
	 * @param keyMessage
	 */
	public void shareKey(NodeHandle nh, KeyMessage keyMessage){
		Message msg = keyMessage;
		endpoint.route(null, msg, nh);
	}
	
	/**
	 * Este metodo se llama cuando el nodo sobre el que trabaja la aplicacion recibe un mensaje
	 * Se debe hacer distincion de casos para saber que tipo de mensaje se recibio para notificar
	 * al observador adecuado
	 */
	@Override
	public void deliver(Id arg0, Message arg1) {
		if (arg1 instanceof MiMensaje)
			guiObserver.newNotification(arg1 + "\n");
		else if (arg1 instanceof KeyMessage)
			coreObserver.notifySharedKey((KeyMessage)arg1);

	}

	/**Metodo que se ha de implementar de la clase Application
	 * 
	 */
	@Override
	public boolean forward(RouteMessage arg0) {
		return true;
	}

	/**Metodo que se ha de implementar de la clase Application
	 * 
	 */
	@Override
	public void update(NodeHandle arg0, boolean arg1) {
		
	}

}
