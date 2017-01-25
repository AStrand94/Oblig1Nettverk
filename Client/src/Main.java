import java.io.IOException;

/**
 * Created by stiangrim on 25.01.2017.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        Client client = new Client();
        client.connect("127.0.0.1", 5555);

    }

}
