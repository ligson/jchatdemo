package org.jchatdemo.chat;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Map;

public class BootstrapClient {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 500);
        frame.setTitle("BootstrapClient");

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


        Map<String, Socket> socketMap = new java.util.HashMap<>();
        sendButton.addActionListener(e -> {
            String ip = ipField.getText();
            String message = msgField.getText();
            if (ip.isEmpty() || message.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid IP address");
            } else {
                try {
                    Socket socket;
                    if (!socketMap.containsKey(ip)) {
                        socket = new Socket(ip, 18888);
                        socketMap.put(ip, socket);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                        String receiveMsg = dataInputStream.readUTF();
                                        textPane.setText(textPane.getText() + "<br>" + receiveMsg);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }).start();

                    } else {
                        socket = socketMap.get(ip); // reuse existing socket
                    }

                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(message);
                    dataOutputStream.flush();
                    msgField.setText("");
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, "Failed to send message");
                    e1.printStackTrace();
                }

            }
        });
    }
}
