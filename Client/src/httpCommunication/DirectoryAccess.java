package httpCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectoryAccess {


    public DirectoryAccess(){

    }

    public String sendRequest(String request, String type)throws IOException {
        URL url = new URL("http://192.168.1.127:8080/" + request);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(type);
        con.setRequestProperty("Content-Type", "application/json");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
