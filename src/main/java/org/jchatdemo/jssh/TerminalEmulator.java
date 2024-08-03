package org.jchatdemo.jssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TerminalEmulator {
    public static void main(String[] args) throws Exception {
        // Establish a SSH connection to the remote server
        JSch jsch = new JSch();
        Session session = jsch.getSession("ligson", "127.0.0.1", 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        // Create a terminal emulator
        Process process = Runtime.getRuntime().exec("vim");

        // Read and write to the terminal emulator
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());

        // Run Vim and interact with it
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            System.out.println(line);

            // Read user input and send it to Vim
            String userInput = System.console().readLine();
            writer.write(userInput + "\n");
            writer.flush();
        }

        // Close the SSH connection
        session.disconnect();
    }
}
