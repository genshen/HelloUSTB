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

import me.gensh.helloustb.http.resolver.GetResolver;
import me.gensh.helloustb.http.resolver.PostResolver;
import me.gensh.helloustb.http.resolver.ResponseResolveException;
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
    final public static int HTTP_GET = 1, HTTP_POST = 2;
    final public static String CHARSET_BTF8 = "utf-8", CHARSET_GB2312 = "gb2312", CHARSET_GBK = "gbk";
    private static OkHttpClient client;
    private BufferedReader br;

    /**
     * make a new HttpClients instance,and make a new OkHttpClient instance.
     *
     * @param timeoutCon  connection limit time(ms)
     * @param timeoutRead read html limit time(ms)
     * @return HttpClients instance
     */
    public synchronized static HttpClients newInstance(int timeoutCon, int timeoutRead) {
        HttpClients httpClient = new HttpClients();
        if (client == null) {
            CookieJar cookieJar = new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                    /** todo cookie manager: put method would clear previous Cookies,
                     * which will lead an empty data in http://e.ustb.edu.cn/index.portal?.pn=p378_p381.*/
                }

                @Override
                public synchronized List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            };

            client = new OkHttpClient.Builder()
                    .connectTimeout(timeoutCon, TimeUnit.MILLISECONDS)
                    .readTimeout(timeoutRead, TimeUnit.MILLISECONDS)
                    .cookieJar(cookieJar)
                    .build();
        }
        return httpClient;
    }

    /**
     * send request and set BufferedReader for var br.
     *
     * @param url         request url
     * @param requestType http request type,GET or POST
     * @param charset     charset decides how to encode http response stream
     * @param params      http get params or post params
     * @return resolved the http response,and return.
     */
    public HttpClients request(String url, int requestType, String charset, Map<String, String> params) throws PendingException {
        if (requestType == HTTP_POST) {  //for post response
            try {  //initial BufferedReader for POST ,or throw PendingException.
                InputStream is = post(url, params);
                if (is != null) {
                    br = new BufferedReader(new InputStreamReader(is, charset));  //remember to close it after finishing its usage.
                    return this;
                } else {
                    throw new PendingException("got null InputStream while http POST request.");
                }
            } catch (IOException e) {
                PendingException pe = new PendingException(e.getMessage());
                pe.setStackTrace(e.getStackTrace());
                throw pe; //throw exception to higher level.
            }
        } else {  // (requestType == HTTP_GET)  process get response sa default.
            try {  //initial BufferedReader for GET ,or throw PendingException.
                InputStream is = get(url, params);
                if (is != null) {
                    br = new BufferedReader(new InputStreamReader(is, charset));  //remember to close it after finishing its usage.
                    return this;
                } else {// maybe connection timeout error
                    throw new PendingException("got null InputStream while http GET request.");
                }
            } catch (IOException e) {
                PendingException pe = new PendingException(e.getMessage());
                pe.setStackTrace(e.getStackTrace());
                throw pe; //throw exception to higher level.
            }
        }
    }

    /**
     * resolve html data from BufferedReader br.
     * return NonNull ResolvedData,or throws Exception
     *
     * @param requestType http request type,GET or POST.
     * @param id          it will process different http response data via this id.
     * @return NonNull ResolvedData: resolved data from  BufferedReader br.
     * @throws ResponseResolveException exception while resolving http response
     */
    public ResolvedData resolve(int requestType, int id) throws ResponseResolveException {
        try {
            if (requestType == HTTP_POST) {
                return PostResolver.resolve(br, id);
            } else { //(requestType == HTTP_GET)  process get response sa default.
                ResolvedData resolvedData = new ResolvedData();
                resolvedData.data = GetResolver.resolve(br, id);
                return resolvedData;
            }
        } catch (IOException e) {
            ResponseResolveException re = new ResponseResolveException(e.getMessage());
            re.setStackTrace(e.getStackTrace());
            throw re; //throw exception to higher level.
        }
    }

    /**
     * @param url    request url
     * @param params http get params
     * @return InputStream returned from website the url pointed to
     */
    public InputStream get(String url, Map<String, String> params) throws IOException {
        if (params == null) {
            return get(url);
        }
        boolean hasParams = false;
        StringBuilder paramsBuilder = new StringBuilder();
        for (String key : params.keySet()) { //paramsBuilder.toString() is not ""
            if (hasParams) {
                paramsBuilder.append("&");
            }
            hasParams = true;
            paramsBuilder.append(String.format("%s=%s", key, params.get(key)));
        }
        return get(url + "?" + paramsBuilder.toString());
    }

    /**
     * @param url request url
     * @return InputStream returned from website the url pointed to
     */
    public InputStream get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().byteStream();
        }
        response.close();
        return null;
    }

    /**
     * @param url    request url
     * @param params post data
     * @return InputStream returned from website the url pointed to
     */
    public InputStream post(String url, Map<String, String> params) throws IOException {
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
        //try
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().byteStream();
        }
        response.close();
        // catch
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
            e.printStackTrace();
        }
        return 500; //inter error.
    }

}
