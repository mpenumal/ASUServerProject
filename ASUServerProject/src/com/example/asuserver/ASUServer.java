package com.example.asuserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ASUServer {  
	
	public static void main(String args[]) throws Exception { 
		String assignments_directory = "D:/MS3rdYear/SER593/AssignmentList_Server";
		String log_directory = "D:/MS3rdYear/SER593/ASULog_Server";
		File[] assignmentList = new File(assignments_directory).listFiles();
		
		while(true) {
			ServerSocket ss=new ServerSocket(5000); 
			System.out.println ("Waiting for request");
			Socket s=ss.accept();  
			System.out.println ("Connected With "+s.getInetAddress().toString());
			
			BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
			DataInputStream din = new DataInputStream(bis);
			
			String action = din.readUTF();
			
			System.out.println(action);
			
			if (action.equals("Assignment")) {
				BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
				DataOutputStream dout = new DataOutputStream(bos);
				
				try { 
					dout.writeInt(assignmentList.length);
					dout.flush();
						
					for(File assignment : assignmentList)
					{
						long sz = assignment.length();
						dout.writeLong(sz); 
						dout.flush();
						
						System.out.println ("Size: "+sz);
						System.out.println ("Buf size: "+ss.getReceiveBufferSize());
						
						System.out.println("Sending File: "+assignment.getName());
						dout.writeUTF(assignment.getName());  
						dout.flush();
						
						System.out.println("Here");
						
						String availability = din.readUTF();
						
						System.out.println(availability);
						
						if (availability.equals("absent")) {
							FileInputStream fis = new FileInputStream(assignment);
							BufferedInputStream bis2 = new BufferedInputStream(fis);
							int theByte = 0;
							while((theByte = bis2.read()) != -1) bos.write(theByte);
							bis2.close();
						}
						System.out.println("..ok"); 
					}					 
						
					System.out.println("Send Complete");
					dout.flush();
					dout.close();
				}
				catch(Exception e) {
					e.printStackTrace();
					System.out.println("An error occured in the ASUServer class.");
				}
			}
			else {
				long sz = din.readLong();
			
				String filename = din.readUTF();
				
				System.out.println("Receving file: "+filename);
				
				File log = new File(log_directory + "/" + filename);
				log.delete();
				log.createNewFile();
				
				System.out.println("Saving as file: "+log);
				
				FileOutputStream fos = new FileOutputStream(log);
				BufferedOutputStream bos = new BufferedOutputStream(fos);

				for(int j = 0; j < sz; j++) bos.write(bis.read());
				bos.close();
				
				System.out.println("Completed");
			}
			din.close();
			s.close();  
			ss.close(); 
		}
	}	
}