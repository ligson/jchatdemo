package org.jchatdemo.jssh;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;

import java.io.File;
import java.io.IOException;

public class SSHTerminal {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 22;
        String username = "ligson";
        String password = "password";

        try {
            Connection connection = new Connection(host, port);
            connection.connect();
            connection.authenticateWithPassword(username, password);


            Session session = connection.openSession();
            session.requestPTY("xterm");
            session.startShell();

            // 在这里实现交互式终端
            // 从 session.getStdout() 读取输出，向 session.getStdin() 写入输入
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
