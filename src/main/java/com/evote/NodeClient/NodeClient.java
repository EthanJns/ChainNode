package com.evote.NodeClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NodeClient {
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    public NodeClient(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            System.out.println(String.format("Established connection to %s:%d", address, port));
            this.input = new DataInputStream(System.in);
            this.out = new DataOutputStream(socket.getOutputStream());
            String line = "";
            while (!line.matches("end_test")) {
                line = input.readLine();
                out.writeUTF(line);
            }

            this.input.close();
            this.out.close();
            this.socket.close();
        } catch (IOException ex) {

        }
    }

    public static void main(String args[]) {
        NodeClient client = new NodeClient("127.0.0.1", 5000);
    }

}
