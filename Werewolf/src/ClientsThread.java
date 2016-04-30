import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by user on 30/04/2016.
 */
class ClientsThread extends Thread {
    private  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    private String sentence;

    public ClientsThread(){

    }

    @Override
    public void run(){
        try {
            System.out.println("Masukan IP Server: ");
            String ip = inFromUser.readLine();
            System.out.println("Masukan Port Server: ");
            String port = inFromUser.readLine();
            System.out.println("Masukan Address : ");
            String SenderAddress = inFromUser.readLine();
            System.out.println("Masukan Port : ");
            String SenderPort = inFromUser.readLine();
            int SendPort = Integer.parseInt(SenderPort);
            System.out.println("Masukan Address Penerima : ");
            String ReceiverAddress = inFromUser.readLine();
            System.out.println("Masukan Port Penerima : ");
            String ReceiverPort = inFromUser.readLine();
            int RecvPort = Integer.parseInt(ReceiverPort);
            String clientSelection;
            System.out.println("Masukan Pilihan: ");
            System.out.println("1. TCP Connection ");
            System.out.println("2. UDP Connection ");
            clientSelection = inFromUser.readLine();
            while (clientSelection != null) {
                switch (clientSelection) {
                    case "1":
                        TCPConnection(ip,Integer.parseInt(port));
                        break;
                    case "2":

                        SendMessage sm = new SendMessage(SenderAddress,SendPort,ReceiverAddress,RecvPort);
                        break;
                    default:
                        System.out.println("Incorrect command received.");
                        break;
                }
                break;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void TCPConnection(String ServerIP, int ServerPort){
        try {


            String ip = ServerIP;
            Socket TCPclientSocket = new Socket(ip, ServerPort);
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
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

class ReceiveMessage extends  Thread{
    private String receiverAddress;
    private int receiverPort;
    private int senderPort;
    private String senderAddress;
    private String sentence;
    private DatagramSocket clientSocket;
    private  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    public ReceiveMessage(String SenderAddress, int SenderPort, String RecvAddress, int RecvPort){
        this.receiverAddress = RecvAddress;
        this.receiverPort = RecvPort;
        this.senderAddress = SenderAddress;
        this.senderPort = SenderPort;
        try {
            this.clientSocket = new DatagramSocket(receiverPort,InetAddress.getByName(receiverAddress));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run(){

        byte[] receiveData = new byte[1024];
        while(true){
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                clientSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("FROM SERVER:" + sentence);
            new Thread(new SendMessage(senderAddress,senderPort,receiverAddress,receiverPort)).start();
        }
    }

}
class SendMessage extends Thread {
    private String receiverAddress;
    private int receiverPort;
    private int senderPort;
    private String senderAddress;
    private String sentence;
    private DatagramSocket clientSocket;
    private  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    public SendMessage(String SenderAddress, int SenderPort, String RecvAddress, int RecvPort){
        this.receiverAddress = RecvAddress;
        this.receiverPort = RecvPort;
        this.senderAddress = SenderAddress;
        this.senderPort = SenderPort;
        try {
            this.clientSocket = new DatagramSocket(senderPort,InetAddress.getByName(senderAddress));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
    }

    public void send(){
        byte[]  sendData = new byte[1024];
        try {
            sentence = inFromUser.readLine();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(this.receiverAddress), receiverPort);
            clientSocket.send(sendPacket);
            new Thread(new ReceiveMessage(senderAddress,senderPort,receiverAddress,receiverPort)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
