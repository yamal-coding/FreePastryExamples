package com.control;

import com.core.Core;
import com.observer.GUIObserver;

public class Controller {
	//Objeto de la clase Core, para realizar llamadas a la logica
	private Core core;
	
	/**
	 * Constructor de la clase Controller
	 * @param core Objeto de la clase Core que implementa la logica del programa
	 */
	public Controller(Core core){
		this.core = core;
	}
	
	/**
	 * Metodo para realizar la concexion de la aplicacion a la red
	 * @param bindport Puerto con el que nos queremos conectar a la red
	 * @param bootAddress Direccion del nodo al que nos conectaremos para entrar en la red
	 * @param bootport Puerto del nodo al que nos conectaremos para entrar en la red
	 */
	public void conectarse(int bindport, String bootAddress, int bootport){
		core.connect(bindport, bootAddress, bootport);
	}
	
	/**
	 * Metodo para aniadir al modelo la GUI como observador
	 * @param o
	 */
	public void addGUIObserver(GUIObserver o){
		core.addGUIObserver(o);
	}
	
	/**
	 * Metodo para enviar un mensaje a todos los vecinos
	 * @param msg
	 */
	public void enviarMensaje(String msg){
		core.enviarMensaje(msg);
	}
	
	/**
	 * Metodo para guardar un String en la DHT
	 * @param content
	 */
	public void put(String content){
		core.put(content);
	}
	
	/**
	 * Metodo para recuperar el contenido de la DHT que se guardo desde que el nodo actual se conecto
	 */
	public void get(){
		core.get();
	}
}
