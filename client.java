import java.io.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.GridLayout;
import java.awt.Color;

//main class
public class Client {

    private static Socket clientSocket; // client socket
    private static BufferedReader reader; // console reader
    private static BufferedReader in; // in canal
    private static BufferedWriter out; // out canal

//class socket   
    private static class socket {
        //socket construct
        socket() {
            try {
                clientSocket = new Socket("localhost", 4004); // port local 4004
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        //socket close
        public void close_socket() {
            try {
                out.write("stop\n");
                out.flush();
                System.out.println("Client closed...");
                clientSocket.close();
                in.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }  
        //send message to server
        public void send_message(String message) {
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException e) {
                System.err.println(e);
            }
        } 
        //receive message from server
        public String recieve_message() {
            try {
                return in.readLine();
            } catch (IOException e) {
                return "error";
            }
        }
    }
    


//drawing form
    private static class TFDemo implements ActionListener {
        socket socket = new socket(); //to send/receive messages from server using form
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        private static JFrame jfrm;
        TFDemo() {
            //constructor for start form
            jfrm = new JFrame("LibIS");   
            jfrm.setLayout(new FlowLayout());
            jfrm.setSize(width, height);
            jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            //buttons to view tables
            JButton jbtntest1 = new JButton("clients");
            JButton jbtntest2 = new JButton("tickets");
            JButton jbtntest3 = new JButton("reserves");
            JButton jbtntest4 = new JButton("books");
            JButton jbtntest5 = new JButton("authors");
            JButton jbtntest6 = new JButton("publishers");

            
            jbtntest1.addActionListener(this);
            jbtntest2.addActionListener(this);
            jbtntest3.addActionListener(this);
            jbtntest4.addActionListener(this);
            jbtntest5.addActionListener(this);
            jbtntest6.addActionListener(this);

            //add buttons to window
            jfrm.add(jbtntest1);
            jfrm.add(jbtntest2);
            jfrm.add(jbtntest3);
            jfrm.add(jbtntest4);
            jfrm.add(jbtntest5);
            jfrm.add(jbtntest6);
           
            jfrm.setVisible(true);
        }
        
        //JSON parser
        public String[][] ParsJSON(String data) {
            String tmp = data;
            int size_m = (tmp.length() - tmp.replace("},{", "").length()) / 3 + 1;
            //System.out.println(size_m);
            tmp = data;
            int size_n = (tmp.length() - tmp.replace("\":\"", "").length()) / 3 / size_m;
            //System.out.println(size_n);
            String value = "";
            String[][] result = new String[size_m][size_n] ;
            int col_pos = 0;
            int row_pos = 0;
            char[] chars = data.toCharArray();
            for (int i = 0; i < data.length(); i++) {
                if (chars[i] == '\"' && chars[i - 1] == ':') {
                    while (chars[i + 1] != '\"') {
                        value = value + chars[i + 1];
                        i++;
                    }
                    result[row_pos][col_pos] = value;
                    value = "";
                    col_pos++;
                }
                if (chars[i] == ',' && chars[i - 1] == '}') {
                    row_pos++;
                    col_pos = 0;
                }
            }
            return result;
        } 

        //Action after push button Clients
        public void DrawClients() {
            String word;
            socket.send_message("DrawClient");
            word = socket.recieve_message();
            //todo json values in data/col
            //String data[][] = parsJSON(word);

            //test json
            String data[][] = ParsJSON(word);
            //test json

            //String data[][] = {{"1","asfasdf","29/02/2001","01/01/2022","","89850550655","1"}};
            String col[] = {"Id","FIO","Birth_date","Reg_date","End_date","Phone","Ticket"};

            JFrame frame = new JFrame("Clients");
            JPanel panel = new JPanel();
            JTable table = new JTable(data,col);
            JTableHeader header = table.getTableHeader();
            header.setBackground(Color.yellow);
            JScrollPane pane = new JScrollPane(table);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            panel.add(pane);
            frame.add(panel);

            table.getColumnModel().getColumn( 0 ).setWidth( 20 );
            table.getColumnModel().getColumn( 1 ).setWidth( 150 );
            table.getColumnModel().getColumn( 2 ).setWidth( 50 );
            table.getColumnModel().getColumn( 3 ).setWidth( 50 );
            table.getColumnModel().getColumn( 4 ).setWidth( 80 );
            table.getColumnModel().getColumn( 5 ).setWidth( 20 );

            frame.setSize(500, 500);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
            frame.setVisible(true);
        }

        //Action after push button Tickets
        public void DrawTickets() {
            String word;
            socket.send_message("DrawTickets");
            word = socket.recieve_message();

            //todo json values in data/col
            //String data[][] = parsJSON(word);

            String data[][] = {{"1","01/01/2022",""}};
            String col[] = {"Id","Date_Start","Date_end"};

            JFrame frame = new JFrame("Tickets");
            JPanel panel = new JPanel();
            JTable table = new JTable(data,col);
            JTableHeader header = table.getTableHeader();
            header.setBackground(Color.yellow);
            JScrollPane pane = new JScrollPane(table);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            panel.add(pane);
            frame.add(panel);

            table.getColumnModel().getColumn( 0 ).setWidth( 20 );
            table.getColumnModel().getColumn( 1 ).setWidth( 50 );
            table.getColumnModel().getColumn( 2 ).setWidth( 50 );

            frame.setSize(500, 500);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
            frame.setVisible(true);
        }

        //Action after push button Reserves
        public void DrawReserves() {
            String word;
            socket.send_message("DrawReserves");
            word = socket.recieve_message();

            //todo json values in data/col
            //String data[][] = parsJSON(word);

            String data[][] =   {
                                    {"1","1","1"},
                                    {"2","1","2"}
                                };
            String col[] = {"Id","Ticket","Book"};

            JFrame frame = new JFrame("Reserves");
            JPanel panel = new JPanel();
            JTable table = new JTable(data,col);
            JTableHeader header = table.getTableHeader();
            header.setBackground(Color.yellow);
            JScrollPane pane = new JScrollPane(table);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            panel.add(pane);
            frame.add(panel);

            table.getColumnModel().getColumn( 0 ).setWidth( 20 );
            table.getColumnModel().getColumn( 1 ).setWidth( 50 );
            table.getColumnModel().getColumn( 2 ).setWidth( 50 );

            frame.setSize(500, 500);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
            frame.setVisible(true);
        }

        //Action after push button Books
        public void DrawBooks() {
            String word;
            socket.send_message("DrawBooks");
            word = socket.recieve_message();

            //todo json values in data/col
            //String data[][] = parsJSON(word);

            String data[][] =   {
                {"1","harry potter","1", "1", "1997"},
                {"2","Lord of the rings","2", "1", "1954"}
            };
            String col[] = {"Id","Name","Author", "Publisher", "Pub_year"};

            JFrame frame = new JFrame("Books");
            JPanel panel = new JPanel();
            JTable table = new JTable(data,col);
            JTableHeader header = table.getTableHeader();
            header.setBackground(Color.yellow);
            JScrollPane pane = new JScrollPane(table);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            panel.add(pane);
            frame.add(panel);

            table.getColumnModel().getColumn( 0 ).setWidth( 20 );
            table.getColumnModel().getColumn( 1 ).setWidth( 100 );
            table.getColumnModel().getColumn( 2 ).setWidth( 20 );
            table.getColumnModel().getColumn( 3 ).setWidth( 20 );
            table.getColumnModel().getColumn( 4 ).setWidth( 50 );

            frame.setSize(500, 500);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
            frame.setVisible(true);
        }

        //Action after push button Authors
        public void DrawAuthors() {
            String word;
            socket.send_message("DrawAuthors");
            word = socket.recieve_message();

            //todo json values in data/col
            //String data[][] = parsJSON(word);

            String data[][] =   {
                {"1","J.K.Rowling"},
                {"2","J.R.R.Tolkin"}
            };
            String col[] = {"Id","FIO"};

            JFrame frame = new JFrame("Authors");
            JPanel panel = new JPanel();
            JTable table = new JTable(data,col);
            JTableHeader header = table.getTableHeader();
            header.setBackground(Color.yellow);
            JScrollPane pane = new JScrollPane(table);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            panel.add(pane);
            frame.add(panel);

            table.getColumnModel().getColumn( 0 ).setWidth( 20 );
            table.getColumnModel().getColumn( 1 ).setWidth( 100 );

            frame.setSize(500, 500);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
            frame.setVisible(true);
        }

        //Action after push button Publishers
        public void DrawPublishers() {
            String word;
            socket.send_message("DrawPublishers");
            word = socket.recieve_message();
        
            //todo json values in data/col
            String data[][] =   {
                {"1","Unknown studio", "Chernogolovka"},
            };
            String col[] = {"Id","Name", "Adress"};

            JFrame frame = new JFrame("Publishers");
            JPanel panel = new JPanel();
            JTable table = new JTable(data,col);
            JTableHeader header = table.getTableHeader();
            header.setBackground(Color.yellow);
            JScrollPane pane = new JScrollPane(table);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            panel.add(pane);
            frame.add(panel);

            table.getColumnModel().getColumn( 0 ).setWidth( 20 );
            table.getColumnModel().getColumn( 1 ).setWidth( 100 );

            frame.setSize(500, 500);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
            frame.setVisible(true);
        }

        //push buttons handler
        public void actionPerformed(ActionEvent ae) {
            
            if(ae.getActionCommand().equals("clients")) {
                DrawClients();
            } else if (ae.getActionCommand().equals("tickets")) {
                DrawTickets();
            } else if (ae.getActionCommand().equals("reserves")) {
                DrawReserves();
            } else if (ae.getActionCommand().equals("books")) {
                DrawBooks();
            } else if (ae.getActionCommand().equals("authors")) {
                DrawAuthors();
            } else if (ae.getActionCommand().equals("publishers")) {
                DrawPublishers();
            }
        }
    }
    
    
    public static void main(String[] args) {
        TFDemo execute = new TFDemo();
    }
}