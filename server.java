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

    //todo add function to create JSON

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
                    while (!word.equals("stop") && word != null) {
                        if (word.equals("DrawClient")) {
                            //test
                            out.write("{{\"id\":\"1\",\"fio\":\"test test test\",\"birth_date\":\"29/05/2001\",\"reg_date\":\"01/01/2022\",\"end_date\":\"\",\"phone\":\"89850550655\",\"ticket\":\"1\"},{\"id\":\"2\",\"fio\":\"test\",\"birth_date\":\"29/06/2001\",\"reg_date\":\"01/02/2022\",\"end_date\":\"\",\"phone\":\"89850550655\",\"ticket\":\"2\"}}\n");
                            out.flush(); // pop buffer
                            //test
                            //todo create json db values
                        } else if (word.equals("DrawTickets")) {
                            //todo create json db values
                            out.write("Recieved message" + word +"\n");
                            out.flush(); // pop buffer
                        } else if (word.equals("DrawReserves")) {
                            //todo create json db values
                            out.write("Recieved message" + word +"\n");
                            out.flush(); // pop buffer
                        } else if (word.equals("DrawBooks")) {
                            //todo create json db values
                            out.write("Recieved message" + word +"\n");
                            out.flush(); // pop buffer
                        } else if (word.equals("DrawAuthors")) {
                            //todo create json db values
                            out.write("Recieved message" + word +"\n");
                            out.flush(); // pop buffer
                        } else if (word.equals("DrawPublishers")) {
                            //todo create json db values
                            out.write("Recieved message" + word +"\n");
                            out.flush(); // pop buffer
                        }
                        //todo another operations(delete/add/view)
                        System.out.println(word);
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