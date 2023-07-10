/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.multiclientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import packets.Message;

public class ClientHandler extends Thread {

    private MultiClientServer server;
    private Socket clientSocket;
    private InputStream input;
    private OutputStream output;
    private boolean isConnected;
    private int clientId;
    Lock objectLock = new ReentrantLock();
    private InputStream objectInputStream;
    private OutputStream objectOutputStream;

    public ClientHandler(MultiClientServer server, Socket socket) {
        this.server = server;
        clientSocket = socket;
        isConnected = true;
        clientId = server.generateClientId();
        String status = "Connected";
        server.setClientStatuses(this, status);
    }
    
    public int getClientId() {
        return clientId;
    }

//    @Override
//    public void run() {
//        try {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//
//            while (isConnected && (bytesRead = input.read(buffer)) != -1) {
//                String message = new String(buffer, 0, bytesRead);
//                System.out.println("Received from client: " + message);
//
//                // Broadcast the received message to all other clients
//                server.broadcastMessage(message, this);
//            }
//
//            // Client disconnected, remove it from the list and update client count
//            server.removeClient(this);
//            System.out.println("Client disconnected: " + clientSocket);
//            clientSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendMessage(String message) {
//        try {
//            output.write(message.getBytes());
//            output.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    
    @Override
    public void run() {
        
        while (isConnected) {
//                Object obj = objectInputStream.readObject();
//                if (obj instanceof String) {
//                    String message = (String) obj;
//                    System.out.println("Received from client: " + message);
//
//                    // Broadcast the received message to all other clients
//                    server.broadcastMessage(message, this);
//                }
            boolean isWritingObject = false;
            try {
                input = new NonClosingInputStream(clientSocket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            while (!isWritingObject) {
                // Read the signal from the server using the normal InputStream
                int signal = 0;
                
                try {
                    signal = input.read();
                } catch (IOException ex) {

                }
                if (signal == 1) {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    isWritingObject = true;
                    System.out.println("signal received.");
                }
            }
            objectLock.lock();
            try {
                objectInputStream = new NonClosingInputStream(clientSocket.getInputStream());
                // Initialize the ObjectInputStream after receiving the signal
                ObjectInputStream objectInput = new ObjectInputStream(objectInputStream);
                System.out.println("input stream initialized.");
                // Read the object sent by the server
                Object obj = objectInput.readObject();
                System.out.println("read");
                if (obj instanceof String) {
                    String message = (String) obj;
                    System.out.println("Received from client: " + message);

                    //Broadcast the received message to all other clients
                    server.broadcastMessage(message, this);
                }else if (obj instanceof Message){
                    Message message = (Message) obj;
                    System.out.println("Received from client: " + message.toString());

                    server.broadcastMessage(message.toString(), this);
                }

                // Close the ObjectInputStream when done
                objectInput.close();
                objectInputStream.close();
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                objectLock.unlock();
            }
        }

        // Client disconnected, remove it from the list and update client count
        server.removeClient(this);
        System.out.println("Client disconnected: " + clientSocket);
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    

    public void sendMessage(Object packet) throws IOException {
        ObjectOutputStream objectOutput = null;
        
        objectLock.lock();
        try {
            output = new NonClosingOutputStream(clientSocket.getOutputStream());
            // Write the object to the ObjectOutputStream
            output.write(1);
            output.close();
            
            objectOutputStream = new NonClosingOutputStream(clientSocket.getOutputStream());
            objectOutput = new ObjectOutputStream(objectOutputStream);
            objectOutput.writeObject(packet);
            objectOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Release the lock in a finally block
            objectLock.unlock();
            if (objectOutput != null) {
                try {
                    objectOutput.close();
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean isClientConnected() {
        return isConnected;
    }

    public void disconnectClient() {
        isConnected = false;
        server.setClientStatuses(this, "Disconnected.");
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public SocketAddress getSocketAddress() {
        return clientSocket.getRemoteSocketAddress();
    }
    
    

}

