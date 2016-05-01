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
    private static int nPlayers;
    private static int nReady;
    private static int counter = 0;
    private static ArrayList<ConnectionHandler> clientList = new ArrayList<>();

    private static class ConnectionHandler implements Runnable {
        private int clientNo;
        private Socket socket = null;
        private String username;
        private String udp_address;
        private int udp_port;
        PrintWriter outToClient;

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
                    //if (/*status_ok */) {
                    nPlayers++;
                    objOut.put("status","ok");
                    objOut.put("player_id", clientNo);
                    /**}
                     else if () {
                     objOut.put("status","fail");
                     objOut.put("description", "fail description");
                     } else {
                     objOut.put("status","error");
                     objOut.put("description","wrong request");
                     }
                     System.out.println(objOut.toJSONString());
                     System.out.println(objOut.toString());**/
                    outToClient.println(objOut.toString());
                    break;
                case "leave" :
                    //if (/* status_ok */) {
                    objOut.put("status","ok");
                    nPlayers--;
					/*} else if (){
						objOut.put("status","fail");
						objOut.put("description","currently in game");
					} else {
						objOut.put("status","error");
						objOut.put("description","wrong request");
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());*/
                    outToClient.println(objOut.toString());
                    break;
                case "ready" :
                    //if (/* status_ok */) {
                    nReady++;
                    objOut.put("status","ok");
                    objOut.put("description","waiting for other player to start");
					/*} else {
						objOut.put("status","error");
						objOut.put("description","wrong request");
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());*/
                    outToClient.println(objOut.toString());
                    if (nPlayers == 6 && nReady == nPlayers)
                    {
                        JSONObject objOut2 = new JSONObject();
                        objOut2.put("method","start_game");
                        objOut2.put("time", "day");
                        //objOut2.put("role", );
                        //if (role.equals("werewolf"))
                        //    objOut2.put("friend", /* friends */);
                        objOut2.put("description","");
                        System.out.println(objOut2.toJSONString());
                        System.out.println(objOut2.toString());
                        outToClient.println(objOut2.toString());
                    }
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
