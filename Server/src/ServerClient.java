import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * The ServerClient class is used when a client connects, and registers with
 * username and password. The ServerClient class contains an User object, together
 * with the Socket object that this User is connected to.
 */
public class ServerClient {

        private Socket socket;
        private User user;
        InetAddress clientAddr;
        int serverport, clientPort;
        BufferedReader in;
        PrintWriter out;

    /**
     * Takes a Socket and User object to initialize this object.
     * @param socket Socket
     * @param user User
     */
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

    /**
     *
     * @return String username
     */
    public String getUsername(){
        return user.getUserName();
    }

    /**
     *
     * @return InetAddress
     */
    public InetAddress getClientAddr(){
        return clientAddr;
    }

    /**
     * Writes a message to this User, using its socket
     * @param text String
     */
    public void writeMessage(String text){
        out.println(text);
    }

    /**
     * Returns a BufferedReader to read messages from this client.
     * Should call readLine() from this BufferedReader to get the message.
     * @return BufferedReader
     */
    public BufferedReader getMessage(){
        return in;
    }

    /**
     * Sets the status of an user.
     * @param status String
     */
    public void setStatus(String status){
        user.setStatus(status);
    }

    /**
     * Returns the user in this ServerClient object
     * @return User
     */
    public User getUser(){
        return user;
    }

    /**
     * Terminates the connection to this user
     */
    public void closeSocket(){
        try {
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
