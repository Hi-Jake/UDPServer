package com.example.pc.udpserver;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Thread {
    public static final int SERVERPORT = 4444;

    private DatagramSocket socket;
    public Server() throws SocketException {
        super();
        socket = new DatagramSocket(SERVERPORT);
    }

    @Override
    public void run() {
        while(true){
            try{
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String line = new String(packet.getData(),0,packet.getLength());
                Log.d("UDP", "S: Received: '" + line + "'");

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String message = "Thanks";
                buf = message.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
