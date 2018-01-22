package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;


public class Json {

    public Json(){}

    public JSONObject createJson(String[] args){
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("openingValue", args[1]);
        obj.put("closingValue", args[2]);
        obj.put("minimumValue", args[3]);
        obj.put("maximumalue", args[4]);
        return obj;
    }

    public JSONObject parseJson(String json){
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            Object obj = parser.parse(json);
            jsonObject = (JSONObject) obj;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONArray parseArray(String reply) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONArray json = (JSONArray) parser.parse(reply);
        return json;
    }

    public String parseToString(String string){
        JSONObject object = parseJson(string);
        return object.toString();
    }
}

