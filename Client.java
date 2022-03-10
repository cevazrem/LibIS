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
        private static JFrame           jfrm;
        private final JPanel            panel;
        private final JTable            table;
        private final JTableHeader      header;
        private final DefaultTableModel tableModel;

        TFDemo() {
            //constructor for start form
            jfrm = new JFrame("LibIS");
            //jfrm.setLayout(new GridLayout());
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

            socket.send_message("DrawAll");
            String word = socket.recieve_message();
            panel = new JPanel();
            String[] col = {"Client_fio", "date_start", "Book_name", "Author_fio"};
            tableModel = new DefaultTableModel(ParsJSON(word, 0), col);
            table = new JTable(tableModel);

            header = table.getTableHeader();

            header.setBackground(Color.yellow);
            JScrollPane pane = new JScrollPane(table);
            //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            jbtntest1.setBounds(width / 16 - 75, height / 4 - 25, 150, 50);
            jbtntest2.setBounds(width * 3 / 16 - 75,height / 4 - 25, 150, 50);
            jbtntest3.setBounds(width / 16 - 75, height / 2 - 25, 150, 50);
            jbtntest4.setBounds(width * 3 / 16 - 75,height / 2 - 25, 150, 50);
            jbtntest5.setBounds(width / 16 - 75, height * 3 / 4 - 25, 150, 50);
            jbtntest6.setBounds(width * 3 / 16 - 75,height * 3/ 4 - 25, 150, 50);

            pane.setPreferredSize(new Dimension(width/2, height*9/10));
            //add buttons to window
            jfrm.add(jbtntest1);
            jfrm.add(jbtntest2);
            jfrm.add(jbtntest3);
            jfrm.add(jbtntest4);
            jfrm.add(jbtntest5);
            jfrm.add(jbtntest6);

            panel.add(pane);
            jfrm.add(panel);

            jfrm.setVisible(true);
        }

        //JSON parser
        public String[][] ParsJSON(String data, int mode) {
            String tmp = data;
            int size_m = (tmp.length() - tmp.replace("},{", "").length()) / 3 + 1;
            //System.out.println(size_m);
            tmp = data;
            int size_n = (tmp.length() - tmp.replace("\":\"", "").length()) / 3 / size_m;
            //System.out.println(size_n);
            StringBuilder value = new StringBuilder();
            String[][] result;
            if (mode == 0) {
                result = new String[size_m][size_n];
            } else {
                result = new String[size_n][size_m];
            }
            int col_pos = 0;
            int row_pos = 0;
            char[] chars = data.toCharArray();
            for (int i = 0; i < data.length(); i++) {
                if (chars[i] == '\"' && chars[i - 1] == ':') {
                    while (chars[i + 1] != '\"') {
                        value.append(chars[i + 1]);
                        i++;
                    }
                    if (mode == 0) {
                        result[row_pos][col_pos] = value.toString();
                    } else {
                        result[col_pos][row_pos] = value.toString();
                    }
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
            private final JButton           insert_cl;
            private final JButton           refresh_cl;
            private final JButton           delete_cl;
            private final DefaultTableModel tableModel;
            private JDialog                 insert_dialog;
            private JDialog                 dialog;
            private JTextField              text;
            private JLabel                  label;
            private JTextField              text_fio;
            private JTextField              text_birth_date;
            private JTextField              text_phone;

            private String[] get_col() {
                String[] col = {"Id", "FIO", "Date_birth", "Date_reg", "Date_end", "Phone"};
                return col;
            }

            private String[][] get_data(String word, int mode) {
                return ParsJSON(word, mode);
            }

            DrawClients() {
                socket.send_message("DrawClient");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word, 0);

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
                insert_cl = new JButton("insert");
                insert_cl.addActionListener(this);

                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_cl);
                frame.add(delete_cl);
                frame.add(insert_cl);
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
                String[][] data = get_data(word, 0);

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

            private void insert_form() {
                insert_dialog = new JDialog(frame);
                insert_dialog.setSize(250, 250);
                JPanel p = new JPanel();
                JLabel fio_label = new JLabel("Введите фио клиента:");
                JLabel birth_label = new JLabel("Введите дату рождения клиента:");
                JLabel phone_label = new JLabel("Введите телефон клиента:");
                text_fio = new JTextField(20);
                text_birth_date = new JTextField(20);
                text_phone = new JTextField(20);
                JButton enter = new JButton("ok");
                enter.setActionCommand("insert_ok");
                enter.addActionListener(this);
                insert_dialog.setAlwaysOnTop(true);
                p.add(fio_label);
                p.add(text_fio);
                p.add(birth_label);
                p.add(text_birth_date);
                p.add(phone_label);
                p.add(text_phone);
                p.add(enter);
                insert_dialog.add(p);
                insert_dialog.setVisible(true);
            }

            private void insert_client() {
                String fio = text_fio.getText();
                String birth_date = text_birth_date.getText();
                String phone = text_phone.getText();
                socket.send_message("InsertClients_" + fio + "_" + birth_date + "_" + phone);
                text_fio.setText("");
                text_birth_date.setText("");
                text_phone.setText("");
                String word = socket.recieve_message();
                if (word.equals("OK")) {
                    insert_dialog.setVisible(false);
                } else {
                    
                }
            }


            public void actionPerformed(ActionEvent ae) {
                System.out.println(ae.getActionCommand());
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_client();
                } else if(ae.getActionCommand().equals("close")) {
                    close_client_form();
                } else if(ae.getActionCommand().equals("close_ok")) {
                    close_client();
                } else if (ae.getActionCommand().equals("insert")) {
                    insert_form();
                } else if(ae.getActionCommand().equals("insert_ok")) {
                    insert_client();
                }
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

            private String[][] get_data(String word, int mode) {
                return ParsJSON(word, mode);
            }

            DrawTickets() {
                socket.send_message("DrawTickets");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word, 0);

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
                String[][] data = get_data(word, 0);

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

            private String[][] get_data(String word, int mode) {
                return ParsJSON(word, mode);
            }

            DrawReserves() {
                socket.send_message("DrawReserves");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word, 0);

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
                String[][] data = get_data(word, 0);

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
        private class DrawBooks implements ActionListener  {
            private final JFrame            frame;
            private final JPanel            panel;
            private final JTable            table;
            private final JTableHeader      header;
            private final JButton           refresh_book;
            private final JButton           insert_book;
            private final DefaultTableModel tableModel;

            private JDialog                 insert_dialog;
            private JTextField              text_name;
            private JTextField              text_pub_year;
            private JComboBox               box_authors;
            private JComboBox               box_publishers;

            private String[] get_col() {
                String[] col = {"Id", "Name", "Author", "Publisher", "Pub_year"};
                return col;
            }

            private String[][] get_data(String word, int mode) {
                return ParsJSON(word, mode);
            }

            DrawBooks() {
                socket.send_message("DrawBooks");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word, 0);

                frame = new JFrame("Books");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);
                header = table.getTableHeader();

                refresh_book = new JButton("refresh");
                refresh_book.addActionListener(this);
                insert_book = new JButton("insert");
                insert_book.addActionListener(this);

                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_book);
                frame.add(insert_book);

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
                String[][] data = get_data(word, 0);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }

            private void insert_form() {
                insert_dialog = new JDialog(frame);
                insert_dialog.setSize(280, 280);
                JPanel p = new JPanel();
                JLabel name_label = new JLabel("Введите название книги:");
                JLabel author_label = new JLabel("Автор:");
                JLabel publisher_label = new JLabel("Издатель:");
                JLabel pub_year_label = new JLabel("Год издания:");
                text_name = new JTextField(20);
                text_pub_year = new JTextField(20);
                JButton enter = new JButton("ok");
                enter.setActionCommand("insert_ok");
                enter.addActionListener(this);
                insert_dialog.setAlwaysOnTop(true);

                socket.send_message("GetAuthors");
                String word = socket.recieve_message();
                String[][] data = get_data(word, 1);

                box_authors = new JComboBox(data[1]);

                socket.send_message("GetPublishers");
                word = socket.recieve_message();
                data = get_data(word, 1);

                box_publishers = new JComboBox(data[1]);

                p.add(name_label);
                p.add(text_name);
                p.add(author_label);
                p.add(box_authors);
                p.add(publisher_label);
                p.add(box_publishers);
                p.add(pub_year_label);
                p.add(text_pub_year);
                p.add(enter);
                insert_dialog.add(p);
                insert_dialog.setVisible(true);
            }

            private void insert_book() {
                String name = text_name.getText();
                String author = box_authors.getSelectedItem().toString();
                String publisher = box_publishers.getSelectedItem().toString();
                String pub_year = text_pub_year.getText();
                socket.send_message("InsertBooks_" + name + "_" + author + "_" + publisher + "_" + pub_year);
                text_name.setText("");
                text_pub_year.setText("");
                String word = socket.recieve_message();
                if (word.equals("OK")) {
                    insert_dialog.setVisible(false);
                } else {

                }
            }
            public void actionPerformed(ActionEvent ae) {
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_book();
                } else if (ae.getActionCommand().equals("insert")) {
                    insert_form();
                } else if(ae.getActionCommand().equals("insert_ok")) {
                    insert_book();
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
            private final JButton           insert_author;
            private final DefaultTableModel tableModel;

            private JDialog                 insert_dialog;
            private JTextField              text_fio;
            private JTextField              text_date_birth;
            private JTextField              text_country_birth;

            private String[] get_col() {
                String[] col = {"Id", "FIO", "date_birth", "country_birth"};
                return col;
            }

            private String[][] get_data(String word, int mode) {
                return ParsJSON(word, mode);
            }
            DrawAuthors() {
                socket.send_message("DrawAuthors");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word, 0);

                frame = new JFrame("Authors");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);
                header = table.getTableHeader();

                refresh_author = new JButton("refresh");
                refresh_author.addActionListener(this);
                insert_author = new JButton("insert");
                insert_author.addActionListener(this);

                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_author);
                frame.add(insert_author);

                table.getColumnModel().getColumn(0).setWidth(20);
                table.getColumnModel().getColumn(1).setWidth(100);
                table.getColumnModel().getColumn(2).setWidth(100);
                table.getColumnModel().getColumn(3).setWidth(100);

                frame.setSize(500, 500);
                frame.setUndecorated(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                frame.setVisible(true);
            }

            private void refresh_author() {
                socket.send_message("RefreshAuthors");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word, 0);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }

            private void insert_form() {
                insert_dialog = new JDialog(frame);
                insert_dialog.setSize(250, 250);
                JPanel p = new JPanel();
                JLabel fio_label = new JLabel("Введите фио писателя:");
                JLabel birth_date_label = new JLabel("Введите дату рождения писателя:");
                JLabel birth_country_label = new JLabel("Введите страну рождения писателя:");
                text_fio = new JTextField(20);
                text_date_birth = new JTextField(20);
                text_country_birth = new JTextField(20);
                JButton enter = new JButton("ok");
                enter.setActionCommand("insert_ok");
                enter.addActionListener(this);
                insert_dialog.setAlwaysOnTop(true);
                p.add(fio_label);
                p.add(text_fio);
                p.add(birth_date_label);
                p.add(text_date_birth);
                p.add(birth_country_label);
                p.add(text_country_birth);
                p.add(enter);
                insert_dialog.add(p);
                insert_dialog.setVisible(true);
            }

            private void insert_author() {
                String fio = text_fio.getText();
                String birth_date = text_date_birth.getText();
                String birth_country = text_country_birth.getText();
                socket.send_message("InsertAuthors_" + fio + "_" + birth_date + "_" + birth_country);
                text_fio.setText("");
                text_date_birth.setText("");
                text_country_birth.setText("");
                String word = socket.recieve_message();
                if (word.equals("OK")) {
                    insert_dialog.setVisible(false);
                } else {

                }
            }

            public void actionPerformed(ActionEvent ae) {
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_author();
                } else if (ae.getActionCommand().equals("insert")) {
                    insert_form();
                } else if(ae.getActionCommand().equals("insert_ok")) {
                    insert_author();
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
            private final JButton           insert_pub;
            private final DefaultTableModel tableModel;

            private JDialog                 insert_dialog;
            private JTextField              text_name;
            private JTextField              text_address;

            private String[] get_col() {
                String[] col = {"Id", "Name", "Address"};
                return col;
            }

            private String[][] get_data(String word, int mode) {
                return ParsJSON(word, mode);
            }

            DrawPublishers() {
                socket.send_message("DrawPublishers");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word, 0);

                frame = new JFrame("Publishers");
                frame.setLayout(new FlowLayout());
                panel = new JPanel();
                tableModel = new DefaultTableModel(data, col);
                table = new JTable(tableModel);
                header = table.getTableHeader();

                refresh_pub = new JButton("refresh");
                refresh_pub.addActionListener(this);
                insert_pub = new JButton("insert");
                insert_pub.addActionListener(this);

                header.setBackground(Color.yellow);
                JScrollPane pane = new JScrollPane(table);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                panel.add(pane);
                frame.add(panel);
                frame.add(refresh_pub);
                frame.add(insert_pub);

                table.getColumnModel().getColumn(0).setWidth(20);
                table.getColumnModel().getColumn(1).setWidth(100);
                table.getColumnModel().getColumn(2).setWidth(100);

                frame.setSize(500, 500);
                frame.setUndecorated(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                frame.setVisible(true);
            }

            private void refresh_pub() {
                socket.send_message("RefreshPublishers");
                String word = socket.recieve_message();

                String[] col = get_col();
                String[][] data = get_data(word, 0);

                tableModel.setRowCount(0);
                for (int i = 0; i < data.length; i++) {
                    tableModel.addRow(data[i]);
                }
                tableModel.fireTableDataChanged();
            }

            private void insert_form() {
                insert_dialog = new JDialog(frame);
                insert_dialog.setSize(250, 250);
                JPanel p = new JPanel();
                JLabel name_label = new JLabel("Введите название издателя:");
                JLabel address_label = new JLabel("Введите адрес издателя:");
                text_name = new JTextField(20);
                text_address = new JTextField(20);
                JButton enter = new JButton("ok");
                enter.setActionCommand("insert_ok");
                enter.addActionListener(this);
                insert_dialog.setAlwaysOnTop(true);
                p.add(name_label);
                p.add(text_name);
                p.add(address_label);
                p.add(text_address);
                p.add(enter);
                insert_dialog.add(p);
                insert_dialog.setVisible(true);
            }

            private void insert_pub() {
                String fio = text_name.getText();
                String birth_date = text_address.getText();
                socket.send_message("InsertPublishers_" + fio + "_" + birth_date);
                text_name.setText("");
                text_address.setText("");
                String word = socket.recieve_message();
                if (word.equals("OK")) {
                    insert_dialog.setVisible(false);
                } else {

                }
            }

            public void actionPerformed(ActionEvent ae) {
                if(ae.getActionCommand().equals("refresh")) {
                    refresh_pub();
                } else if (ae.getActionCommand().equals("insert")) {
                    insert_form();
                } else if(ae.getActionCommand().equals("insert_ok")) {
                    insert_pub();
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