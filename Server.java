/*

public class Client {
    private static final String url = "jdbc:mysql://localhost:3306/LIB";
    private static final String user = "root";
    private static final String password = "1234";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public static void main(String[] args) {
        String query = "select id from Books";
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                System.out.printf("id: %d ", id);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { can't do anything  }
            try { stmt.close(); } catch(SQLException se) { can't do anything  }
            try { rs.close(); } catch(SQLException se) { can't do anything  }
        }
    }
}
*/

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//java mysql
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {

    private static Socket clientSocket; //conv socket
    private static ServerSocket server; // server socket
    private static BufferedReader in; // read pipe
    private static BufferedWriter out; // write pipe

    //For MYSQL connection
    private static final String url = "jdbc:mysql://localhost:3306/LIB";
    private static final String user = "root";
    private static final String password = "1234";

    //For DB operations execute
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    private static class DB_handler {
        DB_handler() {
            try {
                con = DriverManager.getConnection(url, user, password);
                stmt = con.createStatement();
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
        }

        public void close_connection() {
            try {
                con.close();
            } catch(SQLException se) {
                System.err.println(se);
            }
        }

        public void close_statement() {
            try {
                stmt.close();
            } catch(SQLException se) {
                System.err.println(se);
            }
        }

        public void clear_res() {
            try {
                rs.close();
            } catch(SQLException se) {
                System.err.println(se);
            }
        }

        public String[][] select_execute(String table) {
            int size = 1;
           // System.out.println(table);
            try {
                if (table.equals("Tickets")) {
                    rs = stmt.executeQuery("SELECT count(id) FROM Tickets");
                    while (rs.next()) {
                        size = rs.getInt(1);
                    }
                    String[][] result = new String[size + 1][4];
                    result[0][0] = "id";
                    result[0][1] = "date_start";
                    result[0][2] = "date_end";
                    result[0][3] = "client";
                    clear_res();
                    rs = stmt.executeQuery("SELECT id, date_start, date_end, client from Tickets");
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String date_start = rs.getString(3);
                        String date_end = rs.getString(3);
                        int client = rs.getInt(4);
                        result[id][0] = Integer.toString(id);
                        result[id][1] = date_start;
                        result[id][2] = date_end;
                        result[id][3] = Integer.toString(client);
                    }
                    return result;
                } else if (table.equals("Reserves")) {
                    rs = stmt.executeQuery("SELECT count(id) FROM Reserves");
                    while (rs.next()) {
                        size = rs.getInt(1);
                    }
                    String[][] result = new String[size + 1][3];
                    result[0][0] = "id";
                    result[0][1] = "ticket";
                    result[0][2] = "book";
                    clear_res();
                    rs = stmt.executeQuery("SELECT id, ticket, book from Reserves");
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        int ticket = rs.getInt(2);
                        int book = rs.getInt(3);
                        result[id][0] = Integer.toString(id);
                        result[id][1] = Integer.toString(ticket);
                        result[id][2] = Integer.toString(book);
                    }
                    return result;
                } else if (table.equals("Books")) {
                    rs = stmt.executeQuery("SELECT count(id) FROM Books");
                    while (rs.next()) {
                        size = rs.getInt(1);
                    }
                    String[][] result = new String[size + 1][5];
                    result[0][0] = "id";
                    result[0][1] = "name";
                    result[0][2] = "author";
                    result[0][3] = "publisher";
                    result[0][4] = "pub_year";
                    clear_res();
                    rs = stmt.executeQuery("SELECT id, name, author, publisher, pub_year from Books");
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String name = rs.getString(2);
                        int author = rs.getInt(3);
                        int publisher = rs.getInt(4);
                        int pub_year = rs.getInt(5);
                        result[id][0] = Integer.toString(id);
                        result[id][1] = name;
                        result[id][2] = Integer.toString(author);
                        result[id][3] = Integer.toString(publisher);
                        result[id][4] = Integer.toString(pub_year);
                    }
                    return result;
                } else if (table.equals("Authors")) {
                    rs = stmt.executeQuery("SELECT count(id) FROM Authors");
                    while (rs.next()) {
                        size = rs.getInt(1);
                    }
                    String[][] result = new String[size + 1][2];
                    result[0][0] = "id";
                    result[0][1] = "fio";
                    clear_res();
                    rs = stmt.executeQuery("SELECT id, fio from Authors");
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String fio = rs.getString(2);
                        result[id][0] = Integer.toString(id);
                        result[id][1] = fio;
                    }
                    return result;
                } else if (table.equals("Publishers")) {
                    rs = stmt.executeQuery("SELECT count(id) FROM Publishers");
                    while (rs.next()) {
                        size = rs.getInt(1);
                    }
                    String[][] result = new String[size + 1][3];
                    result[0][0] = "id";
                    result[0][1] = "name";
                    result[0][2] = "adress";
                    rs = stmt.executeQuery("SELECT id, name, adress from Publishers");
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String name = rs.getString(2);
                        String adress = rs.getString(3);
                        result[id][0] = Integer.toString(id);
                        result[id][1] = name;
                        result[id][2] = adress;
                    }
                    return result;
                } else if (table.equals("Clients")) {
                    rs = stmt.executeQuery("SELECT count(id) FROM Clients");
                    while (rs.next()) {
                        size = rs.getInt(1);
                    }
                    String[][] result = new String[size + 1][6];
                    result[0][0] = "id";
                    result[0][1] = "fio";
                    result[0][2] = "birth_date";
                    result[0][3] = "reg_date";
                    result[0][4] = "end_date";
                    result[0][5] = "phone";
                    rs = stmt.executeQuery("SELECT id, fio, birth_date, reg_date, end_date, phone from Clients");
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String fio = rs.getString(2);
                        String birth_date = rs.getString(3);
                        String reg_date = rs.getString(4);
                        String end_date = rs.getString(5);
                        String phone = rs.getString(6);
                        result[id][0] = Integer.toString(id);
                        result[id][1] = fio;
                        result[id][2] = birth_date;
                        result[id][3] = reg_date;
                        result[id][4] = end_date;
                        result[id][5] = phone;
                    }
                    return result;
                }

            } catch (SQLException se) {
                System.err.println(se);
            }
            return null;
        }
    }

    private static class socket {
        //socket construct
        socket() {
            try {
                server = new ServerSocket(4004);
                System.out.println("Server ready");
                System.out.println("Waiting for client...");
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        public void accept() {
            try {
                clientSocket = server.accept(); // accept() wait for client connection
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                System.out.println("Connection success!");
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        //socket close
        public static void close_socket() {
            try {
                System.out.println("Server closed...");
                server.close();
                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        //send message to client
        public void send_message(String message) {
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        //receive message from client
        public String recieve_message() {
            try {
                return in.readLine();
            } catch (IOException e) {
                return "error";
            }
        }
    }

    public static String create_JSON(String[][] data) {
        String result = "{";
        for(int i = 1; i < data.length; i++) {
            result = result + '{';
            for(int j = 0; j < data[0].length; j++) {
                result = result + '\"' + data[0][j] + '\"' + ':' + '\"' + data[i][j] + '\"';
                if (j != data[0].length - 1) {
                    result = result + ',';
                }
            }
            result = result + '}';
        }
        result = result + '}';

        result = result.replace("}{", "},{");
        //System.out.println(result);
        return result;
    }

    public static void main(String[] args) {
        String[][] data;
        String message;
        try {
            socket socket = new socket(); //to send/receive messages from client
            socket.accept(); // accept() wait for client connection

            DB_handler sql = new DB_handler(); //new database handler


            String word = socket.recieve_message();
            while (!word.equals("stop") && !word.equals("error") && word != null ) {
                if (word.equals("DrawClient")) {
                    data = sql.select_execute("Clients");
                    message = create_JSON(data);
                    socket.send_message(message);
                } else if (word.equals("DrawTickets")) {
                    data = sql.select_execute("Tickets");
                    message = create_JSON(data);
                    socket.send_message(message);
                } else if (word.equals("DrawReserves")) {
                    data = sql.select_execute("Reserves");
                    message = create_JSON(data);
                    socket.send_message(message);
                } else if (word.equals("DrawBooks")) {
                    data = sql.select_execute("Books");
                    message = create_JSON(data);
                    socket.send_message(message);
                } else if (word.equals("DrawAuthors")) {
                    data = sql.select_execute("Authors");
                    message = create_JSON(data);
                    socket.send_message(message);
                } else if (word.equals("DrawPublishers")) {
                    data = sql.select_execute("Publishers");
                    message = create_JSON(data);
                    socket.send_message(message);
                }
                //todo another operations(delete/add/view)
                //System.out.println(word);
                word = socket.recieve_message();
            }
            sql.clear_res();
            sql.close_statement();
            sql.close_connection();
        } finally { // close socket
            socket.close_socket();
        }
    }
}