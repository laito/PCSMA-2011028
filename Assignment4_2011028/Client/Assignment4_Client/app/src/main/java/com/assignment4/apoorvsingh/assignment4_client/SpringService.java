package com.assignment4.apoorvsingh.assignment4_client;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Apoorv Singh on 4/7/2015.
 */
public class SpringService {
    private static String cookies;

    public static String getCookies() {
        return cookies;
    }

    public static void setCookies(String cookies) {
        SpringService.cookies = cookies;
    }

    /**
     * Injects cookies to every request
     */
    private static final RequestInterceptor COOKIES_REQUEST_INTERCEPTOR = new RequestInterceptor() {
        @Override
        public void intercept(RequestInterceptor.RequestFacade request) {
            if (null != cookies && cookies.length() > 0) {
                request.addHeader("Cookie", cookies);
            }
        }
    };

    private static String API_URL = "http://192.168.53.188:8080";

    public static SpringAPI getService()
    {
        OkHttpClient client = new OkHttpClient();
        client.setFollowRedirects(false);

        return new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setClient(new OkClient(client))
                .setRequestInterceptor(COOKIES_REQUEST_INTERCEPTOR)
                .build()
                .create(SpringAPI.class);
    }
}