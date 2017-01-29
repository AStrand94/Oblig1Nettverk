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



    public ChatServer(ServerClient client1){
        System.out.println("NEW CHATSERVER");
        this.client1 = client1;
        address1 = client1.getClientAddr();

    }

    public void addClient(ServerClient client2){
        this.client2 = client2;
        address2 = client2.getClientAddr();
    }

    public boolean isAvailable(){
        return (client1 == null || client2 == null);
    }

    //should call isAvailable first
    public String getUsername(){

        if (client1 == null && client2 == null)
            throw new NoSuchElementException("No Users in this chat");

        if (client2 == null) return client1.getUsername();
        else if (client1 == null) return client2.getUsername();
        else return client1.getUsername() + " " + client2.getUsername();


    }




    @Override
    public void run(){

        t1 = client1(); t2 = client2();

        t1.start(); t2.start();

    }

    public Thread client1(){
        return new Thread(() -> {
            try {
                System.out.println("client1()");
                String text;
                while ((text = client1.getMessage().readLine()).equals("*QUIT*") && t2.isAlive()){
                    System.out.println("WRITING MESSAGE");
                    client2.writeMessage(text);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    public Thread client2(){
        return new Thread(() -> {
            try {
                String text;
                System.out.println("client2()");
                while ((text = client2.getMessage().readLine()).equals("*QUIT*") && t1.isAlive()){
                    System.out.println("WRITING MESSAGE");
                    client1.writeMessage(text);
                }
                System.out.println("done client2()");
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    public boolean usersAreOnline(){
        return (!client1.socket.isClosed() && !client2.socket.isClosed());
    }

}


