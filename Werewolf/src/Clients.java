/**
 * Created by user on 29/04/2016.
 */
import com.sun.org.apache.xpath.internal.SourceTree;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.*;

class Clients
{
    public static void main(String args[]) throws Exception
    {
        BufferedReader inFromUser =
            new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Masukan IP Server: ");
        String ip = inFromUser.readLine();
        System.out.println("Masukan Port Server: ");
        String port = inFromUser.readLine();

        String sentence;
        String modifiedSentence;
        Socket TCPclientSocket = new Socket(ip, Integer.parseInt(port));
        DataOutputStream outToServer = new DataOutputStream(TCPclientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(TCPclientSocket.getInputStream()));
        System.out.println("Insert username : ");
        sentence = inFromUser.readLine();
        JSONObject objOut = new JSONObject();
        objOut.put("method", "join");
        objOut.put("username", sentence);
        objOut.put("udp_address", TCPclientSocket.getInetAddress().toString());
        objOut.put("udp_port", TCPclientSocket.getPort());
        System.out.println(objOut.toJSONString());
        System.out.println(objOut.toString());
        outToServer.writeBytes(objOut.toString());
        System.out.println("abis dikirim");
        //modifiedSentence = inFromServer.readLine();
        //System.out.println("FROM SERVER: " + modifiedSentence);
        TCPclientSocket.close();

        //UDP Initiation
        System.out.println("Masukan Port Client: ");
        String portclient = inFromUser.readLine();
        System.out.println("Masukan Port Client lain: ");
        String portclientlain = inFromUser.readLine();
        InetAddress ClientAddress = InetAddress.getLocalHost();
        DatagramSocket clientSocket = new DatagramSocket(Integer.parseInt(portclient));
        //Server
        String targetAddress = ip;
        InetAddress ServerAddress = InetAddress.getByName(targetAddress);
        int serverPort = 9875;

        //Data yang di send ke server
        byte[] sendData = ip.getBytes();
        DatagramPacket sendPort = new DatagramPacket(sendData, sendData.length, ServerAddress, serverPort);

        //Unreliable Sender
        UnreliableSender unreliableSender = new UnreliableSender(clientSocket);
        unreliableSender.send(sendPort);

        // UDP LOOP
        while(true) {

            sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            System.out.println("Kirim pesan ? (Y/N)");
            sentence = inFromUser.readLine();
            if(sentence.equals("Y")){
                sentence = inFromUser.readLine();
                sendData = sentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ClientAddress, Integer.parseInt(portclientlain));
                clientSocket.send(sendPacket);
            }
            else {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                modifiedSentence = new String(receivePacket.getData());
                System.out.println("FROM SERVER:" + modifiedSentence);
            }
            clientSocket.close();
            clientSocket = new DatagramSocket(Integer.parseInt(portclient));
        }

    }
}