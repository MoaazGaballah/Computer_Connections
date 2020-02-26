/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_17_02_2020;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Moaaz
 */
public class TCP_Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        String host = "localhost";
        int port = 44444;
        
        new TCP_Client().start(host, port);
    }
    
    private void start(String host, int port) throws IOException{
        // creating client socket
        Socket socket = new Socket(host, port);
        
        //input: messages comming to client
        Scanner input = new Scanner(socket.getInputStream());
        // output : messages going to server
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        
        // a message from client's server can be sent
        System.out.print("Send message to server : ");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            // read message from console
            String message = scanner.nextLine();
            // sending message to server
            output.println(message);
            
            // server closed connection
            if (message.contains("end") || message.contains("close")) {
                System.out.println("Client closed the connection!");
                // terminate the program
                System.exit(0);
            }
            // getting message from server
            message = input.nextLine();
            System.out.println("server sends : " + message);
            
            // client closed connection
            if (message.contains("end") || message.contains("close")) {
                System.out.println("Server closed the connection!");
                // terminate the program
                System.exit(0);
            }
            
            System.out.print("Send message to server : ");
        }
    }
    
}