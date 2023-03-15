package Crypto.Tracker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;

public class CoingeckoAPI {
    private static final String API_URL = "https://api.coingecko.com/api/v3/coins/list";
    private HashMap<String, String[]> cryptoList;

    public void makeApiRequest() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
//        System.out.println(responseBody);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        cryptoList = new HashMap<>();

        for (JsonNode node : rootNode) {
            String id = node.get("id").asText();
            String name = node.get("name").asText();
            String symbol = node.get("symbol").asText();
//            System.out.println(symbol);
            cryptoList.put(id, new String[]{name, symbol});

        }

        System.out.println(cryptoList);
    }


    public HashMap<String, String[]> getCryptoList() {
        return cryptoList;
    }
}
