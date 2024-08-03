package org.jchatdemo.jssh;


public class SSHClient {


    public static void main(String[] args) throws Exception {
        SshTerminalEndpoint endpoint = new SshTerminalEndpoint();
        SSHConnection connection = new SSHConnection("localhost", 22, "ligson", "", 1, "1", endpoint);
        connection.connect();
        //System.out.println("Connected to SSH server");
    }

}
