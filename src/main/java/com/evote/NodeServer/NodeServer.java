package com.evote.NodeServer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;

public class NodeServer {
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;

    public NodeServer(int port) {
        try {
            this.server = new ServerSocket(port);
            this.socket = this.server.accept();
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            String line = "";
            while (!line.matches("test_end")) {
                line = in.readUTF();
                System.out.println(line);
            }

            socket.close();
            in.close();
        } catch (IOException e) {

        }
    }

    public static void main(String args[]) {
        NodeServer nodeServer = new NodeServer(5000);
    }
}
