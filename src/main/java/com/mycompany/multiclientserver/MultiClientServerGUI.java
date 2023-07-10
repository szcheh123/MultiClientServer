/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.multiclientserver;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.Map;

public class MultiClientServerGUI {

    private JFrame frame;
    private JLabel titleLabel;
    private JLabel serverInfoLabel;
    private JLabel serverStatusLabel;
    private JButton startButton;
    private JButton closeButton;
    private JLabel serverLink;
    private JButton copyButton;
    public JTextArea clientStatusArea;
    private JButton startGameButton;
    private JTextArea eventLogArea;

    private MultiClientServer server;
    private ClientHandler clientHandler;
    private String copyText;

    public MultiClientServerGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Server GUI");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Server GUI");
        headerPanel.add(titleLabel);

//        JPanel serverInfoPanel = new JPanel();
//        serverInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        serverInfoLabel = new JLabel("Server Information: IP Address - Port");
        headerPanel.add(serverInfoLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("Start Server");
        closeButton = new JButton("Close Server");
        serverStatusLabel = new JLabel("Server Status: Stopped");
        serverLink = new JLabel();
        copyButton = new JButton("Copy link");
        contentPanel.add(startButton);
        contentPanel.add(closeButton);
        contentPanel.add(serverStatusLabel);
        contentPanel.add(serverLink);
        contentPanel.add(copyButton);

        JPanel clientPanel = new JPanel();
        clientPanel.setLayout(new BorderLayout());
        clientStatusArea = new JTextArea();
        clientStatusArea.setEditable(false);
        JPanel startGamePanel = new JPanel();
        startGamePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        startGameButton = new JButton("Start Game");
        startGamePanel.add(startGameButton);
        JPanel eventLogPanel = new JPanel(new BorderLayout());
        eventLogPanel.setBorder(BorderFactory.createTitledBorder("Event Log"));
        eventLogPanel.add(new JScrollPane(clientStatusArea), BorderLayout.CENTER);
        JPanel nestedPanel = new JPanel(new BorderLayout());
        nestedPanel.add(startGamePanel, BorderLayout.NORTH);
        nestedPanel.add(eventLogPanel, BorderLayout.CENTER);
        clientPanel.add(nestedPanel, BorderLayout.CENTER);


        frame.add(headerPanel, BorderLayout.NORTH);
//        frame.add(serverInfoPanel, BorderLayout.CENTER);
        frame.add(contentPanel, BorderLayout.SOUTH);
        frame.add(clientPanel, BorderLayout.CENTER);
//        frame.add(eventLogPanel, BorderLayout.EAST);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeServer();
            }
        });
        
        copyButton.addActionListener(e -> {
            copyTextToClipboard(copyText);
        });
        
        startGameButton.addActionListener(e -> {
            server.broadcastMessage("Start Game", clientHandler);
        });
    }

    private void startServer() {
        // Start the server and update GUI accordingly
        // Retrieve server IP address and port number
//        String ipAddress = "127.0.0.1";
        int port = 12345;
        
        server = new MultiClientServer(this,port);
        server.start();
        
        InetAddress serverIP = server.getIPAdress();
        String link = generateServerLink(serverIP, port);
        serverLink.setText(link);
        copyText = link; 
        
        serverInfoLabel.setText("Server Information: " + serverIP + " - " + port);
        serverStatusLabel.setText("Server Status: Running");
        startButton.setEnabled(false);
        closeButton.setEnabled(true);
    }

    private void closeServer() {
        // Close the server and update GUI accordingly
        if (server != null) {
            server.close();
        }

        serverStatusLabel.setText("Server Status: Stopped");
        startButton.setEnabled(true);
        closeButton.setEnabled(false);
    }
    
    private static void copyTextToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }
    
    public String generateServerLink(InetAddress serverIP, int port) {
        String protocol = "http"; // Use the appropriate protocol (e.g., http, https)
        String link = protocol + "://" + serverIP.getHostAddress() + ":" + port;
        return link;
    }

    public void updateClientStatuses(MultiClientServer server) {
        // Retrieve the client statuses from the server and update the clientStatusArea
        System.out.println("update");
        Map<ClientHandler, String> clientStatuses = server.getClientStatuses();
        
//        SwingUtilities.invokeLater(() -> {
//            clientStatusArea.append("Hello");
//            System.out.println("hello");
//        });
        clientStatusArea.setText("");
        System.out.println("next");
        for (Map.Entry<ClientHandler, String> entry : clientStatuses.entrySet()) {
            ClientHandler client = entry.getKey();
            String status = entry.getValue();
            String clientInfo = client.getSocketAddress().toString();
            String text = clientInfo + ": " + status + "\n";
            System.out.println("get: " + clientInfo + " " + status);
            
//            SwingUtilities.invokeLater(() -> {
//                
//            });
            clientStatusArea.append(text);
            System.out.println("done");
        }
    }

    private void addEventLog(String event) {
        // Append event to the eventLogArea
        eventLogArea.append(event + "\n");
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MultiClientServerGUI serverGUI = new MultiClientServerGUI();
                serverGUI.show();
            }
        });
    }
}
