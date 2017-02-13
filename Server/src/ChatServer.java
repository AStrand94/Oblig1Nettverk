import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;

/**
 * Created by strand117 on 25.01.2017.
 */
public class ChatServer {

    ServerClient client1, client2;
    InetAddress address1, address2;

    ChatServer thisChat = this;

    Thread t1,t2,st;
    String s1,s2;

    boolean chatAlive;
    static int i = 0;
    int j;



    public ChatServer(ServerClient client1){
        System.out.println("NEW CHATSERVER with " + client1.getUsername());
        this.client1 = client1;
        address1 = client1.getClientAddr();
        s1 = '[' + client1.getUsername() + ']' + ':' + ' ';


        t1 = client1();
        t1.start();
        j = i++;
    }

    public void addClient(ServerClient client2){
        chatAlive = true;
        this.client2 = client2;
        address2 = client2.getClientAddr();

        client2.setStatus("busy");
        client1.setStatus("busy");

        s2 = '[' + client2.getUsername() + ']' + ':' + ' ';
        confirmConnection();

        t2 = client2();
        t2.start();
    }

    private void confirmConnection(){
        String connected = "*c*";
        client1.writeMessage(connected + client2.getUsername());
        client2.writeMessage(connected + client1.getUsername());
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

    private Thread client1(){
        return new Thread(() -> {
            try {

                String text;
                while (!(text = client1.getMessage().readLine()).equals("*QUIT*") && chatAlive){

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
                se.printStackTrace();
                chatAlive = false;
                client1.closeSocket();
                Server.logOff(client1);
                client1 = null;
                Server.endChat(this);
                if(client2 != null) client2.writeMessage("*d*");
            }catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Thread client2(){
        return new Thread(() -> {
            try {
                String text;
                System.out.println("client2()");
                while (!(text = client2.getMessage().readLine()).equals("*QUIT*") && chatAlive){
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
                if(client1 != null) client1.writeMessage("*d*");
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    synchronized void sendMessage(String from,String text){
        client2.writeMessage(s2 + text);
        client1.writeMessage(s2 + text);
    }

    public void endChatSeeUsers(ServerClient client){
        client.setStatus("available");
        Server.endChat(this);
        Server.putInChat(client);
        Server.sendUpdatedUsers();
    }

    private void endChatNewChat(ServerClient client){
        client.setStatus("available");
        Server.endChat(this);
        Server.putInNewChat(client);
        Server.sendUpdatedUsers();
    }

}


