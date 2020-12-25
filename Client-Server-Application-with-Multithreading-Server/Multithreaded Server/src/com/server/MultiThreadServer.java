package com.server;

//Created By Marcal Harrison
//Twitter: @_phantxmh

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MultiThreadServer
{
	private ServerSocket serverSocket = null;
	private Socket socket;	
	Date data = new Date();		
	private JFrame frame = new JFrame("Server");	
	JPanel buttonPanel = new JPanel();
	JPanel infoPanel = new JPanel();
	JPanel statusPanel = new JPanel();	
	JButton startButton = new JButton("START");	
	JButton stopButton = new JButton("STOP");
	JLabel statusLabel = new JLabel("STATUS: ");
	JTextField statusTextField = new JTextField(5); 
	JTextArea info = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(info);
	private int count = 0;
	boolean running = true;
	
	public MultiThreadServer()
	{	
		statusTextField.setEditable(false);
		statusTextField.setText("ONLINE");
		info.setEditable(false);
		statusTextField.setBackground(Color.LIGHT_GRAY);
		buttonPanel.setBackground(Color.GREEN);				
		scrollPane.setAutoscrolls(true);
		scrollPane.setVisible(true);	
		
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		statusPanel.add(statusLabel);
		statusPanel.add(statusTextField);
		
		frame.add(buttonPanel,BorderLayout.NORTH);
		frame.add(scrollPane);
		frame.add(statusPanel,BorderLayout.SOUTH);
		frame.setVisible(true);
		frame.setSize(new Dimension(500,520));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		startButton.addActionListener(new ActionListener() {       
            public void actionPerformed(ActionEvent e)
            {
            	if(running == false)
            	{            		
            		running = true;
            		
            		buttonPanel.setBackground(Color.GREEN);	
                	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            		LocalDateTime serverStart = LocalDateTime.now();
            		System.out.println("The circle server is running...");
            		info.append("Server started: "+dtf.format(serverStart)+"\n\n");
            		statusTextField.setText("ONLINE");
            	}            	
            }            	
        });			
		
		stopButton.addActionListener(new ActionListener() {       
            public void actionPerformed(ActionEvent e)
            {
            	if(running == true)
            	{      
            		running = false;           		
            		          		
            		buttonPanel.setBackground(Color.RED);	
                	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            		LocalDateTime serverStart = LocalDateTime.now();
            		System.out.println("The circle server stopped.");
            		info.append("Server stopped: "+dtf.format(serverStart)+"\n\n");
            		statusTextField.setText("OFFLINE");
            	}            	
            }            	
        });
		
		try
		{				
			serverSocket = new ServerSocket(40);
			
			while(true)
			{			
				socket = serverSocket.accept();
				System.out.println("Passes socket");
				ClientHandler clients = new ClientHandler(this.socket);
				System.out.println("Passes Client");
				clients.start();
				System.out.println("Passes start");
				clients.id = count;
				count++;								
			}			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	class ClientHandler extends Thread implements Runnable
	{
		private Socket socket;		
		private DataInputStream inputStream;
		private DataOutputStream outputStream;
		private int id;	
		
		public ClientHandler(Socket socket)
		{
			super("ClientHandlerThread");
			this.socket = socket;
		}	
		
		public void run()
		{
			try
			{				
				inputStream = new DataInputStream(socket.getInputStream());
				outputStream = new DataOutputStream(socket.getOutputStream());			
					
				while(true)
				{						
					double value = inputStream.readDouble();
					String clientTime = inputStream.readUTF();
					
					if(running)
					{							
						double result = 3.141592653589793238*value*value;	//Calculate area of a circle
						
						String radius = Double.toString(value);
						String ans = String.format("%.02f", result);					
						
						outputStream.writeDouble(result);
				
						info.append("Client "+id+" connected from address: "+socket.getInetAddress().toString());
						info.append("\nTime connected: "+clientTime+"\n");
						info.append("Radius recieved: "+radius+"\n");
						info.append("Area calculated: "+ans+" units squared\n\n");
					}else
					{
						outputStream.writeDouble(-1);						
					}					
				}					
			} catch (IOException e) {	
				
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args0)
	{
		new MultiThreadServer();
	}
}
