package httpCommunication;

import org.json.JSONArray;
import org.json.JSONObject;


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

    public String parseArray(String js){
        JSONObject o = new JSONObject(js);
        JSONArray ja = (JSONArray) o.get("");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < ja.length();i++){
            JSONObject entry = (JSONObject) ja.get(i);
            String name = (String) entry.get("name");
            double openingValue = (double) entry.get("openingValue");
            double closingValue = (double) entry.get("closingValue");
            double minimumValue = (double) entry.get("minimumValue");
            double maximumValue = (double) entry.get("maximumValue");
            sb.append("Company:").append(name).append(" ");
            sb.append("OpeningValue:").append(openingValue).append(" ");
            sb.append("ClosingValue:").append(closingValue).append(" ");
            sb.append("MinimumValue:").append(minimumValue).append(" ");
            sb.append("MaximumValue:").append(maximumValue).append(" ");
            sb.append("\n\n");
        }
        return sb.toString();
    }
}

