import java.net.*;
import java.io.*;
public class Server {

    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;
    final int portNumber = 1001;

    public Server() {
        try {
            server = new ServerSocket(portNumber);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            
            startReading();
            startWriting();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public void startReading() {
        Thread t1 = new Thread(new Runnable(){
        public void run() {
            System.out.println("Reader started...");

            while(!socket.isClosed()) {
            try {
                    String msg = br.readLine();
                    if("End".equals(msg)) {
                        socket.close();
                        System.out.println("Client terminated the chat.");
                        System.out.println("Reader stoped...");
                        break;
                    }
                    System.out.println("Client: "+msg);
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }});
        t1.start(); 
    }

    synchronized public void startWriting() {
        Thread t2 = new Thread(new Runnable(){
        public void run() {
            System.out.println("Writer started...");
            while(!socket.isClosed()) {
                try {
                    BufferedReader br1  = new BufferedReader(new InputStreamReader(System.in));
                        // System.out.println("Server: ");
                        String content = br1.readLine();
                        
                        out.println(content);
                        out.flush();

                        if("End".equals(content)) {
                            socket.close();
                            System.out.println("Writer stoped...");
                            break; 
                        }
                        
                        
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
        }});
        t2.start();  
    }

    public static void main(String[] args) {
        System.out.println("This is server..going to start server"); 
        new Server();
    }
}