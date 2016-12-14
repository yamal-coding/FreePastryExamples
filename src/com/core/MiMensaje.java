package com.core;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

/**
 * Clase que se utiliza para enviar mensajes de texto entre vecinos
 * Implementa la interfaz Message. Interfaz que utiliza FreePastry para enviar mensajes por la red 
 * @author yamal
 *
 */
public class MiMensaje implements Message {
	//Id del nodo que lo envia
	private Id from;
	//Id del nodo que lo recibe
	private Id to;
	//Contenido del mensaje
	private String content;
	
	/**
	 * Constructor de la clase
	 * @param from Id Origen
	 * @param to Id destino
	 * @param content Contenido
	 */
	public MiMensaje(Id from, Id to, String content){
		this.from = from;
		this.to = to;
		this.content = content;
	}
	
	public String toString() {
		return content + " (de " + from + " para " + to + ")\n";
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
