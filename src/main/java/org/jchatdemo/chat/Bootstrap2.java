package org.jchatdemo.chat;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;

public class Bootstrap2 {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setTitle("JChat2");

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


        DatagramSocket datagramSocket = new DatagramSocket(new InetSocketAddress(28888));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    try {
                        datagramSocket.receive(packet);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (packet.getData().length > 0) {
                        String message = new String(packet.getData(), 0, packet.getLength());
                        textPane.setText(textPane.getText() + message);
                    }
                }
            }
        }).start();


        sendButton.addActionListener(e -> {
            String ip = ipField.getText().trim();
            String message = msgField.getText().trim();
            if (ip.isEmpty() || message.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid IP address");
            } else {
                byte[] data = message.getBytes();


                try {
                    int port = 18888;
                    System.out.println("send data:" + ip + ":" + port);
                    DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress(Inet4Address.getByName(ip), port));
                    datagramSocket.send(packet);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, "Failed to send message");
                    e1.printStackTrace();
                }
                msgField.setText("");
            }
        });
    }
}
