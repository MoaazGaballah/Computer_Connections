/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lap_17_02_2020;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Moaaz
 */
public class TCP_Server {

    public static void main(String[] args) throws IOException {
        int port = 44444;

        new TCP_Server().start(port);
    }

    private void start(int port) throws IOException {
        // creating client socket (just port number)
        ServerSocket socket = new ServerSocket(port);
        System.out.println("Server launched ..");
        System.out.println("Please launch a client!");

        while (true) {
            // blocking call, waiting for new client connection
            Socket clientSocket = socket.accept();

            System.out.println("New client connected : " + clientSocket);
            // creating a new thread and make it listening for every client
            new listenThread(clientSocket).start();
        }
    }

    class listenThread extends Thread {

        private final Socket socket;
        private int clientClosedConnection = 0;

        public listenThread(Socket clientSocket) {
            this.socket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("New connection established for " + this.getName());
            try {
                // the message client sends to server
                Scanner input = new Scanner(socket.getInputStream());
                // the message server sends to client
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                // as long as client sends a message
                while (input.hasNextLine()) {
                    // client's message
                    String message = input.nextLine();
                    System.out.println(this.getName() + " sends : " + message);

                    // if client closes the connection 
                    if (message.contains("close") || message.contains("end")) {
                        System.out.println(this.getName() + " closed the connection!");
                        socket.close();
                        System.out.println("Socket is closed");
                        System.out.print("Do you want to wait for another client ?");
                        Scanner scanner = new Scanner(System.in);
                        if (scanner.nextLine().contains("no")) {
                            System.exit(0);
                        }
                        clientClosedConnection = 1;
                        break;
                    }

                    System.out.print("Please enter a message to " + this.getName() + " : ");
                    message = new Scanner(System.in).nextLine();
                    output.println(message);
                }

            } catch (IOException ex) {
                Logger.getLogger(TCP_Server.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (clientClosedConnection == 0) {
                        socket.close();
                        System.out.println("Socket is closed");
                        System.out.print("Do you want to wait for another client ?");
                        Scanner scanner = new Scanner(System.in);
                        if (scanner.nextLine().contains("no")) {
                            System.exit(0);
                        }
                    }                    
                } catch (IOException ex) {
                    Logger.getLogger(TCP_Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
