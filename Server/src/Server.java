import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Server class is a static class that controls all the chats,
 * contains all users in an ArrayList, runs a Thread waiting for users to connect,
 * contains functions to move users between chats.
 */
public class Server {

    static int portNumber;
    static ArrayList<ChatServer> chatServers = new ArrayList<>();
    static ObservableList<User> allUsers = FXCollections.observableArrayList();

    static serverController sc;
    static ServerSocket serverSocket;


    /**
     * This function returns a thread that listens for new clients to connect,
     * and once connected, a new thread starts with that user and the login procedure.
     * Should only be called once.
     *
     * @return Thread - which listens for new users to connect.
     */
    public static Thread startListening(serverController serverController,String port) {

        sc = serverController;
        return new Thread(() -> {

        portNumber = Integer.parseInt(port);

        System.out.println("Hi, this is ChatServer");

        try {
            serverSocket = new ServerSocket(portNumber);
            while (true) {

                System.out.println("Listening for user");

                //Waits for a client to connect
                Socket connect = serverSocket.accept();

                Thread t = logInProcedure(connect);

                t.start();
            }
        }catch (BindException be){
            System.out.println("ERROR WHEN CONNECTING TO PORT " + portNumber);
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("ERROR");
                a.setHeaderText("Permission denied when connecting to portnumber " + portNumber);
                a.showAndWait();
            });
        }catch (IOException e) {
            System.out.println("ServerSocket has been closed");
        }
        });
    }



    /**
     * Returns a Thread that contains a log-in procedure, or to register new client,
     * and then puts the client in a new, empty chat and awaits further instructions
     * from the client.
     * @param connect Socket
     * @return a Thread that contains a log-in procedure
     */
    private static Thread logInProcedure(Socket connect){

        return new Thread(() -> {

            String isUser;
            User user;
            try{
                PrintWriter out =
                        new PrintWriter(connect.getOutputStream(), true); //out to client
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(connect.getInputStream())); //input from client
                boolean connected = false;
                while (!connected) {
                    //Wants 'y' or 'n' from user. 'y' if user is registered, 'n' if needs to register
                    out.println("Are you a registered user? y/n");
                    isUser = in.readLine();

                    out.println("requser");

                    // req username and password with a single space between them
                    Pattern p = Pattern.compile("(.+) (.+)");
                    String up = in.readLine();
                    if (up == null) throw new NullPointerException();
                    Matcher m = p.matcher(up);
                    if (!m.find()) continue;


                    //if client is a user
                    if (isUser.equals("y")) {

                        //if user is not registered
                        if (!checkUser(m.group(1), m.group(2))) {

                            continue;   //continue makes you go on from start in loop again.
                        }
                        if (isOnline(m.group(1))) continue;
                        user = getUser(m.group(1));
                        logIn(user);

                    } else { //if user is not a registered user

                        if (!registerUser(m.group(1), m.group(2))){
                            continue;
                        }
                        user = getUser(m.group(1));
                        if (isOnline(user.getUsername())) continue;
                        logIn(user);
                    }


                    //Done checking user
                    out.println("loginAccept");
                    ServerClient client = new ServerClient(connect, user);

                    putInNewChat(client);
                    connected = true;
                    user.setStatus(new Circle(8,Color.GREEN));
                    sendUpdatedUsers();
                }

            } catch (IOException|NullPointerException e) {
                System.out.println("User disconnected");

            }
        });
    }

    /**
     * Sets the status for a user to "available"
     * @param u User
     */
    private static void logIn(User u){
        u.setStatus(new Circle(8,Color.GRAY));
    }

    /**
     * Checks if a user exists in the server program.
     * @param uname String, username
     * @param passw String, password
     * @return
     */
    public static boolean checkUser(String uname, String passw){

        boolean exists = false;

        for (User u : allUsers){
            if (u.getUsername().equals(uname) &&
                    u.getPassword().equals(passw))
                exists = true;
        }

        return exists;
    }

    public static boolean checkUser(String uname){
        boolean exists = false;

        for (User u : allUsers){
            if (u.getUsername().equals(uname))
                exists = true;
        }

        return exists;
    }

    /**
     * Registers a user by username and password
     * @param uname String, username
     * @param passw String, password
     * @return
     */
    public static boolean registerUser(String uname, String passw){

        for (User u : allUsers){
            if (uname.equals(u.getUsername())) return false;
        }
        Circle stat = new Circle(8,Color.GRAY);
        User u = new User(uname,passw,stat);
        allUsers.add(u);

        return true;
    }

    /**
     * Sets the status of a client to "offline"
     * Takes a ServerClient as parameter
     * @param sc ServerClient
     */
    public static void logOff(ServerClient sc){
        sc.getUser().setStatus(new Circle(8,Color.GRAY));
        sendUpdatedUsers();


    }

    //Needs to check if user exists before calling method

    /**
     * Returns a user. Should check if a user exists first.
     * It throws a NoSuchElementException if the user doe not exist.
     * @param uname String, username
     * @return User
     */
    public static User getUser(String uname){
        for (User u : allUsers){
            if (u.getUsername().equals(uname)) return u;
        }

        throw new NoSuchElementException("No user with username" + uname);
    }

    /**
     * Remoces a chat from existing chats
     * @param cs ChatServer
     */
    public static void endChat(ChatServer cs){
        chatServers.remove(cs);
    }

    /**
     * Puts a client in a new chat. Should only be called if a user wants to
     * request a chat with a new user.
     * @param client ServerClient
     */
    public static void putInChat(ServerClient client){
        client.out.println("*ui*" + onlineUsers() + " new");
        boolean inChat = false;
        while (!inChat) try {
            String chat = client.in.readLine();
            if (chat.length() > 4 && chat.substring(0, 4).equals("*OK*")) {

                chat = chat.substring(4, chat.length());
                for (ChatServer cs : chatServers) {
                    if (cs.getUsername().equals(chat)) {

                        if (cs.isAvailable()) {
                            cs.addClient(client);
                        } else {
                            client.writeMessage("Could not connect to " + chat);
                            putInNewChat(client);
                        }
                        inChat = true;
                        break;
                    }
                }
            } else {
                chatServers.add(new ChatServer(client));
                if (chat.equals("ok")) break;
                ServerClient serverClient;
                boolean req;
                for (ChatServer cs : chatServers) {
                    if (cs.client1.getUsername().equals(chat)) {
                        serverClient = cs.client1;
                        reqChat(client, serverClient);
                        req = true;
                        break;
                    } else if (cs.client2 != null && cs.client2.getUsername().equals(chat)) {
                        serverClient = cs.client2;
                        reqChat(client, serverClient);
                        break;
                    }
                }
                inChat = true;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a ChatServer in the active chatservers List
     * @param client ServerClient
     */
    static synchronized void putInNewChat(ServerClient client){
        chatServers.add(new ChatServer(client));
    }

    /**
     * Returns a string with all the available users
     * @return String
     */
    private static String isAvailable(){
        StringBuilder sb = new StringBuilder();
        for (ChatServer cs : chatServers){
            if (cs.isAvailable())
                sb.append("a:").append(cs.getUsername()).append(' ');
        }
        return sb.toString();
    }

    /**
     * Returns a string with all the busy users.
     * @return String
     */
    private static String busyUsers(){
        StringBuilder sb = new StringBuilder();
        for (User u : allUsers){
            if (u.getStatus().equals(Color.GREY))
                sb.append("b:").append(u.getUsername()).append(' ');
        }
        return sb.toString();
    }

    /**
     * returns a String with all online users (available and busy)
     * @return String
     */
    private static String onlineUsers(){
        return isAvailable() + busyUsers();
    }

    /**
     * Sends an update with the status of all users, to all online users
     */
    synchronized static void sendUpdatedUsers(){
        String userInfo = "*ui*" + allUsers();
        sendToAll(userInfo);
        sc.updateTable();
    }

    /**
     * Sends a message from the server, to all online users
     * @param m String - message to be sent
     */
    static void broadcaster(String m){
        String s = "[SERVER]: " + m;
        sendToAll(s);
        sc.updateTable();
    }

    /**
     * Sends a message to all user
     * @param s String - message to be broadcasted
     */
    private static void sendToAll(String s){
        for (ChatServer cs : chatServers){
            if (cs.client1 != null) cs.client1.writeMessage(s);
            if (cs.client2 != null) cs.client2.writeMessage(s);
        }
        sc.updateTable();
    }

    /**
     * Searches the active chats for a user by the username.
     * The function takes as granted that the input parameter is
     * the username of an online user.
     * @param s String, username
     * @return ServerClient
     */
    private static ServerClient findServerClient(String s){

        for (ChatServer cs : chatServers){

            if (cs.client1.getUsername().equals(s)) return cs.client1;
            else if(cs.client2 != null) {
                if (cs.client2.getUsername().equals(s))
                    return cs.client2;
            }
        }
        return null;
    }

    /**
     * Returns a String with all the users
     * @return String
     */
    public static String allUsers(){
        StringBuilder sb = new StringBuilder();
        for (User u : allUsers) {
            char status;
            if(u.getColor().equals(Color.GREEN)) status = 'a';
            else if(u.getColor().equals(Color.ORANGE)) status ='b';
            else status = 'o';
            sb.append(status).append(':').append(u.getUsername()).append(' ');
       }
        return sb.toString();
    }

    /**
     * Checks if a user is online
     * @param s String, username
     * @return boolean
     */
    private static boolean isOnline(String s){
        for (User u : allUsers) {
            if (u.getUsername().equals(s)) {
                return !u.getColor().equals(Color.GREY);
            }
        }
        return false;
    }

    /**
     * Sends a requestmessage to another user, to chat with that user.
     * @param requester ServerClient
     * @param client ServerClient
     */
    public static void reqChat(ServerClient requester, ServerClient client){
        client.writeMessage("*c?*" + requester.getUsername());
    }

    /**
     * Is called when a user declines a request from another user, to send
     * the descline to that user.
     * @param user String, the user declining
     * @param requester String, the user that requested the chat
     */
    public static void noChat(String user, String requester){
        ServerClient sc = findServerClient(requester);

        if (sc == null) return;

        sc.writeMessage('<' + user + " declined your chat request.>");

    }

    /**
     * Checks if there are any users online
     * @return boolean, true if there are users online, false if no users are online.
     */
    static boolean areUsersOnline(){
        if (chatServers.isEmpty()) return false;

        for (User u : allUsers){
            if (!u.getColor().equals(Color.GREY)) return true;
        }

        return false;
    }

}
