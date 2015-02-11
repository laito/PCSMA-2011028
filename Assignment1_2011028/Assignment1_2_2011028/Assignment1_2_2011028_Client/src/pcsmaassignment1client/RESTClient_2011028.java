/*
 * A Simple HTTP Client to request data from the REST HTTP Server built in Assignment 1
 * Sends a simple GET request to /myip on the server
 * Author: Apoorv Singh (2011028)
 */
package pcsmaassignment1client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class RESTClient_2011028 {

    public static void main(String[] args) throws UnknownHostException, IOException {
        
        Integer port = 8080;
        String hostname = "localhost";
        try (Socket clientSocket = new Socket(hostname, port)) {
            DataOutputStream requestStream = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader responseStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            String requestData;
            String serverResponse;
            
            requestData = "GET /myip HTTP/1.1\n" +
                        "Host: localhost\n" +
                        "Connection: keep-alive\n";
            
            requestStream.writeBytes(requestData + '\n');
            
            while((serverResponse = responseStream.readLine()) != null) {
                System.out.println(serverResponse);
            }
        }
    }
}
