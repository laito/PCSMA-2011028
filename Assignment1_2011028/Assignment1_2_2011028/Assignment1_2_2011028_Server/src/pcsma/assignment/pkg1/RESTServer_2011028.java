/*
 * This is a very basic multithreaded REST HTTP server.
 * Multiple routes are defined, and the rest give 404
 * 
 * Routes which have been defined:
 *  GET / : Prints {status: "OK"} JSON
 *  GET /myip : Prints {ip: "client_ip_address"} JSON
 *  GET /mybrowser : Prints {browser: "client_user_agent"} JSON
 * 
 * Default port is set to be 80
 * You can test by opening http://localhost/ in the browser
 * 
 * 
 * 
 * Developer's Guide:
 * 
 * You can define more routes and their behaviour in the following places:
 * 
 * 1. Routes definition goes inside the validRoutes HashMap
 * 2. Routes view (JSON Schema) goes inside the routeViews HashMap
 * 3. Routes controller (Variables to be used by schema) can go in two places:
 *      a. Initial string values which a route can has are stored in routeControllers HashMap. This is before the request actually gets initiated.
 *      b. If your controller requires request's data (Like browser, ip etc), then you have to fill in these values inside the getRouteController method.
 * 
 * Thus I have created a very minimalistic bare bones View Controller Architecture for a basic multithreaded HTTP REST API server.
 * 
 * Author: Apoorv Singh (2011028)
 */

package pcsma.assignment.pkg1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;



/**
 *
 * @author root
 */
public class RESTServer_2011028 implements Runnable {

