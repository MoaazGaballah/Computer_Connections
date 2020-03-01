package HW1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Moaaz
 */
public class UDP_Client {

    private javax.swing.JTextPane historyJTextPane;
    private javax.swing.JLabel nameJLabel;
    protected ListenThread clientThread;

    protected void start(String host, int port, javax.swing.JTextPane jTextPaneHistory, javax.swing.JLabel jLabelName) throws SocketException {
        try {
            // clientUI history area, every thing will be written
            this.historyJTextPane = jTextPaneHistory;
            // clientUI Name label, will be specified by server
            this.nameJLabel = jLabelName;
            
            InetAddress IPAddress = InetAddress.getLocalHost();
            DatagramSocket clientSocket = new DatagramSocket();
            clientThread = new ListenThread(host, port, IPAddress, clientSocket, nameJLabel);
            clientThread.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDP_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void writeToHistory(Object message) {
        // write a message to history area in UI
        historyJTextPane.setText(historyJTextPane.getText() + "\n" + message);
    }

    protected void closeSocket() throws IOException {
        // close all streams and sockets
        if (clientThread != null) {
            clientThread.interrupt();
        }
    }

    class ListenThread extends Thread {

        private final String host;
        protected final int port;
        protected InetAddress IPAddress;
        protected final DatagramSocket clientSocket;
        private final javax.swing.JLabel nameJLabel;

        public ListenThread(String host, int port, InetAddress IPAddress, DatagramSocket clientSocket,javax.swing.JLabel nameJLabel) {
            this.host = host;
            this.port = port;
            this.IPAddress = IPAddress;
            this.clientSocket = clientSocket;
            this.nameJLabel = nameJLabel;
        }
        
        // listen to messages comming from server
        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    this.clientSocket.receive(receivePacket);
                    // Setting the name of client in clientUI
                    this.nameJLabel.setText(this.getName());
                    String modifiedSentence = new String(receivePacket.getData());
                    writeToHistory("FROM SERVER:" + modifiedSentence);
                    if (modifiedSentence.contains("end")) {
                        clientSocket.close();
                        break;
                    }
                    receiveData = new byte[1024];
                }
            } catch (SocketException ex) {
                Logger.getLogger(UDP_Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(UDP_Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(UDP_Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        protected void sendMessage(String message, int sendPort, InetAddress IPAddress, DatagramSocket serverSocket) {
            try {
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket
                        = new DatagramPacket(sendData, sendData.length, IPAddress, sendPort);
                serverSocket.send(sendPacket);
            } catch (IOException ex) {
                Logger.getLogger(UDP_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
