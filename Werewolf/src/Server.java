/**
 * Created by user on 30/04/2016.
 */
import java.io.*;
import java.net.*;

class Server
{
    public static void main(String argv[]) throws Exception
    {
        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(9875);

        while(true)
        {
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Connected from " + connectionSocket .getInetAddress() + " on port "
                    + connectionSocket .getPort() + " to port " + connectionSocket .getLocalPort() + " of "
                    + connectionSocket .getLocalAddress());
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = inFromClient.readLine();
            System.out.println("Received: " + clientSentence);
            capitalizedSentence = clientSentence.toUpperCase() + '\n';
            outToClient.writeBytes(capitalizedSentence);
        }
    }
}