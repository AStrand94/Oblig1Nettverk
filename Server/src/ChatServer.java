import javafx.scene.shape.Circle;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;
import javafx.scene.paint.Color;
/**
 * Class ChatServer
 *
 * Is an object that controls the text-based chat between two users.
 */
public class ChatServer {

    ServerClient client1, client2;

    private Thread t1,t2;
    private String s1,s2;
    private String u1,u2;

    private boolean chatAlive;
    private static int i = 0;

    /**
     * Constructor, to take one client and start the first thread
     * @param client1 ServerClient
     */
    public ChatServer(ServerClient client1){
        System.out.println("NEW CHATSERVER with " + client1.getUsername());
        this.client1 = client1;
        s1 = '[' + client1.getUsername() + ']' + ':' + ' ';


        t1 = client1();
        t1.start();
        u1 = client1.getUsername();
    }

    /**
     * Adds a new client to the chat. Is nessescary to call addClient to
     * start the chat between two clients.
     * @param client2 ServerClient
     */
    public void addClient(ServerClient client2){
        chatAlive = true;
        this.client2 = client2;

        client2.setStatus(new Circle(8, Color.ORANGE));
        client1.setStatus(new Circle(8, Color.ORANGE));

        s2 = '[' + client2.getUsername() + ']' + ':' + ' ';
        confirmConnection();
        u2 = client2.getUsername();

        t2 = client2();
        t2.start();
    }

    /**
     * Called when connection is confirmed, to send out confirmation
     * to both clients.
     */
    private void confirmConnection(){
        String connected = "*c*";
        client1.writeMessage(connected + client2.getUsername());
        client2.writeMessage(connected + client1.getUsername());
    }

    /**
     * Returns true if there is at least one client
     * @return true if no ServerClients = null
     */
    public boolean isAvailable(){
        if (client1 == null && client2 == null)
            throw new IllegalStateException("Empty ChatServer still alive");

        return (client1 == null || client2 == null);
    }


    /**
     * Should call isAvailable() first.
     * @return the username(s) of the clients
     * @throws NullPointerException if isAvailable == false
     */
    public String getUsername(){

        if (client1 == null && client2 == null)
            throw new NoSuchElementException("No Users in this chat");

        if (client2 == null) return client1.getUsername();
        else if (client1 == null) return client2.getUsername();
        else return client1.getUsername() + ' ' + client2.getUsername();
    }

    /**
     * Returns a Thread that starts the thread for
     * the ServerClient initialized in the constructor
     * @return Thread
     */
    private Thread client1(){
        return new Thread(() -> {
            try {

                String text;
                while (!(text = client1.getMessage().readLine()).equals("*QUIT*") && (chatAlive || (text.length() > 4 && text.substring(0,4).equals("*no*")))){
                    System.out.println(text);
                    if (text.length() > 4 && text.substring(0,4).equals("*no*")){
                        System.out.println("RECEIVED NO");
                        Server.noChat(client1.getUsername(),text.substring(4,text.length()));
                    }

                    sendMessage(s1,text);
                }
                /*
                    client returns the string "ok" when disconnected, to break out of the readLine() method,
                    and will be put in a new chat.
                   */
                if (text.equals("ok")){
                    endChatNewChat(client1);
                } else if (text.equals("*QUIT*") && client2 != null) {
                    client1.writeMessage("*d*" + client2.getUsername());
                    endChatSeeUsers(client1);
                    chatAlive = false;
                    if (client2 != null) client2.writeMessage("*d*" + client1.getUsername());
                }else {
                    chatAlive = false;
                    endChatSeeUsers(client1);
                }
            }catch(SocketException|NullPointerException se){
                System.out.println(client1.getUsername() + " has logged out");
                chatAlive = false;
                client1.closeSocket();
                Server.logOff(client1);
                client1 = null;
                Server.endChat(this);
                if(client2 != null) client2.writeMessage("*d*" + u1);
            }catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Returns a Thread for client2, to start the chat.
     * @return Thread
     */
    private Thread client2(){
        return new Thread(() -> {
            try {
                String text;
                System.out.println("client2()");
                while (!(text = client2.getMessage().readLine()).equals("*QUIT*") && chatAlive){
                    if (text.length() > 4 && text.substring(0,4).equals("*no*")){
                        Server.noChat(client2.getUsername(),text.substring(4,text.length()));
                    }
                    sendMessage(s2,text);
                }

                if (text.equals("ok")){
                    endChatNewChat(client2);
                } else if (text.equals("*QUIT*")) {
                    client2.writeMessage("*d*" + client1.getUsername());
                    endChatSeeUsers(client2);
                    chatAlive = false;
                    if (client1 != null)client1.writeMessage("*d*" + client2.getUsername());
                }else {
                    chatAlive = false;
                    endChatSeeUsers(client2);
                }
                System.out.println("done client2()");
            }catch(SocketException|NullPointerException se){
                se.printStackTrace();
                chatAlive = false;
                client2.closeSocket();
                Server.logOff(client2);
                client2 = null;
                if(client1 != null) client1.writeMessage("*d*" + u2);
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    /**
     * Sends messages to the clients in the chat.
     * @param from String, username to the sender
     * @param text String, the message
     */
    synchronized void sendMessage(String from,String text){
        if (chatAlive) {
            client2.writeMessage(from + text);
            client1.writeMessage(from + text);
        }
    }

    /**
     * Ends the chat for a client, and puts the client in a new chat
     * and lets the client chose a new client to connect to
     * @param client ServerClient
     */
    public void endChatSeeUsers(ServerClient client){
        client.setStatus(new Circle(8, Color.GREEN));
        Server.endChat(this);
        Server.putInChat(client);
        Server.sendUpdatedUsers();
    }

    /**
     * Ends the chat for a client, and puts the client in a new chat.
     * Called when the other user already has disconnected.
     * @param client
     */
    private void endChatNewChat(ServerClient client){
        client.setStatus(new Circle(8, Color.GREEN));
        Server.endChat(this);
        Server.putInNewChat(client);
        Server.sendUpdatedUsers();
    }

}


