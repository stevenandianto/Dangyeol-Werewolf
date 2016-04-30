/**
 * Created by user on 30/04/2016.
 */
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.*;

class Server
{

    public static void main(String argv[]) throws Exception
    {
        String clientSentence = null;
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

            /** GETTING METHOD PER LOOP **/

            System.out.println("masuk 1");

            // reading lines
            StringBuffer jb = new StringBuffer();
            while ((clientSentence = inFromClient.readLine()) != null)
                jb.append(clientSentence);

            System.out.println(jb.toString());

            // parsing JSON
            JSONParser parser = new JSONParser();

            Object tempObj = parser.parse(jb.toString());
            JSONObject obj = (JSONObject) tempObj;

            System.out.println("Received: " + obj.toString());

            // receive parameters
            String method = (String) obj.get("method");

            switch (method) {
                case "join":
                    System.out.println("Masuk join");
                    break;
                case "leave" :
                    break;
                default :
                    System.out.println("Wrong syntax");
            }

        }


    }
}