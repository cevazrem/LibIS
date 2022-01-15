import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
//import oracle.jdbc.driver;

public class Server {

    private static Socket clientSocket; //conv socket
    private static ServerSocket server; // server socket
    private static BufferedReader in; // read pipe
    private static BufferedWriter out; // write pipe

    public static void main(String[] args) {
        try {
            try  {
                server = new ServerSocket(4004); // port 4004
                System.out.println("Server ready");
                System.out.println("Waiting for client...");
                clientSocket = server.accept(); // accept() wait for client connection
                try { 
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // in canal
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); // out canal
                    String word = in.readLine();
                    System.out.println(word);
                    out.write(word + "\n");
                    out.flush(); // pop buffer
                    word = in.readLine();
                    while (!word.equals("stop") && word != null) {
                        System.out.println(word);
                        out.write("Recieved message :" + word + "\n");
                        out.flush(); // pop buffer
                        word = in.readLine();
                    }
                } finally { // close socket
                    clientSocket.close();
                    in.close();
                    out.close();
                }
            } finally {
                System.out.println("Server closed!");
                server.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}