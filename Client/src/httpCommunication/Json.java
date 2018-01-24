package httpCommunication;


import org.json.JSONObject;
import org.json.JSONArray;



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

    public String getHost(String js) {
        JSONObject o = new JSONObject(js);
        String host = (String) o.get("host");
        return host;
    }

    public int getPort(String js) {
        JSONObject o = new JSONObject(js);
        int port = (int) o.get("port");
        return port;
    }

    public int getExchangeId(String js) {
        JSONObject o = new JSONObject(js);
        int exchangeId = (int) o.get("exchangeId");
        return exchangeId;
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

    public String parseArray(String js){
        JSONObject o = new JSONObject("{list:"+js+"}");
        JSONArray ja = (JSONArray) o.get("list");
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < ja.length();i++){
            JSONObject entry = (JSONObject) ja.get(i);
            String name = (String) entry.get("name");

            double openingValue = (double) entry.get("openingValue");
            double closingValue = (double) entry.get("closingValue");
            double minimumValue = (double) entry.get("minimumValue");
            double maximumValue = (double) entry.get("maximumValue");

            double openingValueY = (double) entry.get("openingValueY");
            double closingValueY = (double) entry.get("closingValueY");
            double minimumValueY = (double) entry.get("minimumValueY");
            double maximumValueY = (double) entry.get("maximumValueY");

            sb.append("Company: ").append(name).append("\n");

            if (openingValue > 0)
                sb.append("OpeningValue: ").append(openingValue).append("\t");
            if (closingValue > 0)
                sb.append("ClosingValue: ").append(closingValue).append("\t");
            if (minimumValue > 0)
                sb.append("MinimumValue: ").append(minimumValue).append("\t");
            if (maximumValue > 0)
                sb.append("MaximumValue: ").append(maximumValue).append("\n");

            if (openingValueY > 0)
                sb.append("OpeningValueY: ").append(openingValueY).append("\t");
            if (closingValueY > 0)
                sb.append("ClosingValueY: ").append(closingValueY).append("\t");
            if (minimumValueY > 0)
                sb.append("MinimumValueY: ").append(minimumValueY).append("\t");
            if (maximumValueY > 0)
                sb.append("MaximumValueY: ").append(maximumValueY).append("\t");
            sb.append("\n\n");
        }
        return sb.toString();
    }


}
