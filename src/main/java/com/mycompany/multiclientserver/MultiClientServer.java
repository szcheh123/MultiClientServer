/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.multiclientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class MultiClientServer implements Runnable{

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    public Map<ClientHandler, String> clientStatuses;
    private int clientCount;
    private boolean running = false;
    private AtomicInteger nextClientId;
    private MultiClientServerGUI serverGUI;

    public MultiClientServer(MultiClientServerGUI serverGUI, int port) {
        try {
            this.serverGUI = serverGUI;
            serverSocket = new ServerSocket(port);
            clients = new ArrayList<>();
            clientStatuses = new HashMap<>();
            clientCount = 0;
            nextClientId = new AtomicInteger(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int generateClientId() {
        return nextClientId.getAndIncrement();
    }

    public void start() {
        System.out.println("Server started. Listening on port " + serverSocket.getLocalPort() + "...");
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                // Accept incoming client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                

                // Create a new thread to handle the client
                ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                clients.add(clientHandler);
                clientCount++;
                clientHandler.start();
                SwingUtilities.invokeLater(() -> {
                    serverGUI.updateClientStatuses(this);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getClientCount() {
        return clientCount;
    }

    public Map<ClientHandler, String> getClientStatuses() {
        return clientStatuses;
    }
    
    public void setClientStatuses(ClientHandler client, String status) {
        synchronized (clientStatuses) {
            clientStatuses.put(client, status);
        }
    }

    public void sendMessageToClient(int clientId, String message) {
        for (ClientHandler client : clients) {
            if (client.getClientId() == clientId) {
                try {
                    client.sendMessage(message);
                } catch (IOException ex) {
                    Logger.getLogger(MultiClientServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            }
        }
        // If the target client is not found
        System.out.println("Target client with ID " + clientId + " not found.");
    }
    
    public void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                try {
                    client.sendMessage(message);
                } catch (IOException ex) {
                    Logger.getLogger(MultiClientServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        clientCount--;
    }

    public void close() {
        running = false;
        try {
            serverSocket.close();
            
            for (ClientHandler client : clients) {
                client.disconnectClient();
            }

            clients.clear();
            clientCount = 0;
            
            System.out.println("Server closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getIPAdress(){
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.getName().startsWith("wlan")) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address.getHostAddress().contains(".")) { // Check for IPv4 address
                            System.out.println("IPv4 Address: " + address.getHostAddress());
                            return address;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(MultiClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //InetAddress localhost = InetAddress.getLocalHost();
        //System.out.println("Server IP Address: " + localhost.getHostName());
        //return localhost;
        return null;
    }
    
    
//    public static void main(String[] args) {
////        int port = 12345;
////        MultiClientServer server = new MultiClientServer(port);
////        server.start();
//    }
}
