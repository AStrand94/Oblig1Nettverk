import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by stiangrim on 25.01.2017.
 */
public class Client {

    String hostName;
    int portNumber;

    String username;
    String password;

    public Client() {
        this.hostName = "127.0.0.1";
        this.portNumber = 5555;
    }

    public boolean registrerUser(String username, String password) {
        //Check if username exists before

        return false;
    }

    public void connect(String hostName, int portNumber) throws IOException{
        this.hostName = hostName;
        this.portNumber = portNumber;

        Socket socket = new Socket(hostName, portNumber);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));


        String userInput;

        //Evt. while (bruker ikke har logget ut)
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);

            String recievedText = in.readLine();

            System.out.println(recievedText);
        }
    }

}
