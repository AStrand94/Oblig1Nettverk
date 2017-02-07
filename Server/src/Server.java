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
 * Created by strand117 on 25.01.2017.
 */
public class Server {

    static int portNumber;
    static ArrayList<User> allUsers = new ArrayList<>();
    //static ArrayList<User> onlineUsers = new ArrayList<>();
    static ArrayList<ChatServer> chatServers = new ArrayList<>();



    public static void main(String[] args) {
        allUsers.add(new User("admin","admin"));
        allUsers.add(new User("stian","stian"));

        portNumber = 5555; //default

        if (args.length > 1) {
            System.err.println("Wrong input");
            System.exit(1);
        }
        if(args.length == 1) portNumber = Integer.parseInt(args[0]);

        System.out.println("Hi, this is ChatServer");

        try(
                ServerSocket serverSocket = new ServerSocket(portNumber)

                ){


            String isUser;
            User user;
            while (true) {

                System.out.println("Listening for user");

                //Waits for a client to connect
                Socket connect = serverSocket.accept();


                System.out.println("client connected");
                PrintWriter out = new PrintWriter(
                        connect.getOutputStream(),true); //out to client
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connect.getInputStream())); //input from client
                boolean connected = false;
                try {
                    while (!connected) {

                        //Wants 'y' or 'n' from user. 'y' if user is registered, 'n' if needs to register
                        out.println("Are you a registered user? y/n");
                        isUser = in.readLine();
                        System.out.println(isUser);

                        out.println("requser");

                        // req username and password with a single space between them
                        Pattern p = Pattern.compile("(.+) (.+)");
                        String up = in.readLine();
                        Matcher m = p.matcher(up);
                        if (!m.find()) continue;


                        //if client is a user
                        if (isUser.equals("y")) {

                            //if user is not registered
                            if (!checkUser(m.group(1), m.group(2))) {

                                continue;   //continue makes you go on from start in loop again.
                            }

                            user = getUser(m.group(1));
                            logIn(user);

                        } else { //if user is not a registered user

                            if (!registerUser(m.group(1),m.group(2))) {
                                continue;
                            }
                            user = getUser(m.group(1));
                            logIn(user);
                        }


                        //Done checking user
                        out.println("loginAccept");
                        ServerClient client = new ServerClient(connect,user);
                        //looking for available chats
                        out.println("*ui*" + onlineUsers());
                        /*
                        if (availableChats()){

                            String chat = in.readLine();
                            reqChat(client,findServerClient(chat));
                            putInNewChat(client);
                        }else{
                            putInNewChat(client);
                        }
                        */

                        putInNewChat(client);
                        connected = true;
                        System.out.println("Done connecting");
                        sendUpdatedUsers();
                    }

                } catch (IOException e) {
                    System.out.println("Connection timed out with client, waiting for new client");

                }catch(NullPointerException npe){
                    npe.printStackTrace();
                    System.out.println("User disconnected");
                }
            }

        }catch (IOException ioe){
            System.out.println("Exception ocurred when listening to port"
                    + portNumber + "or listening to connection");
        }

    }

    private boolean logInUser(String uname){
        for (User u : allUsers)
            if (uname.equals(u.getUserName())){
                u.setStatus("online");
                return true;
            }

        return false;
    }

    public static boolean checkUser(String uname, String passw){

        System.out.println("test");
        boolean exists = false;

        int i = 0;
        for (User u : allUsers){
            System.out.println(i++);
            if (u.getUserName().equals(uname) &&
                    u.getPassword().equals(passw))
                exists = true;
        }

        return exists;
    }

    public static boolean registerUser(String uname, String passw){

        for (User u : allUsers){
            if (uname.equals(u.getUserName())) return false;
        }
        User u = new User(uname,passw);
        allUsers.add(u);

        return true;

    }

    public static void logIn(User u){
        u.setStatus("online");
    }

    public static void logOff(User u){
        u.setStatus("offline");
    }

    //Needs to check if user exists before calling method
    public static User getUser(String uname){
        for (User u : allUsers){
            if (u.getUserName().equals(uname)) return u;
        }

        throw new NoSuchElementException("No user with username" + uname);
    }

    public static void endChat(ChatServer cs){
        chatServers.remove(cs);
    }

    public static void putInChat(ServerClient client){
        client.out.println("*ui*" + onlineUsers() + " new");
        boolean inChat = false;
        while (!inChat) try {
            String chat = client.in.readLine();
            System.out.println("User trying to reconnect, chat: " + chat);


            if (chat.substring(0, 4).equals("*OK*")) {
                System.out.println("STRING IS *OK*");
                chat = chat.substring(4, chat.length());
                for (ChatServer cs : chatServers) {
                    if (cs.getUsername().equals(chat)) {
                        System.out.println("CHATSERVER IS :" + cs.isAvailable());
                        if (cs.isAvailable()) {
                            cs.addClient(client);
                            cs.start(); //Denne skal vel fjernes senere?
                        } else {
                            client.writeMessage("Could not connect to " + chat);
                            chatServers.add(new ChatServer(client));
                        }
                        inChat = true;
                        break;
                    }
                }
            } else {
                chatServers.add(new ChatServer(client));
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

    private static void putInNewChat(ServerClient client){
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

    protected static void sendUpdatedUsers(){
        String userInfo = "*ui*" + onlineUsers();
        for (ChatServer cs : chatServers){
            cs.client1.writeMessage(userInfo);
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
            else if(cs.client2.getUsername() != null)
                if (cs.client2.getUsername().equals(s))
                    return cs.client2;

        }
        return null;
    }

    public static void reqChat(ServerClient requester, ServerClient client){
        client.writeMessage("*c?*" + requester.getUsername());
    }
}
