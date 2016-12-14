package com.observer;

import com.core.KeyMessage;

/**
 * Interfaz que se usa para que las clase que implementan la logica mas baja se comuniquen con el
 * nucleo de la aplicacion
 * @author yamal
 *
 */
public interface CoreObserver {
	/**
	 * Metodo para transmitir al nucleo de la aplicacion (Objeto de la clase Core) la clave con la que se
	 * ha guardado un contenido en la DHT para que la almacene junto con el Id del nodo que la creo
	 * Ver documentacion de la clase KeyMessage
	 * @param key
	 */
	public void notifySharedKey(KeyMessage key);
}
