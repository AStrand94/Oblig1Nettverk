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

    Socket c1, c2;
    InetAddress clientAddr1, clientAddr2;
    int serverport1, clientPort1, serverport2, clientport2;
    BufferedReader in1, in2;
    PrintWriter out1,out2;

    public ChatServer(Socket connection, PrintWriter out, BufferedReader in){
        this.c1 = connection;
        clientAddr1 = connection.getInetAddress();
        serverport1 = connection.getLocalPort();
        clientPort1 = connection.getPort();

    }

    @Override
    public void run(){
        try(
                PrintWriter out = new PrintWriter(c1.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(c1.getInputStream()));
                ){

            String text;
            while ((text = in.readLine()) != null) {

                System.out.println("Client [" + clientAddr1.getHostAddress()
                        +  ":" + clientPort1 +"] > " + text);




            }
            }catch(IOException ioe){
                System.out.println("Exception when connecting to the client");
            }
        }

}


