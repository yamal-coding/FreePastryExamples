package com.observer;

/**
 * Interfaz que sirve al modelo para notificar a la GUI de los cambios producidos
 * @author yamal
 *
 */
public interface GUIObserver {
	/**
	 * Metodo que notifica a la GUI del id del nodo con el se ha conectado a la red
	 * @param id
	 */
	public void notifyNodeId(String id);
	
	/**
	 * Metodo que notifica del exito de la conexion a la GUI
	 */
	public void notifyConectionSucces();
	
	/**
	 * Metodo que devuelve a la GUI el contenido recuperado de la DHT tras la llamada get
	 * @param content Contenido devuelto de la DHT
	 */
	public void notifyGetContent(String content);
	
	/**
	 * Metodo para notificar a la GUI de algo con un mensaje especifico y puntual
	 * @param resultado
	 */
	public void newNotification(String resultado);
}
