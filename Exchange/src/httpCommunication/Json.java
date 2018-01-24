package httpCommunication;

import exchange.Share;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Json {


    public Json() {
    }

    public String createJsonTotal(String[] args) {
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("openingValue", args[1]);
        obj.put("closingValue", args[2]);
        obj.put("minimumValue", args[3]);
        obj.put("maximumalue", args[4]);
        return obj.toString();
    }

    public String createJsonMin(String[] args) {
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("minimumValue", args[1]);
        return obj.toString();
    }

    public String createJsonMax(String[] args) {
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("maximumValue", args[1]);
        return obj.toString();
    }

    public String createJsonOpening(String[] args) {
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("openingValue", args[1]);
        return obj.toString();
    }

    public List<String> parseArray(String js){
        JSONObject o = new JSONObject(js);
        JSONArray ja = (JSONArray) o.get("companies");
        List<String> list = new ArrayList<>();
        for(int i = 0; i < ja.length();i++){
            String name = (String) ja.get(i);
            list.add(name);
        }
        return list;
    }
}

