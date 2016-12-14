package com.main;

import com.control.Controller;
import com.core.Core;
import com.gui.GUI;

import rice.environment.Environment;

/**
 * Clase principal que lanza el programa
 * @author yamal
 *
 */
public class Main {

	public static void main(String[] args) {
		//Creamos el objeto Environment necesario para que funcione FreePastry
		Environment env = new Environment();
		//Establecemos los parametros adecuados
		env.getParameters().setString("nat_search_policy","never");
		
		//Instanciamos Core, Controller y Vista
		Core core = new Core(env);
		Controller c = new Controller(core);
		GUI v = new GUI(c);
	}
}
