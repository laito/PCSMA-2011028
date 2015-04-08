package com.assignment4.apoorvsingh.assignment4_client;

/**
 * Created by Apoorv Singh on 4/7/2015.
 */
public class Resource {
    private Integer id;
    private String name;
    private String extension;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
