import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import references.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by user on 30/04/2016.
 */
class ClientsThread extends Thread {
    private  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    private int playerID;
    private String time;
    private String role;
    private int kpu_id = 0;
    private ArrayList<String> friend;
    private JSONArray clients;
    private String sentence;

    public ClientsThread(){

    }

    @Override
    public void run(){
        System.out.println("Masukan IP Server: ");
        //String ip = inFromUser.readLine();
        String ip = "localhost";
        System.out.println("Masukan Port Server: ");
        //String port = inFromUser.readLine();
        String port = "9875";

        TCPConnection(ip,Integer.parseInt(port));

            /*System.out.println("Masukan Address : ");
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
            }*/

    }

    public int statusCode(String test, ArrayList<String> attributes) {
        if (!isJSONValid(test))
            return 1;
        else {
            int i;
            for (i=0;i<attributes.size();i++) {
                if (!test.contains(attributes.get(i)))
                    return 1;
            }
        }
        return 0;
    }

    public boolean isJSONValid(String test) {
        try {
            new org.json.JSONObject(test);
        } catch (Exception e) {
            try {
                new org.json.JSONArray(test);
            } catch (Exception e1) {
                return false;
            }
        }
        return true;
    }

    public void TCPConnection(String ServerIP, int ServerPort){
        try {
            String method, description, responseLine, statusResponse;
            ArrayList<String> attributes = new ArrayList<>();
            boolean progress;
            StringBuffer response = new StringBuffer();
            JSONParser parser = new JSONParser();
            Object tempObj;
            JSONObject obj, objOut;
            JSONArray array;
            int temp = 9990;
            String ip = ServerIP;
            Socket TCPclientSocket = new Socket(ip, ServerPort);
            PrintWriter outToServer = new PrintWriter(TCPclientSocket.getOutputStream(), true);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(TCPclientSocket.getInputStream()));


            // joining
            progress = false;
            while (!progress) {
                System.out.println("Insert username : ");
                sentence = inFromUser.readLine();
                objOut = new JSONObject();
                objOut.put("method", "join");
                objOut.put("username", sentence);
                objOut.put("udp_address", TCPclientSocket.getLocalAddress().toString());
                objOut.put("udp_port", 9999);
                System.out.println(objOut.toString());
                System.out.println(TCPclientSocket.getOutputStream().toString());
                outToServer.println(objOut.toString());
                System.out.println("abis dikirim");
                responseLine = inFromServer.readLine();
                response.append(responseLine);
                tempObj = parser.parse(response.toString());
                obj = (JSONObject) tempObj;
                statusResponse = (String) obj.get("status");
                switch (statusResponse) {
                    case "ok":
                        playerID = ((Long) obj.get("player_id")).intValue();
                        progress = true;
                        break;
                    case "fail":
                    case "error":
                        description = (String) obj.get("description");
                        System.out.println(description);
                        break;
                }
            }

            // readying up
            response = new StringBuffer();
            progress = false;
            while (!progress) {
                System.out.println("Type 'ready' without single quotation mark to ready up OR 'leave' to leave the game : ");
                sentence = inFromUser.readLine();
                while (!sentence.equals("ready")) {
                    if (sentence.equals("leave")) {
                        TCPclientSocket.close();
                        System.exit(0);
                    }
                    System.out.println("Type 'ready' without single quotation mark to ready up OR 'leave' to leave the game : ");
                    sentence = inFromUser.readLine();
                }
                objOut = new JSONObject();
                objOut.put("method", "ready");
                outToServer.println(objOut.toString());

                responseLine = inFromServer.readLine();
                response.append(responseLine);
                System.out.println(response.toString());
                tempObj = parser.parse(response.toString());
                obj = (JSONObject) tempObj;
                statusResponse = (String) obj.get("status");
                switch (statusResponse) {
                    case "ok":
                        progress = true;
                    case "error":
                        description = (String) obj.get("description");
                        System.out.println(description);
                        break;
                }
            }

            // starting game
            response = new StringBuffer();
            progress = false;
            while (!progress) {
                responseLine = inFromServer.readLine();
                response.append(responseLine);
                tempObj = parser.parse(response.toString());
                obj = (JSONObject) tempObj;
                objOut = new JSONObject();
                attributes.add("method");
                attributes.add("time");
                attributes.add("role");
                attributes.add("description");
                System.out.println(response.toString());
                System.out.println("status code = " + statusCode(obj.toString(), attributes));
                if (statusCode(obj.toString(), attributes) == 0) {
                    method = (String) obj.get("method");
                    if (method.equals("start_game")) {
                        time = (String) obj.get("time");
                        role = (String) obj.get("role");
                        description = (String) obj.get("description");
                        System.out.println(description);
                        if (role.equals("werewolf")) {
                            friend = new ArrayList<>();
                            array = (JSONArray) obj.get("friend");
                            for (int i = 0; i < array.size(); i++)
                                friend.add((String) array.get(i));
                        }
                        objOut.put("status", "ok");
                        outToServer.println(objOut.toString());
                        progress = true;
                    } else {
                        objOut.put("status", "fail");
                        outToServer.println(objOut.toString());
                    }
                } else {
                    objOut.put("status", "error");
                    outToServer.println(objOut.toString());
                }
            }


            //SendMessage sendMessage = new SendMessage(TCPclientSocket.getLocalAddress().toString(), TCPclientSocket.getLocalPort());


            // getting all players info
            System.out.println("Masuk get player info");
            response = new StringBuffer();
            progress = false;
            while (!progress) {
                objOut = new JSONObject();
                objOut.put("method", "client_address");
                outToServer.println(objOut.toString());
                responseLine = inFromServer.readLine();
                response.append(responseLine);
                tempObj = parser.parse(response.toString());
                obj = (JSONObject) tempObj;
                statusResponse = (String) obj.get("status");
                switch (statusResponse) {
                    case "ok":
                        description = (String) obj.get("description");
                        System.out.println(description);
                        clients = (JSONArray) obj.get("clients");
                        System.out.println(clients.toString());
                        progress = true;
                        break;
                    case "fail":
                    case "error":
                        description = (String) obj.get("description");
                        System.out.println(description);
                        break;
                }

            }



            // paxos consensus
            if (playerID == clients.size()-1 || playerID == clients.size()-2) {
                DatagramSocket datagramSocket = new DatagramSocket();
                UnreliableSender unreliableSender = new UnreliableSender(datagramSocket);
                System.out.println("Sebelum read integer");
                int proposedID = playerID;
                System.out.println("Proposed id"+proposedID);
                int proposalNo = 1;
                while (proposalNo <= 4) {
                    objOut = new JSONObject();
                    objOut.put("method", "prepare_proposal");
                    JSONArray proposalID = new JSONArray();
                    proposalID.add(proposalNo);
                    proposalID.add(proposedID);
                    objOut.put("proposal_id", proposalID);

                    byte[] sendData = objOut.toString().getBytes();
                    String targetAddress = (String) ((JSONObject) clients.get(proposalNo-1)).get("address");
                    targetAddress = targetAddress.substring(1);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(targetAddress), temp+proposalNo-1);
                    unreliableSender.send(sendPacket);
                    proposalNo++;
                }
                datagramSocket.close();
            }
            else {
                DatagramSocket serverSocket = null;
                System.out.println("MASUK ELSE 5 atau 6");
                for(int i=0; i< clients.size()-2;i++){
                    if(playerID == i){
                        temp += playerID;
                         serverSocket = new DatagramSocket(temp);
                    }

                }
                System.out.println("Socket Voter "+temp);
                //ServerSocket sSocket = new ServerSocket(serverPort);
                byte[] receiveData = new byte[1024];
                while(true)
                {

                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    //Socket clientSocket = sSocket.accept();

                    String clientAddress = receivePacket.getAddress().toString();

                    String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("RECEIVED: " + sentence);
                    tempObj = parser.parse(sentence);
                    obj = (JSONObject) tempObj;
                    objOut = new JSONObject();

                }
            }

            // closing TCP socket after game is over
            TCPclientSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

class ReceiveMessage extends Thread{
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
        } catch (SocketException | UnknownHostException e) {
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
            //System.out.println("FROM SERVER:" + sentence);
            //new Thread(new SendMessage(senderAddress,senderPort,receiverAddress,receiverPort)).start();
        }
    }

}
class SendMessage extends Thread {
    private int senderPort;
    private String senderAddress;
    private String sentence;
    private DatagramSocket clientSocket;
    private  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    public SendMessage(String SenderAddress, int SenderPort){
        this.senderAddress = SenderAddress;
        this.senderPort = SenderPort;
        try {
            this.clientSocket = new DatagramSocket(senderPort,InetAddress.getByName(senderAddress));
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
    }

    public void send(String receiverAddress, int receiverPort){
        byte[]  sendData = new byte[1024];
        try {
            sentence = inFromUser.readLine();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(receiverAddress), receiverPort);
            clientSocket.send(sendPacket);
            new Thread(new ReceiveMessage(senderAddress,senderPort,receiverAddress,receiverPort)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
