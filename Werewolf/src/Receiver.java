/**
 * Created by user on 29/04/2016.
 */
import java.net.*;

public class Receiver
{
    /**
     * Contoh kode program untuk node yang menerima paket. Idealnya dalam paxos
     * balasan juga dikirim melalui UnreliableSender.
     */
    public static void main(String args[]) throws Exception
    {
        int serverPort = 9875;
        DatagramSocket serverSocket = new DatagramSocket(serverPort);
        //ServerSocket sSocket = new ServerSocket(serverPort);
        byte[] receiveData = new byte[1024];
        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            //Socket clientSocket = sSocket.accept();

            String clientAddress = receivePacket.getAddress().toString();
            String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("RECEIVED: " + clientAddress);
        }
    }
}
