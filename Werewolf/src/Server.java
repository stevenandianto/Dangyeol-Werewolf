/**
 * Created by user on 30/04/2016.
 */
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


class Server
{
    private static boolean gameover = false;
    private static boolean sameUserName;
    private static boolean currentlyInGame = false;
    private static volatile int nPlayers;
    private static volatile int nReady;
    private static int counter = 0;
    private static int werewolfID1;
    private static int werewolfID2;
    private static volatile int daycount = 1;

    private static ArrayList<ConnectionHandler> clientList = new ArrayList<>();
    private static class ConnectionHandler implements Runnable {
        private int clientNo;
        private Socket socket = null;
        private String username;
        private String udp_address;
        private int udp_port;

        PrintWriter outToClient;
        private int is_alive = 1;

        public ConnectionHandler(Socket sock) {
            socket = sock;
            clientNo = counter;
            System.out.println("clientNo : " + clientNo);
        }

        public void parseCommand(JSONObject obj) throws IOException {
            // receive parameters
            String method = (String) obj.get("method");

            JSONObject objOut = new JSONObject();
            switch (method) {
                case "join":
                    username = (String) obj.get("username");
                    udp_address = (String) obj.get("udp_address");
                    Long udp = (Long) obj.get("udp_port");
                    udp_port = udp.intValue();
                    sameUserName = false;
                    for (int i=0; i<nPlayers; i++) {
                    	if (username.equals(clientList.get(i).username)) {
                    		sameUserName = true;
                    	}
                    }
                    if (!sameUserName) {
	            	    nPlayers++;
	                    objOut.put("status","ok");
	                    objOut.put("player_id", clientNo);
                    }
                    else if (sameUserName) {
	                    objOut.put("status","fail");
	                    objOut.put("description", "username is already taken, no longer single");
                    } else {
	                    objOut.put("status","error");
	                    objOut.put("description","wrong request");
                    }
                    System.out.println(objOut.toJSONString());
                    System.out.println(objOut.toString());
                    outToClient.println(objOut.toString());
                    break;
                case "leave" :
                    if (!currentlyInGame) {
                        objOut.put("status","ok");
                        nPlayers--;
                    } else if (currentlyInGame){
                        objOut.put("status","fail");
                        objOut.put("description","currently in game");
                    } else {
                        objOut.put("status","error");
                        objOut.put("description","wrong request");
                    }
                    System.out.println(objOut.toJSONString());
                    System.out.println(objOut.toString());
                    outToClient.println(objOut.toString());
                    break;
                case "ready" :
                    nReady++;
                    objOut.put("status","ok");
                    objOut.put("description","waiting for other player to start");
                    outToClient.println(objOut.toString());
                    while (nPlayers < 6 || nReady < 6) {
                        System.out.println(nPlayers + " " + nReady);
                    }
                    if (nPlayers == 6 && nReady == nPlayers)
                    {
                        JSONObject objOut2 = new JSONObject();

                        objOut2.put("method","start_game");
                        objOut2.put("time", "day");
                        if (clientNo == werewolfID1 || clientNo == werewolfID2) {
                            objOut2.put("role", "werewolf");
                            JSONArray friend = new JSONArray();
                            friend.add(clientList.get(werewolfID1).username);
                            friend.add(clientList.get(werewolfID2).username);
                            objOut2.put("friend", friend);
                        }
                        else
                            objOut2.put("role", "civilian");
                        objOut2.put("description", "");
                        System.out.println(objOut2.toString());
                        outToClient.println(objOut2.toString());
                        currentlyInGame = true;
                    }
                    break;
                case "client_address" :
                    objOut = new JSONObject();
                    JSONObject objOut2;
                    objOut.put("status","ok");
                    JSONArray clientlist = new JSONArray();
                    for(int i=0; i<clientList.size(); i++) {
                        objOut2 = new JSONObject();
                        objOut2.put("player_id",clientList.get(i).clientNo);
                        objOut2.put("is_alive",clientList.get(i).is_alive);
                        objOut2.put("address",clientList.get(i).udp_address);
                        objOut2.put("port",clientList.get(i).udp_port);
                        objOut2.put("username",clientList.get(i).username);
                        clientlist.add(objOut2);
                    }
                    objOut.put("clients", clientlist);
                    objOut.put("description","list of client retrieved");
                    System.out.println(objOut.toString());
                    outToClient.println(objOut.toString());
                    break;
                case "vote_result_werewolf" :
                    System.out.println("Hasil Vote Werewolf");
                    //Algoritma Hasil Vote Werewolf
                    daycount++;
                    objOut = new JSONObject();
                    objOut.put("method","change_phase");
                    objOut.put("time","day");
                    objOut.put("days",daycount);
                    objOut.put("description","Ganti hari jadi siang");
                    System.out.println(objOut.toString());
                    outToClient.println(objOut.toString());
                    break;
                case "vote_result_civilian" :
                    System.out.println("Hasil Vote Civilian");
                    //Algoritma Hasil Vote Civilian
                    objOut = new JSONObject();
                    objOut.put("method","change_phase");
                    objOut.put("time","night");
                    objOut.put("days", daycount);
                    objOut.put("description","Ganti hari jadi malam");
                    System.out.println(objOut.toString());
                    outToClient.println(objOut.toString());
                    break;
                default :
                    System.out.println("Wrong syntax");
            }
        }

        @Override
        public void run() {
            try {
                String clientSentence = null;
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outToClient = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("asdf");
                System.out.println(socket.getInputStream().toString());

                while (!gameover) {
                    // reading lines
                    StringBuffer jb = new StringBuffer();
                    clientSentence = inFromClient.readLine();
                    System.out.println("hahaha");
                    System.out.println(clientSentence);
                    jb.append(clientSentence);

                    // parsing JSON
                    JSONParser parser = new JSONParser();

                    Object tempObj = parser.parse(jb.toString());
                    JSONObject obj = (JSONObject) tempObj;

                    System.out.println("Received: " + obj.toString());

                    if (obj.toString().contains("method"))
                        parseCommand(obj);
                }
            } catch (IOException | ParseException ex) {
                ex.printStackTrace();
                nPlayers--;
            }
        }

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

    public static void main(String argv[]) throws Exception
    {
        try {
            werewolfID1 = (int) (Math.random() * 6);
            werewolfID2 = (int) (Math.random() * 6);
            while (werewolfID2 == werewolfID1)
                werewolfID2 = (int) (Math.random() * 6);
            ServerSocket servSock = new ServerSocket(9875);
            for (;;) {
                Socket clientSock = servSock.accept();
                System.out.println("Connected from " + clientSock .getInetAddress() + " on port "
                        + clientSock .getPort() + " to port " + clientSock .getLocalPort() + " of "
                        + clientSock .getLocalAddress());
                ConnectionHandler connectionHandler = new ConnectionHandler(clientSock);
                clientList.add(connectionHandler);
                new Thread(connectionHandler).start();
                counter++;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
