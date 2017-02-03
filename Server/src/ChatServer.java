import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * Created by strand117 on 25.01.2017.
 */
public class ChatServer extends Thread{

    ServerClient client1, client2;
    InetAddress address1, address2;

    Thread t1,t2;
    String s1,s2;

    boolean chatAlive;



    public ChatServer(ServerClient client1){
        System.out.println("NEW CHATSERVER");
        this.client1 = client1;
        address1 = client1.getClientAddr();
        s1 = '[' + client1.getUsername() + ']' + ':' + ' ';

    }

    public void addClient(ServerClient client2){
        this.client2 = client2;
        address2 = client2.getClientAddr();

        client2.setStatus("busy");
        client1.setStatus("busy");

        s2 = '[' + client2.getUsername() + ']' + ':' + ' ';
    }

    public boolean isAvailable(){
        if (client1 == null && client2 == null)
            throw new IllegalStateException("Empty ChatServer still alive");

        return (client1 == null || client2 == null);
    }

    //should call isAvailable first
    public String getUsername(){

        if (client1 == null && client2 == null)
            throw new NoSuchElementException("No Users in this chat");

        if (client2 == null) return client1.getUsername();
        else if (client1 == null) return client2.getUsername();
        else return client1.getUsername() + ' ' + client2.getUsername();


    }




    @Override
    public void run(){

        chatAlive = true;
        t1 = client1(); t2 = client2();

        t1.start(); t2.start();



    }

    private Thread client1(){
        return new Thread(() -> {
            try {
                System.out.println("client1()");
                client1.writeMessage("connected to " + client2.getUsername());
                String text;
                while (!(text = client1.getMessage().readLine()).equals("*QUIT*") && chatAlive){
                    client1.writeMessage(s1 + text);
                    client2.writeMessage(s1 + text);
                }
                chatAlive = false;
                endChatSeeUsers(client1);
                System.out.println("done client1()");
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    private Thread client2(){
        return new Thread(() -> {
            try {
                String text;
                System.out.println("client2()");
                client2.writeMessage("connected to " + client1.getUsername());
                while (!(text = client2.getMessage().readLine()).equals("*QUIT*") && chatAlive){
                    client2.writeMessage(s2 + text);
                    client1.writeMessage(s2 + text);
                }
                chatAlive = false;
                endChatSeeUsers(client2);
                System.out.println("done client2()");
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    public void endChatSeeUsers(ServerClient client){
        client.setStatus("available");
        Server.endChat(this);
        Server.putInChat(client);
    }

    public boolean usersAreOnline(){
        return (!client1.socket.isClosed() && !client2.socket.isClosed());
    }

}


