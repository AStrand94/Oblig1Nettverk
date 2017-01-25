import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by strand117 on 25.01.2017.
 */
public class ChatServer extends Thread{

    Socket connection;
    InetAddress clientAddr;
    int serverport, clientPort;

    public ChatServer(Socket connection){
        this.connection = connection;
        clientAddr = connection.getInetAddress();
        serverport = connection.getLocalPort();
        clientPort = connection.getPort();
    }

    @Override
    public void run(){
        try(
                PrintWriter out = new PrintWriter(connection.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ){

            String text;
            while ((text = in.readLine()) != null) {

                System.out.println("Client [" + clientAddr.getHostAddress()
                        +  ":" + clientPort +"] > " + text);




            }
            }catch(IOException ioe){
            System.out.println("Exception when connecting to the client");
            }
        }
    }

}
