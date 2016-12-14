package com.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.control.Controller;
import com.observer.GUIObserver;

/**
 * La vista implementa a la clase GUIObserver para recibir notificaciones de la lógica (el modelo)
 * @author yamal
 *
 */
public class GUI extends JFrame implements GUIObserver{
	//Etiqueta que indica el id del nodo con el que estamos conectados a la red
	private JLabel myNodeId;
	
	//Campo de texto y etiqueta para introducir la direccion IP del nodo a partir del cual nos queremos conetctar
	private JTextField bootAddressInput;
	private JLabel bootAddressLabel;
	
	//Campo de texto y etiqueta para introducir el puerto del nodo a partir del cual nos queremos conetctar
	private JTextField bootportInput;
	private JLabel bootportLabel;
	
	//Campo de texto y etiqueta para introducir el puerto con el que nos queremos conectar a la red
	private JTextField bindportInput;
	private JLabel bindportLabel;
	
	//Boton para conectarse a la red una vez introducidos los campos anteriores
	private JButton connectButton;
	
	//Campo de texto para introducir un mensaje que se enviará a todos nuestros vecinos
	private JTextField inputMessage;
	//Boton para enviar el mensaje anterior
	private JButton sendButton;
	
	//Area de texto con barra de scroll para mostrar los mensajes y notificaciones que nos llegan
	private JScrollPane scrollPane;
	private JTextArea resultados;
	
	//Campo de texto para introducir el mensaje que se quiere guardar en la DHT
	private JTextField contentToSave;
	//Boton para guardar el mensaje anterior en la DHT
	private JButton saveContent;
	
	//Boton para recuperar los mensajes guardados en la DHT desde que este nodo se concecto
	private JButton getContent;
	//Area de texto con scrollbar para mostrar el contenido que se carga de la DHT
	private JScrollPane contentFromStorage;
	private JTextArea contentTextArea;
	
	//Controlador del programa para hacer las llamadas a la lógica
	private Controller c;
	
	/**
	 * Constrctor de la GUI
	 * @param c Controlador a traves del cual la GUI lanza ordenes a la logica
	 */
	public GUI(Controller c){
		super("Vista");
		this.setSize(1000, 500);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        
        initComponents();
        
        this.c = c;
        
        c.addGUIObserver(this);
        
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Metodo que inicializa parte de la GUI
	 */
	private void initComponents(){
		contentToSave = new JTextField();
		saveContent = new JButton("Guardar contenido");
		contentToSave.setBounds(500, 20, 200, 20);
		saveContent.setBounds(500, 50, 200, 20);
		saveContent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.put(contentToSave.getText());
			}
		});
		
		
		getContent = new JButton("Recuperar contenido del sistema");
		getContent.setBounds(710, 20, 200, 20);
		getContent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.get();				
			}
		});
		contentTextArea = new JTextArea("");
		contentTextArea.setEditable(false);
		contentFromStorage = new JScrollPane(contentTextArea);
		contentFromStorage.setBounds(710, 50, 200, 300);
		
		myNodeId = new JLabel("Nodo Id:");
		myNodeId.setBounds(450, 390, 200, 20);
		
		bootAddressInput = new JTextField();
		bootAddressLabel = new JLabel("Enter boot address:");
		bootportInput = new JTextField();
		bootportLabel = new JLabel("Enter boot port:");
		bindportInput = new JTextField();
		bindportLabel = new JLabel("Enter bind port:");
		connectButton = new JButton("Connect node");
		
		
		bootAddressLabel.setBounds(50, 20, 200, 20);
		bootAddressInput.setBounds(200, 20, 200, 20);
		bootportLabel.setBounds(50, 40, 200, 20);
		bootportInput.setBounds(200, 40, 200, 20);
		bindportLabel.setBounds(50, 60, 200, 20);
		bindportInput.setBounds(200, 60, 200, 20);
		connectButton.setBounds(50, 80, 180, 20);
		connectButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int bootport = Integer.parseInt(bootportInput.getText());
				int bindport = Integer.parseInt(bindportInput.getText());
				String bootAddress = bootAddressInput.getText();
				c.conectarse(bindport, bootAddress, bootport);
			}
			
		});
		
		
		inputMessage = new JTextField();
		sendButton = new JButton();
		resultados = new JTextArea();
		
		inputMessage.setText("Escribe aquí el mensaje");
		inputMessage.setBounds(50, 120, 200, 20); 
		
		//endpoint.setText("Nodo destino");
		//endpoint.setBounds(50, 150, 200, 25);
		
		sendButton.setText("Enviar");
		sendButton.setBounds(50, 150, 100, 25);
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				c.enviarMensaje(inputMessage.getText());
			}
		});
		
		
		resultados.setEditable(false);
		resultados.setText("");
		scrollPane = new JScrollPane(resultados);
		scrollPane.setBounds(50, 210, 350, 200);
		
		addComponents();
	}

	/**
	 * Metodo que participa en la inicializacion de la GUI
	 */
	private void addComponents() {
		this.add(getContent);
		this.add(contentFromStorage);
		this.add(contentToSave);
		this.add(saveContent);
		this.add(myNodeId);
		this.add(bootAddressLabel);
		this.add(bootAddressInput);
		this.add(bootportLabel);
		this.add(bootportInput);
		this.add(bindportLabel);
		this.add(bindportInput);
		this.add(connectButton);
		this.add(inputMessage);
		this.add(sendButton);
		this.add(scrollPane);
	}

	
	@Override
	public void newNotification(String resultado) {
		resultados.setText(resultados.getText() + resultado);
	}

	@Override
	public void notifyNodeId(String id) {
		myNodeId.setText("Nodo Id: " + id);
	}
	
	@Override
	public void notifyConectionSucces() {
		resultados.setText(resultados.getText() + "Conexión exitosa.\n");
		connectButton.setEnabled(false);
	}

	@Override
	public void notifyGetContent(String content) {
		contentTextArea.setText(contentTextArea.getText() + content + "\n");
	}
}
