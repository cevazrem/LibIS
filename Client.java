import java.io.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
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
            StringBuilder value = new StringBuilder();
            String[][] result = new String[size_m][size_n] ;
            int col_pos = 0;
            int row_pos = 0;
            char[] chars = data.toCharArray();
            for (int i = 0; i < data.length(); i++) {
                if (chars[i] == '\"' && chars[i - 1] == ':') {
                    while (chars[i + 1] != '\"') {
                        value.append(chars[i + 1]);
                        i++;
                    }
                    result[row_pos][col_pos] = value.toString();
                    value = new StringBuilder();
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
        private class DrawClients implements ActionListener{
            private final JFrame            frame;
            private final JPanel            panel;
            private final JTable            table;
            private final JTableHeader      header;
            private final JButton           refresh_cl;
            private final JButton           delete_cl;
            private final DefaultTableModel tableModel;
            private JDialog                 dialog;
            private JTextField              text;
            private JLabel                  label;
            private String[] get_col() {
                String[] col = {"Id", "FIO", "Date_birth", "Date_reg", "Date_end", "Phone"};
                return col;
            }

            private String[][] get_data(String word) {
                return ParsJSON(word);
            }

            DrawClients() {
                socket.send_message("DrawClient");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                frame = new JFrame("Clients");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);

                header = table.getTableHeader();
                refresh_cl = new JButton("refresh");
                refresh_cl.addActionListener(this);
                delete_cl = new JButton("close");
                delete_cl.addActionListener(this);


                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_cl);
                frame.add(delete_cl);
                //frame.setLocationRelativeTo(parent);

                table.getColumnModel().getColumn(0).setWidth(20);
                table.getColumnModel().getColumn(1).setWidth(150);
                table.getColumnModel().getColumn(2).setWidth(50);
                table.getColumnModel().getColumn(3).setWidth(50);
                table.getColumnModel().getColumn(4).setWidth(80);

                frame.setSize(500, 500);
                frame.setUndecorated(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                frame.setVisible(true);
            }
            private void refresh_client() {
                socket.send_message("RefreshClients");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }
            private void close_client_form() {
                dialog = new JDialog(frame);
                dialog.setSize(200, 200);
                JPanel p = new JPanel();
                label = new JLabel("Enter id:");
                text = new JTextField(16);
                JButton enter = new JButton("ok");
                enter.setActionCommand("close_ok");
                enter.addActionListener(this);
                dialog.setAlwaysOnTop(true);
                p.add(label);
                p.add(text);
                p.add(enter);
                dialog.add(p);
                dialog.setVisible(true);
                //socket.send_message("DeleteRow");
            }
            private void close_client() {
                int id;
                boolean is_real = false;
                try {
                    id = Integer.parseInt(text.getText());
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        System.out.println(tableModel.getValueAt(i, 0));
                        if (Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 0))) == id) {
                            is_real = true;
                        }
                    }
                    if (!is_real) {
                        throw new Exception("Bad id");
                    }
                    socket.send_message("CloseClient_" + text.getText());
                    String word = socket.recieve_message();
                    System.out.println(word);
                    if (word.equals("Already closed")) {
                        throw new Exception("Already closed");
                    } else if (!word.equals("OK")){
                        throw new Exception("Database error");
                    }
                    text.setText("");
                    dialog.setVisible(false);
                } catch (NumberFormatException e) {
                    label.setText("Id must be a number!");
                    text.setText("");
                } catch (Exception e) {
                    if (e.getMessage().equals("Bad id")) {
                        label.setText("Wrong id!");
                        text.setText("");
                    } else if (e.getMessage().equals("Database error")) {
                        label.setText("DB error. ID not found");
                        text.setText("");
                    } else if (e.getMessage().equals("Already closed")) {
                        label.setText("Already close!");
                        text.setText("");
                    }
                }
            }
            public void actionPerformed(ActionEvent ae) {
                System.out.println(ae.getActionCommand());
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_client();
                } else if(ae.getActionCommand().equals("close")) {
                    close_client_form();
                } else if(ae.getActionCommand().equals("close_ok")) {
                    close_client();}
            }
        }
        //Action after push button Tickets
        private class DrawTickets implements ActionListener {
            private final JFrame            frame;
            private final JPanel            panel;
            private final JTable            table;
            private final JTableHeader      header;
            private final JButton           refresh_tick;
            private final JButton           delete_tick;
//            private final JButton           insert_tick;
            private final DefaultTableModel tableModel;
            private JDialog                 dialog;
            private JTextField              text;
 //           private JTextField              text_id;
 //           private JTextField              text_date_start;
//            private JTextField              text_date_end;
            private JLabel                  label;
            private String[] get_col() {
                String[] col = {"Id", "Client", "Date_Start", "Date_end"};
                return col;
            }

            private String[][] get_data(String word) {
                return ParsJSON(word);
            }

            DrawTickets() {
                socket.send_message("DrawTickets");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                frame = new JFrame("Tickets");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);

                header = table.getTableHeader();
                refresh_tick = new JButton("refresh");
                refresh_tick.addActionListener(this);
                delete_tick = new JButton("close");
                delete_tick.addActionListener(this);
//                insert_tick = new JButton("insert");
//                insert_tick.addActionListener(this);
                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_tick);
                frame.add(delete_tick);
