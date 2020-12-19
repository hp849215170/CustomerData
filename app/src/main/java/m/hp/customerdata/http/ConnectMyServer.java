package m.hp.customerdata.http;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectMyServer {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private String url;

    public ConnectMyServer() {
    }

    public ConnectMyServer(String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String postData(String json) {

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
