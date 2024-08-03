package org.jchatdemo.chat;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BootstrapServer {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 500);
        frame.setTitle("BootstrapServer");

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");

        JTextField ipField = new JTextField();
        JTextField msgField = new JTextField();

        JButton sendButton = new JButton("Send");

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(ipField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(msgField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(sendButton, gbc);


        frame.setLayout(new BorderLayout());
        frame.add(textPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);


        frame.setVisible(true);
        frame.setLocationRelativeTo(null);


        ServerSocket serverSocket = new ServerSocket(18888);

        Map<String, Socket> sockets = new HashMap<String, Socket>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        Socket socket = serverSocket.accept();
                        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buffer)) > 0) {
                            String msg = new String(buffer, 0, len);
                            textPane.setText(textPane.getText() + msg);
                        }
                        sockets.put(socket.getInetAddress().getHostAddress(), socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        sendButton.addActionListener(e -> {
            String ip = ipField.getText();
            String message = msgField.getText();
            if (ip.isEmpty() || message.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid IP address");
            } else {

                try {
                    Socket socket = sockets.get(ip);
                    if (socket != null) {
                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                        outputStream.writeUTF(message);
                        outputStream.flush();
                        msgField.setText("");
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, "Failed to send message");
                    e1.printStackTrace();
                }

            }
        });
    }
}
