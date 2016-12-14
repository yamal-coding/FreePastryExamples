package com.core;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

/**
 * Clase que extiende a la clase ContentHashPastContent. Esta clase es necesaria para almacenar contenidos
 * en la DHT de FreePastry
 * @author yamal
 *
 */
public class MiContenidoPast extends ContentHashPastContent {
	//Contenido que se almacena
	private String content;
	
	/**
	 * Construtor de la clase
	 * @param id Id que usara como hash para almacenar el contenido
	 * @param content
	 */
	public MiContenidoPast(Id id, String content) {
		super(id);
		this.content = content;
	}
	
	public String toString(){
		return this.content;
	}

}
