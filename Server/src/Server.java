import sun.misc.ClassFileTransformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
    static ArrayList<User> allUsers = new ArrayList<>();
    static ArrayList<ChatServer> chatServers = new ArrayList<>();


    /**
     * The main function runs a while(true) loop that awaits for clients to connect
     * to the socket, and then starts a new Thread for the log-in procedure.
     * @param args String[] terminal input
     */
    public static void main(String[] args) {
        allUsers.add(new User("admin", "admin"));
        allUsers.add(new User("stian", "stian"));

        portNumber = 5555; //default

        if (args.length > 1) {
            System.err.println("Wrong input");
            System.exit(1);
        }
        if (args.length == 1) portNumber = Integer.parseInt(args[0]);

        System.out.println("Hi, this is ChatServer");

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {

                System.out.println("Listening for user");

                //Waits for a client to connect
                Socket connect = serverSocket.accept();

                Thread t = logInProcedure(connect);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    System.out.println("TESTESTETSTEST");

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
                        if (isOnline(m.group(1))) continue;;
                        user = getUser(m.group(1));
                        logIn(user);

                    } else { //if user is not a registered user

                        if (!registerUser(m.group(1), m.group(2))) {
                            continue;
                        }
                        user = getUser(m.group(1));
                        logIn(user);
                    }


                    //Done checking user
                    out.println("loginAccept");
                    ServerClient client = new ServerClient(connect, user);

                    putInNewChat(client);
                    connected = true;
                    user.setStatus("available");
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
        u.setStatus("available");
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
            if (u.getUserName().equals(uname) &&
                    u.getPassword().equals(passw))
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
            if (uname.equals(u.getUserName())) return false;
        }
        User u = new User(uname,passw);
        allUsers.add(u);

        return true;
    }

    /**
     * Sets the status of a client to "offline"
     * Takes a ServerClient as parameter
     * @param sc ServerClient
     */
    public static void logOff(ServerClient sc){
        sc.getUser().setStatus("offline");
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
            if (u.getUserName().equals(uname)) return u;
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
     * Puts a client in a new chat. Should only
     * @param client
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

    private static boolean availableChats(){
        for (ChatServer cs : chatServers){
            if (cs.isAvailable()) return true;
        }
        return false;
    }

    static synchronized void putInNewChat(ServerClient client){
        chatServers.add(new ChatServer(client));
    }

    private static String isAvailable(){
        StringBuilder sb = new StringBuilder();
        for (ChatServer cs : chatServers){
            if (cs.isAvailable())
                sb.append("a:").append(cs.getUsername()).append(' ');
        }
        return sb.toString();
    }

    private static String busyUsers(){
        StringBuilder sb = new StringBuilder();
        for (User u : allUsers){
            if (u.getStatus().equals("busy"))
                sb.append("b:").append(u.getUserName()).append(' ');
        }
        return sb.toString();
    }

    private static String onlineUsers(){
        return isAvailable() + busyUsers();
    }

    synchronized static void sendUpdatedUsers(){
        String userInfo = "*ui*" + allUsers();
        for (ChatServer cs : chatServers){
            if (cs.client1 != null) cs.client1.writeMessage(userInfo);
            if (cs.client2 != null) cs.client2.writeMessage(userInfo);
        }
    }

    /**
     * Searches the active chats for a user by the username.
     * The function takes as granted that the input parameter is
     * the username of an online user.
     * @param s
     * @return
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

    public static String allUsers(){
        StringBuilder sb = new StringBuilder();
        for (User u : allUsers){
            char status;
            status = u.getStatus().charAt(0);
            sb.append(status).append(':').append(u.getUserName()).append(' ');
        }
        return sb.toString();
    }

    private static boolean isOnline(String s){
        for (User u : allUsers) {
            if (u.getUserName().equals(s)) {
                if (u.getStatus().equals("available") || u.getStatus().equals("busy")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static void reqChat(ServerClient requester, ServerClient client){
        client.writeMessage("*c?*" + requester.getUsername());
    }

    public static void noChat(String user, String requester){
        ServerClient sc = findServerClient(requester);

        System.out.println("ER SC NULL?: " + (sc == null));

        if (sc == null) return;

        sc.writeMessage('<' + user + " declined your chat request.>");

    }
}
