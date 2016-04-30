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
			
			JSONObject objOut = new JSONObject();
            switch (method) {
                case "join":
                    String username = (String) obj.get("username");
					String udp_address = (String) obj.get("udp_address");
					String udp_port = (String) obj.get("udp_port");
					if (/*status_ok */) {
						objOut.put("status","ok");
						objOut.put("player_id", /* playerID */);
					}
					else if ( /* status_fail */) {
						objOut.put("status","fail");
						objOut.put("description", /* failDescription */);
					} else {
						objOut.put("status","error");
						objOut.put("description","wrong request");
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());
					outToClient.writeBytes(objOut.toString());
                    break;
                case "leave" :
					if (/* status_ok */) {
						objOut.put("status","ok");
					} else if ( /* status_fail */){
						objOut.put("status","fail");
						objOut.put("description","currently in game");
					} else {
						objOut.put("status","error");
						objOut.put("description","wrong request");
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());
					outToClient.writeBytes(objOut.toString());
                    break;
				case "ready" :
					if (/* status_ok */) {
						objOut.put("status","ok");
						objOut.put("description","waiting for other player to start")
					} else {
						objOut.put("status","error");
						objOut.put("description","wrong request");
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());
					outToClient.writeBytes(objOut.toString());
					if (/* all players ready */)
					{
						JSONObject objOut2 = new JSONObject();
						objOut2.put("method","start_game");
						objOut2.put("time", /* day/night */);
						objOut2.put("role", /* werewolf/civilian */);
						objOut2.put("friend", /* friends */);
						objOut2.put("description","");
						System.out.println(objOut2.toJSONString());
						System.out.println(objOut2.toString());
						outToClient.writeBytes(objOut2.toString());												
					}
					break;
				case "client_address" :
					if (/* status_ok */) {
						objOut.put("status","ok");
						objOut.put("clients", /* ("player_id",playerID)("is_alive",isAlive)("address",playerAddress)("port",playerPort)("username",playerName) if(!isAlive){("role",playerRole)} */);
					} else if (/* status_fail */){
						objOut.put("status","fail");
						objOut.put("description", /* failDescription */);
					} else {
						objOut.put("status","error");
						objOut.put("description", /* errorDescription */);
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());
					outToClient.writeBytes(objOut.toString());
					JSONObject objOut2 = new JSONObject();
					objOut2.put("method","vote_now");
					objOut2.put("phase", /* day/night */);
					System.out.println(objOut2.toJSONString());
					System.out.println(objOut2.toString());
					outToClient.writeBytes(objOut2.toString());
					break;
				case "accepted_proposal" :
					String kpu_id = (String) obj.get("kpu_id");
					if (/* status_ok */) {
						objOut.put("status","ok");
						objOut.put("description","");
					} else if (/* status_fail */){
						objOut.put("status","fail");
						objOut.put("description",/* failDescription */);
					} else {
						objOut.put("status","error");
						objOut.put("description", /* errorDescription */);
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());
					outToClient.writeBytes(objOut.toString());
					if (/* kpu_selected */)
					{
						JSONObject objOut2 = new JSONObject();
						objOut2.put("method","kpu_selected");
						objOut2.put("kpu_id", /* KPU ID */);
						System.out.println(objOut2.toJSONString());
						System.out.println(objOut2.toString());
						outToClient.writeBytes(objOut2.toString());						
					}
					break;
				case "vote_result_werewolf" :
					String vote_status = (String) obj.get("vote_status");
					String vote_result = (String) obj.get("vote_result");
					if (vote_status.equal("1")) {
						String player_killed = (String) obj.get(player_killed);
					}
					if (/* status_ok */) {
						objOut.put("status","ok");
						objOut.put("description","");
					} else if (/* status_fail */){
						objOut.put("status","fail");
						objOut.put("description","");
					} else {
						objOut.put("status","error");
						objOut.put("description","");
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());
					outToClient.writeBytes(objOut.toString());
					if (/* game_over */)
					{
						JSONObject objOut2 = new JSONObject();
						objOut2.put("method","game_over");
						objOut2.put("winner", /* winner */);
						objOut2.put("description","");
						System.out.println(objOut2.toJSONString());
						System.out.println(objOut2.toString());
						outToClient.writeBytes(objOut2.toString());						
					}
					JSONObject objOut3 = new JSONObject();
					objOut3.put("method","change_phase");
					objOut3.put("time", "night");
					objOut3.put("day", /* day */);
					objOut3.put("description","");
					System.out.println(objOut3.toJSONString());
					System.out.println(objOut3.toString());
					outToClient.writeBytes(objOut3.toString());											
					break;
				case "vote_result_civilian" :
					String vote_status = (String) obj.get("vote_status");
					String vote_result = (String) obj.get("vote_result");
					if (vote_status.equal("1")) {
						String player_killed = (String) obj.get(player_killed);
					}
					if (/* status_ok */) {
						objOut.put("status","ok");
						objOut.put("description","");
					} else if (/* status_fail */){
						objOut.put("status","fail");
						objOut.put("description","");
					} else {
						objOut.put("status","error");
						objOut.put("description","");
					}
					System.out.println(objOut.toJSONString());
					System.out.println(objOut.toString());
					outToClient.writeBytes(objOut.toString());
					if (/* game_over */)
					{
						JSONObject objOut2 = new JSONObject();
						objOut2.put("method","game_over");
						objOut2.put("winner", /* winner */);
						objOut2.put("description","");
						System.out.println(objOut2.toJSONString());
						System.out.println(objOut2.toString());
						outToClient.writeBytes(objOut2.toString());						
					}
					JSONObject objOut3 = new JSONObject();
					objOut3.put("method","change_phase");
					objOut3.put("time", "day");
					objOut3.put("day", /* day */);
					objOut3.put("description","");
					System.out.println(objOut3.toJSONString());
					System.out.println(objOut3.toString());
					outToClient.writeBytes(objOut3.toString());											
					break;					
					break;
                default :
                    System.out.println("Wrong syntax");
            }

        }


    }
}