    Integer port = 8080;
    Integer maxConnections = 100;
    ServerSocket sock = null;
    String serverName = "Apoorv's Server";
    ArrayList<String> validRoutes;
    HashMap<String,String> routeViews;
    HashMap<String,String[]> routeControllers;
    
    
    public RESTServer_2011028() {
        System.out.println("Starting Server at http://127.0.0.1:"+port.toString());
        
        /* 
         * Define the routes and their corresponding views here 
         */
        validRoutes = new ArrayList<>(Arrays.asList
            (
                "/", 
                "/myip",
                "/mybrowser"
            ));
        /* 
         * Define here the views for each route
        */
        routeViews = new HashMap<String, String>() {
            {
                put("/", "{status: \"OK\"}");
                put("/myip", "{ip: \"%s\"}");
                put("/mybrowser", "{browser: \"%s\"}");
            }
        };
        
        /* 
         * Define here the variables that can be pushed on the controller BEFORE the request has been processed 
         */    
        routeControllers = new HashMap<String, String[]>() {
            {
                put("/", new String[]{});
                put("/myip", new String[]{});
                put("/mybrowser", new String[]{});
            }
        };
        
        startListening();
    }
    
    
    public void startListening() {
        if (maxConnections == 0) {
            return;
        }
        Integer counter = 0;
        try {
            ServerSocket listener = new ServerSocket(port);
            Socket server;
            while(counter++ < maxConnections) {
                server = listener.accept();
                serverProcess(server);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void serverThread(Socket server) throws IOException {
        System.out.println("Client connected from: "+server.getInetAddress().getHostName());
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        PrintWriter out = new PrintWriter(server.getOutputStream(), true);        
        String[] requestData = new String[50];
        Integer counter = 0;
        for(counter = 0; counter < 50; counter++) {
            requestData[counter] = in.readLine();
            if(requestData[counter] == null) {
                break;
            }
            if(requestData[counter].length() == 0) {
                if(requestData[0].toUpperCase().startsWith("POST")) {
                    readData(requestData, counter, in);
                    counter += 2;
                }
                break;
            }
        }
        
        HashMap<String, String> requestDataMap = new HashMap<String, String>();
        requestDataMap.put("IP", server.getInetAddress().getHostName());
        fillHeaderData(counter, out, requestData, requestDataMap);
        showView(out, requestDataMap);
        server.close();
    }
    
    public void fillHeaderData(Integer limit, PrintWriter out, String[] data, HashMap<String, String> requestDataMap) {
        Integer counter = 0;
        requestDataMap.put("URL", data[0]); /* Add URL to the data map */
        for (counter = 0; counter < limit; counter++) {
            if(data[counter].contains(":")) {
                String[] keyval = data[counter].split(":", 2); 
                requestDataMap.put(keyval[0], keyval[1].trim()); /* Add other fields of the request headers */
            }
            //out.println(data[counter]);
        }
    }
    

    public Integer getLength(String lengthString) {
        StringTokenizer tok = new StringTokenizer(lengthString);
        tok.nextToken();
        return(Integer.parseInt(tok.nextToken()));    
    }
    
    public Integer getContentLength(String[] input) {
        String line;
        Integer counter = 0;
        for (counter = 0; counter < input.length; counter++) {
                if (input[counter].length() == 0) {
                    break;
                }
            line = input[counter].toUpperCase();
            if (line.startsWith("CONTENT-LENGTH"))
                return(getLength(line));
        }
        return 0;
    }
    
    public void readData(String[] input, Integer counter, BufferedReader in) throws IOException {
        Integer contentLength = getContentLength(input);
        char[] postData = new char[contentLength];
        in.read(postData, 0, contentLength);
        input[++counter] = new String(postData, 0, contentLength);
    }
    
    
    /* 
     * Define here the variables that can be pushed on the controller AFTER the request has been processed 
     * 
     */
    public String[] getRouteController(String URL, HashMap<String,String> Data) {
        String[] controller = routeControllers.get(URL);
        ArrayList<String> newController = new ArrayList<String>(Arrays.asList(controller));
        switch(URL) {
            case "/":
                break;
            case "/myip":
                newController.add(Data.get("IP")); /* First variable is IP of the request */
                break;
            case "/mybrowser":
                newController.add(Data.get("User-Agent")); /* First variable is User-Agent of the request */
                break;
        }
        
        return (String[]) newController.toArray(new String[0]);
    }
    
    public String getRouteData(String URL, HashMap<String, String> Data) {
        for(String route : validRoutes) {
            if(URL.equals(route)) {
                String[] routeController = getRouteController(URL, Data);
                return String.format(routeViews.get(route), routeController);
            }
        }
        return "";
    }
    
    public boolean isValidRoute(String URL) {
        if(validRoutes.contains(URL)) {
            return true;
        } else {
            return false;
        }
    }
    
    public void showView(PrintWriter out, HashMap<String, String> Data) {
        String URL = Data.get("URL");
        if(URL == null) {
            return;
        }
        URL = URL.split(" ")[1];
        if(isValidRoute(URL)) {
            String data = getRouteData(URL, Data);
            out.println(
                    "HTTP/1.0 200 OK\r\n" +
                    "Server: " + serverName + "\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    data
            );
        } else {
            print404(out);
        }
    }
    
    public void print404(PrintWriter out) {
        out.println(
                    "HTTP/1.0 404 NOT FOUND\r\n" +
                    "Server: " + serverName + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "\r\n" +
                    "<h1>Not Found</h1>"
            );
    }
    
    public void serverProcess(Socket server) throws IOException {
        Connection currentConnection = new Connection(this, server); 
        currentConnection.start();
    }
    
    public static void main(String[] args) {
        RESTServer_2011028 server = new RESTServer_2011028();
    }
    
    @Override
    public void run() {
        Connection currentThread = (Connection)Thread.currentThread();
        try {
            serverThread(currentThread.getSocket());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

/* 
 * Thread Safe Connection Class 
 */
class Connection extends Thread {
    private Socket serverSocket;

    public Connection(Runnable serverObject, Socket serverSocket) {
        super(serverObject);
        this.serverSocket = serverSocket;
    }
    public Socket getSocket() {
        return serverSocket;
    }
}