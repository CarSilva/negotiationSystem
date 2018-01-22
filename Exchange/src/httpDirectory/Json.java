package httpDirectory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Json {

    public Json(){}

    public String createJsonTotal(String[] args){
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("openingValue", args[1]);
        obj.put("closingValue", args[2]);
        obj.put("minimumValue", args[3]);
        obj.put("maximumalue", args[4]);
        return obj.toString();
    }

    public String createJsonMin(String[] args){
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("minimumValue", args[1]);
        return obj.toString();
    }

    public String createJsonMax(String[] args){
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("maximumValue", args[1]);
        return obj.toString();
    }

    public String createJsonOpening(String[] args){
        JSONObject obj = new JSONObject();

        obj.put("name", args[0]);
        obj.put("openingValue", args[1]);
        return obj.toString();
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
}

