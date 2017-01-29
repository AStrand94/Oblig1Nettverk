import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by strand117 on 28.01.2017.
 */
public class ServerClient {

        Socket socket;
        User user;
        InetAddress clientAddr;
        int serverport, clientPort;
        BufferedReader in;
        PrintWriter out;

    public ServerClient(Socket socket, User user){
        this.user = user;
        this.socket = socket;
        clientAddr = socket.getInetAddress();
        serverport = socket.getLocalPort();
        clientPort = socket.getPort();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public String getUsername(){
        return user.getUserName();
    }

    public InetAddress getClientAddr(){
        return clientAddr;
    }

    public void writeMessage(String text){
        out.println(text);
    }

    public BufferedReader getMessage(){
        return in;
    }
}
