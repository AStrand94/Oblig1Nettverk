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

    ChatServer thisChat = this;

    Thread t1,t2,st;
    String s1,s2;

    boolean chatAlive;
    static int i = 0;
    int j;



    public ChatServer(ServerClient client1){
        System.out.println("NEW CHATSERVER");
        this.client1 = client1;
        address1 = client1.getClientAddr();
        s1 = '[' + client1.getUsername() + ']' + ':' + ' ';

       //st = startThread();
       //st.start();
        t1 = client1();
        t1.start();
        j = i++;
    }

    private Thread startThread(){
        return new Thread(() -> {
            try {
                String text;
                while (!(text = client1.getMessage().readLine()).equals("*QUIT*") && !chatAlive){
                    System.out.println("fra startthread: " + text);
                }
                System.out.println("fra startthread: " + text);




            }catch(IOException e){
                e.printStackTrace();
            }
        });
    }

    public void addClient(ServerClient client2){
        chatAlive = true;
        System.out.println(client2.getUsername() + " is now in chat with thread number " + t1.getId());
        //st.interrupt();
        this.client2 = client2;
        address2 = client2.getClientAddr();

        client2.setStatus("busy");
        client1.setStatus("busy");

        s2 = '[' + client2.getUsername() + ']' + ':' + ' ';
        confirmConnection();

        //t1 = client1();
        t2 = client2();
        //t1.start();
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


/*
    @Override
    public void run(){

        chatAlive = true;
        t2 = client2();

        t2.start();

    }
    */

    private Thread client1(){
        return new Thread(() -> {
            try {

                String text;/*
                while ((text = client1.getMessage().readLine()) != null){
                    System.out.println("QUITTER FRA FØRSTE FOR LOOP" + client1.getUsername());
                    if (text.equals("*QUIT*")) break;
                    if (!chatAlive) break;
                }
                if (text.equals("*QUIT*")){
                    System.out.println("TEXT ER FAEN MEG *QUIT*, SE: " + text);
                    endChatSeeUsers(client1);
                }
                else client2.writeMessage(text);
*/
                System.out.println("client1()" + Thread.currentThread().getId() + client1.getUsername());
                //client1.writeMessage("connected to " + client2.getUsername());
                while (!(text = client1.getMessage().readLine()).equals("*QUIT*") && chatAlive){
                    System.out.println(client1.getUsername() + ": <" + text + '>');
                    //System.out.print("text from client1, " + client1.getUsername() + " " + text);
                    client1.writeMessage(s1 + text);
                    if(client2 != null) client2.writeMessage(s1 + text);
                }
                System.out.println("(after while)"+client1.getUsername() + ": <" + text + '>');
                System.out.println("while loop done, nr: " + Thread.currentThread().getId());
                if (text.equals("*QUIT*") && client2 != null) {
                    client2.writeMessage("*d*" + client1.getUsername());
                    client1.writeMessage("*d*" + client2.getUsername());
                }
                chatAlive = false;
                endChatSeeUsers(client1);
                System.out.println("done client1()" + Thread.currentThread().getId());
            }catch (IOException e){
                e.printStackTrace();
            }/*catch(NullPointerException npe){
                System.out.println("client " + client1.getUsername() + " disconnected");
                chatAlive = false;
                //t2.stop();
                if (client2 != null) {
                    client2.writeMessage("*d*");
                    endChatSeeUsers(client2);
                }
                client1.setStatus("offline");

            }*/
        });
    }

    private Thread client2(){
        return new Thread(() -> {
            try {
                String text;
                System.out.println("client2()");
                //client2.writeMessage("connected to " + client1.getUsername());
                while (!(text = client2.getMessage().readLine()).equals("*QUIT*") && chatAlive){
                    client2.writeMessage(s2 + text);
                    client1.writeMessage(s2 + text);
                }
                if (text.equals("*QUIT*")) {
                    client1.writeMessage("*d*" + client2.getUsername());
                    client2.writeMessage("*d*" + client1.getUsername());
                }
                chatAlive = false;
                endChatSeeUsers(client2);
                System.out.println("done client2()");
            }catch (IOException e){
                e.printStackTrace();
            }/*catch(NullPointerException npe){
                System.out.println("client " + client2.getUsername() + " disconnected");
                chatAlive = false;
                //t1.stop();
                client1.writeMessage("*d*");
                client2.setStatus("offline");
                endChatSeeUsers(client1);
            }*/
        });
    }

    public void endChatSeeUsers(ServerClient client){
        System.out.print("ending chat for " + client.getUsername());
        if (client == client1) System.out.println("tid: "+t1.getId());
        else System.out.println(t2.getId());
        client.setStatus("available");
        Server.endChat(this);
        Server.putInChat(client);
        Server.sendUpdatedUsers();
    }

}