//                frame.add(insert_tick);

                table.getColumnModel().getColumn(0).setWidth(20);
                table.getColumnModel().getColumn(1).setWidth(20);
                table.getColumnModel().getColumn(2).setWidth(50);
                table.getColumnModel().getColumn(3).setWidth(50);

                frame.setSize(500, 500);
                frame.setUndecorated(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                frame.setVisible(true);
            }
            private void refresh_ticket() {
                socket.send_message("RefreshTickets");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }
            private void close_ticket_form() {
                dialog = new JDialog(frame);
                dialog.setSize(200, 200);
                JPanel p = new JPanel();
                label = new JLabel("Enter id:");
                text = new JTextField(16);
                JButton enter = new JButton("ok");
                enter.setActionCommand("close_ok");
                enter.addActionListener(this);
                dialog.setAlwaysOnTop(true);
                p.add(label);
                p.add(text);
                p.add(enter);
                dialog.add(p);
                dialog.setVisible(true);
                //socket.send_message("DeleteRow");
            }
            private void close_ticket() {
                int id;
                boolean is_real = false;
                try {
                    id = Integer.parseInt(text.getText());
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        System.out.println(tableModel.getValueAt(i, 0));
                        if (Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 0))) == id) {
                            is_real = true;
                        }
                    }
                    if (!is_real) {
                        throw new Exception("Bad id");
                    }
                    socket.send_message("CloseTicket_" + text.getText());
                    String word = socket.recieve_message();
                    System.out.println(word);
                    if (word.equals("Already closed")) {
                        throw new Exception("Already closed");
                    } else if (!word.equals("OK")){
                        throw new Exception("Database error");
                    }
                    text.setText("");
                    dialog.setVisible(false);
                } catch (NumberFormatException e) {
                    label.setText("Id must be a number!");
                    text.setText("");
                } catch (Exception e) {
                    if (e.getMessage().equals("Bad id")) {
                        label.setText("Wrong id!");
                        text.setText("");
                    } else if (e.getMessage().equals("Database error")) {
                        label.setText("DB error. ID not found");
                        text.setText("");
                    } else if (e.getMessage().equals("Already closed")) {
                        label.setText("Already close!");
                        text.setText("");
                    }
                }
            }
 /*           private void insert_form() {
                dialog = new JDialog(frame);
                dialog.setSize(400, 400);
                JPanel p = new JPanel();
                label = new JLabel("Enter values:");
                text_id = new JTextField(16);
                text_date_start = new JTextField(16);
                text_date_end = new JTextField(16);
                JButton enter = new JButton("ok");
                enter.setActionCommand("close_ok");
                enter.addActionListener(this);
                dialog.setAlwaysOnTop(true);
                p.add(label);
                p.add(text_id);
                p.add(text_date_start);
                p.add(text_date_end);
                p.add(enter);
                dialog.add(p);
                dialog.setVisible(true);
                //socket.send_message("DeleteRow");
            }

            private void insert_ticket() {
                int id;
                boolean is_real = false;
                try {
                    id = Integer.parseInt(text_id.getText());
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        System.out.println(tableModel.getValueAt(i, 0));
                        if (Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 0))) == id) {
                            is_real = true;
                        }
                    }
                    if (!is_real) {
                        throw new Exception("Bad id");
                    }



                    socket.send_message("CloseTicket_" + text_id.getText());
                    String word = socket.recieve_message();
                    System.out.println(word);
                    if (word.equals("Already closed")) {
                        throw new Exception("Already closed");
                    } else if (!word.equals("OK")){
                        throw new Exception("Database error");
                    }
                    text_id.setText("");
                    dialog.setVisible(false);
                } catch (NumberFormatException e) {
                    label.setText("Id must be a number!");
                    text_id.setText("");
                } catch (Exception e) {
                    if (e.getMessage().equals("Bad id")) {
                        label.setText("Wrong id!");
                        text_id.setText("");
                    } else if (e.getMessage().equals("Database error")) {
                        label.setText("DB error. ID not found");
                        text_id.setText("");
                    } else if (e.getMessage().equals("Already closed")) {
                        label.setText("Already close!");
                        text_id.setText("");
                    }
                }
            }
*/
            public void actionPerformed(ActionEvent ae) {
                System.out.println(ae.getActionCommand());
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_ticket();
                } else if(ae.getActionCommand().equals("close")) {
                    close_ticket_form();
                } else if(ae.getActionCommand().equals("close_ok")) {
                    close_ticket();
/*                } else if(ae.getActionCommand().equals("insert")) {
                    insert_form();
                } else if(ae.getActionCommand().equals("insert_ok")) {
                    insert_ticket();
 */               }
            }
        }

        //Action after push button Reserves
        private class DrawReserves implements ActionListener {
            private final JFrame            frame;
            private final JPanel            panel;
            private final JTable            table;
            private final JTableHeader      header;
            private final JButton           refresh_res;
            private final JButton           delete_res;
            private final DefaultTableModel tableModel;
            private JDialog                 dialog;
            private JTextField              text;
            private JLabel                  label;

            private String[] get_col() {
                String[] col = {"Id", "Ticket", "Book"};
                return col;
            }

            private String[][] get_data(String word) {
                return ParsJSON(word);
            }

            DrawReserves() {
                socket.send_message("DrawReserves");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                frame = new JFrame("Reserves");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);
                header = table.getTableHeader();

                refresh_res = new JButton("refresh");
                refresh_res.addActionListener(this);
                delete_res = new JButton("close");
                delete_res.addActionListener(this);

                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_res);
                frame.add(delete_res);

                table.getColumnModel().getColumn(0).setWidth(20);
                table.getColumnModel().getColumn(1).setWidth(50);
                table.getColumnModel().getColumn(2).setWidth(50);

                frame.setSize(500, 500);
                frame.setUndecorated(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                frame.setVisible(true);
            }

            private void refresh_reserve() {
                socket.send_message("RefreshReserves");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }

            private void close_reserve_form() {
                dialog = new JDialog(frame);
                dialog.setSize(200, 200);
                JPanel p = new JPanel();
                label = new JLabel("Enter id:");
                text = new JTextField(16);
                JButton enter = new JButton("ok");
                enter.setActionCommand("close_ok");
                enter.addActionListener(this);
                dialog.setAlwaysOnTop(true);
                p.add(label);
                p.add(text);
                p.add(enter);
                dialog.add(p);
                dialog.setVisible(true);
                //socket.send_message("DeleteRow");
            }
            private void close_reserve() {
                int id;
                boolean is_real = false;
                try {
                    id = Integer.parseInt(text.getText());
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        System.out.println(tableModel.getValueAt(i, 0));
                        if (Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 0))) == id) {
                            is_real = true;
                        }
                    }
                    if (!is_real) {
                        throw new Exception("Bad id");
                    }
                    socket.send_message("CloseReserves_" + text.getText());
                    String word = socket.recieve_message();
                    System.out.println(word);
                    if (word.equals("Already closed")) {
                        throw new Exception("Already closed");
                    } else if (!word.equals("OK")) {
                        throw new Exception("Database error");
                    }
                    text.setText("");
                    dialog.setVisible(false);
                } catch (NumberFormatException e) {
                    label.setText("Id must be a number!");
                    text.setText("");
                } catch (Exception e) {
                    if (e.getMessage().equals("Bad id")) {
                        label.setText("Wrong id!");
                        text.setText("");
                    } else if (e.getMessage().equals("Database error")) {
                        label.setText("DB error. ID not found");
                        text.setText("");
                    } else if (e.getMessage().equals("Already closed")) {
                        label.setText("Already close!");
                        text.setText("");
                    }
                }
            }
            public void actionPerformed(ActionEvent ae) {
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_reserve();
                } else if(ae.getActionCommand().equals("close")) {
                    close_reserve_form();
                } else if(ae.getActionCommand().equals("close_ok")) {
                    close_reserve();
                }
            }
        }

        //Action after push button Books
        private class DrawBooks implements ActionListener {
            private final JFrame            frame;
            private final JPanel            panel;
            private final JTable            table;
            private final JTableHeader      header;
            private final JButton           refresh_book;
            private final DefaultTableModel tableModel;

            private String[] get_col() {
                String[] col = {"Id", "Name", "Author", "Publisher", "Pub_year"};
                return col;
            }

            private String[][] get_data(String word) {
                return ParsJSON(word);
            }

            DrawBooks() {
                socket.send_message("DrawBooks");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                frame = new JFrame("Books");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);
                header = table.getTableHeader();

                refresh_book = new JButton("refresh");
                refresh_book.addActionListener(this);

                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_book);

                table.getColumnModel().getColumn(0).setWidth(20);
                table.getColumnModel().getColumn(1).setWidth(100);
                table.getColumnModel().getColumn(2).setWidth(20);
                table.getColumnModel().getColumn(3).setWidth(20);
                table.getColumnModel().getColumn(4).setWidth(50);

                frame.setSize(500, 500);
                frame.setUndecorated(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                frame.setVisible(true);
            }

            private void refresh_book() {
                socket.send_message("RefreshBooks");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }

            public void actionPerformed(ActionEvent ae) {
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_book();
                }
            }
        }

        //Action after push button Authors
        private class DrawAuthors implements ActionListener {
            private final JFrame            frame;
            private final JPanel            panel;
            private final JTable            table;
            private final JTableHeader      header;
            private final JButton           refresh_author;
            private final DefaultTableModel tableModel;

            private String[] get_col() {
                String[] col = {"Id", "FIO"};
                return col;
            }

            private String[][] get_data(String word) {
                return ParsJSON(word);
            }
            DrawAuthors() {
                socket.send_message("DrawAuthors");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                frame = new JFrame("Authors");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);
                header = table.getTableHeader();

                refresh_author = new JButton("refresh");
                refresh_author.addActionListener(this);

                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_author);

                table.getColumnModel().getColumn(0).setWidth(20);
                table.getColumnModel().getColumn(1).setWidth(100);

                frame.setSize(500, 500);
                frame.setUndecorated(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                frame.setVisible(true);
            }

            private void refresh_author() {
                socket.send_message("RefreshAuthors");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }

            public void actionPerformed(ActionEvent ae) {
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_author();
                }
            }
        }

        //Action after push button Publishers
        private class DrawPublishers implements ActionListener {
            private final JFrame            frame;
            private final JPanel            panel;
            private final JTable            table;
            private final JTableHeader      header;
            private final JButton           refresh_pub;
            private final DefaultTableModel tableModel;

            private String[] get_col() {
                String[] col = {"Id", "Name", "Address"};
                return col;
            }

            private String[][] get_data(String word) {
                return ParsJSON(word);
            }

            DrawPublishers() {
                socket.send_message("DrawPublishers");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                frame = new JFrame("Publishers");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);
                header = table.getTableHeader();

                refresh_pub = new JButton("refresh");
                refresh_pub.addActionListener(this);

                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_pub);

                table.getColumnModel().getColumn(0).setWidth(20);
                table.getColumnModel().getColumn(1).setWidth(100);

                frame.setSize(500, 500);
                frame.setUndecorated(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                frame.setVisible(true);
            }

            private void refresh_pub() {
                socket.send_message("RefreshPublishers");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }

            public void actionPerformed(ActionEvent ae) {
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_pub();
                }
            }
        }

        //push buttons handler
        public void actionPerformed(ActionEvent ae) {

            if(ae.getActionCommand().equals("clients")) {
                DrawClients exec_cl = new DrawClients();
            } else if (ae.getActionCommand().equals("tickets")) {
                DrawTickets exec_tick = new DrawTickets();
            } else if (ae.getActionCommand().equals("reserves")) {
                DrawReserves exec_res = new DrawReserves();
            } else if (ae.getActionCommand().equals("books")) {
                DrawBooks exec_book = new DrawBooks();
            } else if (ae.getActionCommand().equals("authors")) {
                DrawAuthors exec_auth = new DrawAuthors();
            } else if (ae.getActionCommand().equals("publishers")) {
                DrawPublishers exec_pub = new DrawPublishers();
            }
        }
    }


    public static void main(String[] args) {
        TFDemo execute = new TFDemo();
    }
}