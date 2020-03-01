package HW1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @file UDP_Server.java
 * @date Feb 24, 2020 , 13:24:00
 * @author Muhammet Alkan
 */
public class UDP_Server {

    protected javax.swing.JTextPane historyJTextPane;
//    private javax.swing.JButton jButtonSendMessage;
    protected ListenThread serverThread;

    public void start(int recievePort, javax.swing.JTextPane historyJTextPane) {
        try {
            this.historyJTextPane = historyJTextPane;
            // first default value
            int sendPort = 0;
            InetAddress IPAddress = InetAddress.getLocalHost();
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            DatagramSocket serverSocket = new DatagramSocket(recievePort);
            serverSocket.receive(receivePacket);
            serverThread = new ListenThread(recievePort, sendPort, IPAddress, serverSocket, receivePacket);
            serverThread.start();
        } catch (SocketException ex) {
            Logger.getLogger(UDP_Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDP_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void writeToHistory(String message) {
        // write a message to history area in UI
        historyJTextPane.setText(historyJTextPane.getText() + "\n" + message);
    }

    protected void stop() throws IOException {
        // close all streams and sockets
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    

    class ListenThread extends Thread {

        private final int recievePort;
        protected int sendPort;
        protected InetAddress IPAddress;
        protected final DatagramSocket serverSocket;
        private DatagramPacket receivePacket;

        public ListenThread(int recievePort, int sendPort, InetAddress IPAddress, DatagramSocket serverSocket, DatagramPacket receivePacket) {
            this.recievePort = recievePort;
            this.sendPort = sendPort;
            this.IPAddress = IPAddress;
            this.serverSocket = serverSocket;
            this.receivePacket = receivePacket;
        }

        @Override
        public void run() {
            try {
//                System.out.println("A thread is created for client : " + this.getName());
//                DatagramSocket serverSocket = new DatagramSocket(this.port);
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                byte[] receiveData = new byte[1024];
                ArrayList<InetAddress> CLIENTS = new ArrayList<InetAddress>();
                while (true) {

                    String sentence = new String(receivePacket.getData());
                    writeToHistory(this.getName() + " : " + sentence);
                    // get the address from the headder of packet
                    this.IPAddress = receivePacket.getAddress();
                    this.sendPort = receivePacket.getPort();
//                    String capitalizedSentence = inFromUser.readLine();
//                    sendMessage(capitalizedSentence, IPAddress, sendPort, this.serverSocket);
                    receiveData = new byte[1024];
                    this.receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    this.serverSocket.receive(receivePacket);
                }
            } catch (SocketException ex) {
                Logger.getLogger(UDP_Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(UDP_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        protected void sendMessage(String message, InetAddress IPAddress, int sendPort, DatagramSocket serverSocket) {
            try {
                byte[] sendData = new byte[1024];
                sendData = message.getBytes();
                DatagramPacket sendPacket
                        = new DatagramPacket(sendData, sendData.length, IPAddress, sendPort);
                serverSocket.send(sendPacket);
            } catch (IOException ex) {
                Logger.getLogger(UDP_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
