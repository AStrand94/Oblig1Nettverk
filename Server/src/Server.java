import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by strand117 on 25.01.2017.
 */
public class Server {

    static int portNumber;
    static ArrayList<User> allUsers = new ArrayList<>();
    static ArrayList<User> onlineUsers = new ArrayList<>();


    public static void main(String[] args) {

        portNumber = 5555; //default

        if (args.length > 1) {
            System.err.println("Wrong input"); //fiks tekst senere
            System.exit(1);
        }
        if(args.length == 1) portNumber = Integer.parseInt(args[0]);

        System.out.println("Hi, this is ChatServer");

        try(
                ServerSocket serverSocket = new ServerSocket(portNumber);

                ){


            String text;
            while (true){


                Socket connect = serverSocket.accept(); //Waits for a client to connect
                PrintWriter out = new PrintWriter(connect.getOutputStream()); //out to client
                BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream())); //input from client

                out.println();

                //Wants 'y' or 'n' from user. 'y' if user is registered, 'n' if needs to register

                text = in.readLine();

                while (!text.equals("y") || !text.equals("n")) { //
                    out.println("Are you a registered user? y/n");
                    text = in.readLine();
                }



                //if client is a user
                if (text.equals("y")){

                    out.println("Req user");

                    while (!checkUser(in.readLine(),in.readLine())){
                        out.println("Req user");
                    }
                }else { //if user is not a registered user

                    out.println("req userinfo");

                }











                ChatServer chat = new ChatServer(connect);
                chat.start();
            }

        }catch (IOException ioe){
            System.out.println("Exception ocurred when listening to port"
                    + portNumber + "or listening to connection");
        }

    }

    public static boolean checkUser(String uname, String passw){
        String uname, passw;
        boolean exists;

        int i = 0;
        for (User u : allUsers){
            if (u.getUserName().equals(uname) &&
                    u.getPassword().equals(passw))
                exists = true;
        }
        return exists;
    }
}
