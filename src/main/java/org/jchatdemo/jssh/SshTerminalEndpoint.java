package org.jchatdemo.jssh;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Setter
@Getter
public class SshTerminalEndpoint {
    private SSHConnection sshConnection;

    public void replyMsg(String sid, byte[] result) {
        for (byte b : result) {
            System.out.write(b);
        }
    }

    public void read() {
        try {
            int b = System.in.read();
            if (getSshConnection() != null) {
                getSshConnection().sendCmd(new byte[]{(byte) b});
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
