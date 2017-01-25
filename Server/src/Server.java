import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by strand117 on 25.01.2017.
 */
public class Server {

    static int portNumber;
    ArrayList users = new ArrayList<User>();
    ArrayList onlineUsers = new ArrayList<User>();


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

            }

        }catch (IOException ioe){
            System.out.println("Exception ocurred when listening to port"
                    + portNumber + "or listening to connection");
        }

    }
}
