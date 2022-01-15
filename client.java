import java.io.*;
import java.net.Socket;

public class Client {

    private static Socket clientSocket; // client socket
    private static BufferedReader reader; // console reader
    private static BufferedReader in; // in pipe
    private static BufferedWriter out; // out pipe

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", 4004); // port local 4004
                reader = new BufferedReader(new InputStreamReader(System.in)); // console input
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.println("Listen:");
                String word;
                String serverWord;
                do {
                    word = reader.readLine();
                    System.out.println(word);
                    out.write(word + "\n");
                    out.flush();
                    serverWord = in.readLine(); 
                    System.out.println(serverWord);
                } while (!word.equals("stop") && word != null);
            } finally { // close socket
                System.out.println("Client closed...");
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}