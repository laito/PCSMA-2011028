package com.assignment4.apoorvsingh.assignment4_client;

import com.squareup.okhttp.Response;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

/**
 * Created by Apoorv Singh on 4/7/2015.
 */
public interface SpringAPI {

    @Headers({
            "Accept: application/json",
            "User-Agent: Retrofit-Sample-App"
    })
    @GET("/")
    void resources(
            Callback<List<Resource>> callback
    );


    @Headers({
            "Accept: application/json",
            "User-Agent: Retrofit-Sample-App"
    })
    @GET("/{resourceID}.pdf")
    void resource(
            @Path("resourceID") Integer resourceID,
            Callback<String> callback
    );

    @Headers({
            "Accept: application/json",
            "User-Agent: Retrofit-Sample-App"
    })
    @DELETE("/{resourceID}")
    void delete(
            @Path("resourceID") Integer resourceID,
            Callback<String> callback
    );


    @Headers({
            "Accept: application/json",
            "User-Agent: Retrofit-Sample-App"
    })
    @POST("/j_spring_security_check")
    void login(
        @Body LoginRequest login,
        Callback<String> callback
    );

    @Multipart
    @POST("/upload")
    void upload(
        @Part("name") String name,
        @Part("file") TypedFile resource,
        Callback<String> callback
    );
}
