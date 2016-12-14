package com.core;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

/**
 * Un objeto de esta clase contiene la clave de un contenido almacenado enla DHT y el Id del nodo que la creo
 * Implementa la interfaz Message. Interfaz que utiliza FreePastry para enviar mensajes por la red 
 * @author yamal
 *
 */
public class KeyMessage implements Message{
	//Clave de un contenido de la DHT
	private Id key;
	//Id del nodo que creo la clave anterior
	private Id nodo;
	
	/**
	 * Constructor
	 * @param key
	 * @param nodo
	 */
	public KeyMessage(Id key, Id nodo){
		this.key = key;
		this.nodo = nodo;
	}
	
	/**
	 * 
	 * @return La clave del contenido DHT
	 */
	public Id getKey(){
		return this.key;
	}
	
	/**
	 * 
	 * @return El Id del nodo que creo la clave DHT almacenada en este Objeto
	 */
	public Id getNodo(){
		return this.nodo;
	}
	
	/**
	 * Metodo que devuelve la prioridad de un mensaje al ser enviado por la red.
	 * Devolvemos una prioridad baja para que no afecte al trafico interno de mantenimiento de FreePastry
	 */
	@Override
	public int getPriority() {
		return LOW_PRIORITY;
	}
}
