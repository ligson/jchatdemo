package org.jchatdemo.jssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Properties;

public class SSHTerminalSimulator extends JFrame {
    private JTextArea textArea;
    private Session session;
    private Channel channel;

    public SSHTerminalSimulator() throws Exception {
        setTitle("SSH Terminal Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 创建文本编辑区域
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // 连接 SSH 服务器
        connectToSSH();
    }

    private void connectToSSH() throws Exception {
        // 连接 SSH 服务器的配置
        String host = "127.0.0.1";
        int port = 22;
        String username = "ligson";


        try {
            // 创建 JSch 会话
            JSch jSch = new JSch();


            File privateKeyDir = new File(System.getProperty("user.home") + "/.ssh");
            File knownHostsFile = new File(privateKeyDir, "known_hosts");
            File privateKeyFile = new File(privateKeyDir, "id_rsa");

            jSch.setKnownHosts(Files.newInputStream(knownHostsFile.toPath()));
            File tempFile = File.createTempFile("id_rsa", "");
            try (InputStream inputStream = Files.newInputStream(privateKeyFile.toPath())) {
                FileUtils.copyInputStreamToFile(inputStream, tempFile);
                jSch.addIdentity(tempFile.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            session = jSch.getSession(username, host, port);
            session.setConfig("PreferredAuthentications", "publickey");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("GSSAPIAuthentication", "no");
            session.setConfig("UseDNS", "no");
            if (tempFile.exists()) {
                tempFile.delete();
            }

            Properties config = new Properties();
            // 去掉首次连接确认
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            // 超时连接时间为3秒
            session.setTimeout(3000);

            session.connect();

            // 打开 SSH 通道
            channel = session.openChannel("shell");
            channel.connect();

            // 启动 I/O 线程
            startIOThread();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    private void startIOThread() throws Exception {
        new Thread(() -> {
            try {
                InputStream inputStream = channel.getInputStream();
                PrintStream outputStream = new PrintStream(channel.getOutputStream());

                // 从文本编辑区读取输入,发送到 SSH 服务器
                while (true) {
                    String text = textArea.getText();
                    outputStream.print(text);
                    outputStream.flush();

                    // 从 SSH 服务器读取输出,更新文本编辑区
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = inputStream.read(buffer)) > 0) {
                        textArea.append(new String(buffer, 0, read));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        SSHTerminalSimulator sshTerminalSimulator = new SSHTerminalSimulator();
        sshTerminalSimulator.setVisible(true);
    }
}
