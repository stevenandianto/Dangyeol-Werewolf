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
        ClientsThread ct = new ClientsThread();
        ct.start();

    }
}