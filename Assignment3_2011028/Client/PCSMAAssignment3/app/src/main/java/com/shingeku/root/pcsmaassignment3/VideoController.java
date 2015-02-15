package com.shingeku.root.pcsmaassignment3;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by root on 2/12/2015.
 */
public class VideoController {

    public static void getVideos(Callback callback) {
        String endpoint = "VideoServlet";
        URLContext videosContext = new URLContext(Main.MovieServiceURL+endpoint, "GET", callback);
        new AsyncHTTP(callback).execute(videosContext);
    }

    public static void getVideo(int videoID, Callback callback) {
        String endpoint = "VideoServlet?id="+videoID;
        URLContext videoContext = new URLContext(Main.MovieServiceURL+endpoint, "GET", callback);
        new AsyncHTTP(callback).execute(videoContext);
    }

    public static void postVideo(List<NameValuePair> videoData, Callback callback) {
        String endpoint = "VideoServlet";
        URLContext videoContext = new URLContext(Main.MovieServiceURL+endpoint, "POST", callback);
        videoContext.setData(videoData);
        new AsyncHTTP(callback).execute(videoContext);
    }

    public static void putVideo(List<NameValuePair> videoData, int videoID, Callback callback) {
        String endpoint = "VideoServlet?id="+videoID;
        URLContext videoContext = new URLContext(Main.MovieServiceURL+endpoint, "PUT", callback);
        videoContext.setData(videoData);
        new AsyncHTTP(callback).execute(videoContext);
    }

    public static void deleteVideo(int videoID, Callback callback) {
        String endpoint = "VideoServlet?id="+videoID;
        URLContext videosContext = new URLContext(Main.MovieServiceURL+endpoint, "DELETE", callback);
        new AsyncHTTP(callback).execute(videosContext);
    }

}
