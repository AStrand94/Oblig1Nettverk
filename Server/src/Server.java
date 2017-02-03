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
    static ArrayList<User> onlineUsers = new ArrayList<>();
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
                        if (availableChats()){

                            out.println("*ui*" + onlineUsers());

                            String chat = in.readLine();
                            if (chat.equals("new")) putInNewChat(client);
                            for (ChatServer cs : chatServers) {
                                if (cs.getUsername().equals(chat)) cs.addClient(client);
                                else continue;
                                cs.start();
                            }
                        }else{
                            out.println("No chats available, client put in empty chat");
                            putInNewChat(client);
                        }

                        connected = true;
                        System.out.println("Done connecting");
                    }

                } catch (IOException e) {
                    System.out.println("Connection timed out with client, waiting for new client");

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
        while (!inChat) {
            try {
                String chat = client.in.readLine();
                if (chat.equals("new")){
                    putInNewChat(client);
                    inChat = true;
                } else {
                    for (ChatServer cs : chatServers) {
                        if (cs.getUsername().equals(chat)) {
                            cs.addClient(client);
                            cs.start();
                            inChat = true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
