import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class Client {

    private static Socket clientSocket; // client socket
    private static BufferedReader reader; // console reader
    private static BufferedReader in; // in canal
    private static BufferedWriter out; // out canal

    public static void draw_form() {
        JFrame a = new JFrame("LibInfoSys");
        JButton b = new JButton("Authorise");
        b.setBounds(40,90,85,20);
        a.add(b);
        a.setSize(1200,900);
        a.setLayout(null);
        a.setVisible(true);
    } 

    public static void main(String[] args) {
        //draw_form();
        try {
            try {
                clientSocket = new Socket("localhost", 4004); // port local 4004
                reader = new BufferedReader(new InputStreamReader(System.in)); // console input
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.println("Listen:");
                String word;
                String serverWord;
                out.write("Connection success!\n");
                out.flush();
                serverWord = in.readLine();
                System.out.println(serverWord);
                do {
                    word = reader.readLine();
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