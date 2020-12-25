package com.client;

// Created By Marcal Harrison
// Twitter: @_phantxmh

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client 
{
	DataOutputStream outputStream;
	DataInputStream inputStream;
	Socket socket;
	private JFrame frame = new JFrame("Client");
	private JPanel panel = new JPanel();
	private JPanel infoPanel = new JPanel();
	private JLabel label = new JLabel("Enter value here: ");
	private JTextField box = new JTextField(5);
	private JTextArea info =  new JTextArea();
	private JButton button = new JButton("Send");		
	
	public Client()
	{
		panel.add(label);
		panel.add(box);
		panel.add(button);
		panel.setBackground(Color.CYAN);
		info.setSize(new Dimension(100,100));
		info.setEditable(false);
		infoPanel.add(info);
		frame.add(panel,BorderLayout.NORTH);
		frame.add(info,BorderLayout.CENTER);		
		frame.setVisible(true);
		frame.setSize(new Dimension(500,250));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");	
		
		button.addActionListener(new ActionListener()
		{			
			public void actionPerformed(ActionEvent e) 
			{
				double radius = 0;
				info.setText("");
				try {
					radius = Double.parseDouble(box.getText());
					if(radius < 0)
					{
						JOptionPane.showMessageDialog(frame, "The area cannot be less than 0!","Client", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					LocalDateTime Time = LocalDateTime.now();
					String clientTime = dtf.format(Time);									
												
						outputStream.writeDouble(radius);					
						outputStream.writeUTF(clientTime); //Time client connects
						System.out.println("Written");						
						
						double ans = inputStream.readDouble(); //area calculated by server
						
						if(ans>=0)
						{
							DecimalFormat df = new DecimalFormat();
							String result = String.format("%.02f", ans);
							System.out.println("Read");
							String recieved = dtf.format(Time);
							info.append("Time sent: "+clientTime+"\n");
							info.append("Area  recieved: "+result+" units squared\n");
							info.append("Time recieved: "+recieved);
						}else
						{
							throw new IOException();
						}					
				}catch(NumberFormatException error) {				
					JOptionPane.showMessageDialog(frame, "The input should be numbers!","Client", JOptionPane.WARNING_MESSAGE);
					box.setText("");					
				}catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "Unable to connect to the server!","Client", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();					
				}catch(NullPointerException x) {				
					JOptionPane.showMessageDialog(frame, "The server is not responding!","Client", JOptionPane.WARNING_MESSAGE);
					x.printStackTrace();					
				} 			
			}	
		});
		
		try {
			socket = new Socket("127.0.0.1",40);
			this.getStream();
		} catch (UnknownHostException e1) {	
			JOptionPane.showMessageDialog(frame, "IP address unknown!","Client", JOptionPane.WARNING_MESSAGE);
			e1.printStackTrace();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(frame, "Unable to connect to server!","Client", JOptionPane.WARNING_MESSAGE);
			e1.printStackTrace();
		}	
	}
			
	public void getStream()
	{
		try
		{
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());			
			System.out.println("Streams initialized!");
		}catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "Failed to create connection to server!","Client", JOptionPane.WARNING_MESSAGE);			
			e.printStackTrace();
		}catch(NullPointerException e)
		{
			JOptionPane.showMessageDialog(null, "Failed to initialize stream!","Client", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args0)
	{
		new Client();
	}
}
