package references; /**
 * Created by user on 29/04/2016.
 */
import java.io.*;
import java.net.*;

class UDPClients
{
    public static void main(String args[]) throws Exception
    {  BufferedReader inFromUser =
            new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Masukan IP Server: ");
        String ip = inFromUser.readLine();
        System.out.println("Masukan Port Server: ");
        String port = inFromUser.readLine();
        InetAddress IPAddress = InetAddress.getByName(ip);
        DatagramSocket clientSocket = new DatagramSocket();
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
        /*
        while(true) {
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = inFromUser.readLine();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9875);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + modifiedSentence);
            clientSocket.close();
        }
        */
    }
}