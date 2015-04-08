package com.springapp.security;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Apoorv Singh on 4/7/2015.
 */

public class Resource {
    private Integer id;
    private String name;
    private String extension;

    public Resource(String name, String originalName) {
        this.name = name;
        this.extension = originalName.substring(originalName.lastIndexOf('.') + 1);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return "D:\\Projects\\PCSMA\\Assignment 4\\data\\"+this.getId()+"."+this.getExtension();
    }

}