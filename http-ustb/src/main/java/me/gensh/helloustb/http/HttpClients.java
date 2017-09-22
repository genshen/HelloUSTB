package me.gensh.helloustb.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gensh on 2015/11/15.
 * Rewritten with OkHttp by gensh on 2017/9/22.
 */

public class HttpClients {
    private OkHttpClient client;

    /**
     * make a new HttpClients instance,and make a new OkHttpClient instance.
     *
     * @param timeoutCon  connection limit time(ms)
     * @param timeoutRead read html limit time(ms)
     * @return HttpClients instance
     */
    public static HttpClients newInstance(int timeoutCon, int timeoutRead) {
        HttpClients httpClient = new HttpClients();
        CookieJar cookieJar = new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };

        httpClient.client = new OkHttpClient.Builder()
                .connectTimeout(timeoutCon, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutRead, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                .build();
        return httpClient;
    }

    /**
     * @param url     request url
     * @param charset witch decides how to encode return stream
     * @return bufferedReader returned from website the url pointed to
     */
    public BufferedReader get(String url, String charset) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream is = response.body().byteStream();
                if (is != null) {
                    return new BufferedReader(new InputStreamReader(is, charset));  //remember to close it after finishing its usage.
                }
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url     request url
     * @param params  post data
     * @param charset witch decides how to encode return stream
     * @return bufferedReader returned from website the url pointed to
     */
    public BufferedReader post(String url, Map<String, String> params, String charset) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream is = response.body().byteStream();
                if (is != null) {
                    return new BufferedReader(new InputStreamReader(is, charset)); //remember to close it after finishing its usage.
                }
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url test url
     * @return http response code
     */
    public static int testConnection(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(8000, TimeUnit.MILLISECONDS)
                .readTimeout(8000, TimeUnit.MILLISECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            return response.code();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return 500; //inter error.
    }

}
