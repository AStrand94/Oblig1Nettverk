import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientServerTest {

    static PrintWriter out;
    static BufferedReader stdIn, in;

    public static void main(String[] args) {

        String hostName = "127.0.0.1";
        int portNumber = 5555;

        if (args.length > 0){
            hostName = args[0];
            if (args.length > 1){
                portNumber = Integer.parseInt(args[1]);
                if (args.length > 2){
                    System.err.println("Usage : java EchoClientTCP " +
                            "[<host name>] [port number]");
                    System.exit(1);
                }
            }
        }

        System.out.println("Hi, I am EchoUCase TCP client!");

        try {

            Socket clientSocket = new Socket(hostName,portNumber);

            //Writer til socket
            out = new PrintWriter(clientSocket.getOutputStream(),true);

                //Reader fra socket
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            //Leser fra keyboard
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(1);
            String userInput, receivedText = in.readLine();
            System.out.println(2);
            System.out.println(receivedText);
            System.out.println(3);

            while (!receivedText.equals("loginAccept") && (userInput = stdIn.readLine()) != null){  //while loop to initialize login/registration for the user
                System.out.println(4);
                out.println(userInput);
                receivedText = in.readLine();
                System.out.println(receivedText);
            }

            if (!receivedText.equals("loginAccept")) {
                System.out.println("Session ended");
                System.exit(1);
            }

            System.out.println(in.readLine());
            System.out.println("READLINE:");

            sender().start();
            receiver().start();

        }catch (UnknownHostException uhe){
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }catch(IOException ioe) {
            System.err.println("Unknown host " + hostName);
            System.exit(1);
        }

    }

    public static Thread sender(){
        return new Thread(() -> {
            try {
                String input;
                while ((input = stdIn.readLine()) != null) {
                    out.println(input);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        });
    }

    public static Thread receiver(){
        return new Thread(() -> {
            try {
                String received;
                while ((received = in.readLine()) != null) {
                    System.out.println(received);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        });
    }
}
