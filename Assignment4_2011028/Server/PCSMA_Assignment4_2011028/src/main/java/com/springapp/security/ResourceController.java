package com.springapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by Apoorv Singh on 4/7/2015.
 */

@Controller
@EnableWebMvc
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value="/upload", method=RequestMethod.POST, headers="Accept=*/*")
    public @ResponseBody Status handleFileUpload(@RequestParam("name") String name, @RequestParam("file") MultipartFile file){
        System.out.println("WUWWWWW");
        System.out.println(name);
        return resourceService.addResource(name, file);
    }

    @RequestMapping(value="/", method=RequestMethod.GET, headers="Accept=*/*")
    public @ResponseBody List<Resource> listResources() {
        List<Resource> x = resourceService.getResources();
        for(Resource y : x) {
            System.out.println(y.getId());
        }
        return resourceService.getResources();
    }

    @RequestMapping(value="/details/{resourceId}", method=RequestMethod.GET, headers="Accept=*/*")
    public @ResponseBody Resource getResource(@PathVariable Integer resourceId) {
        return resourceService.getResource(resourceId);
    }

    // Get method on an ID -> Show that particular media
    @RequestMapping(value = "/{resourceId}.pdf", method = RequestMethod.GET)
    public void printResource(@PathVariable Integer resourceId, HttpServletResponse response) {
        System.out.println("HOHOHOHOHOHO");
        System.out.println(resourceId);
        try {
            Resource myResource = resourceService.getResource(resourceId);
            FileInputStream stream = new FileInputStream(myResource.getLocation());
            OutputStream responseStream = response.getOutputStream();
            byte[] buffer = new byte[1024*1024];
            int bytesRead = -1;
            // write bytes read from the input stream into the output stream
            while ((bytesRead = stream.read(buffer)) != -1) {
                responseStream.write(buffer, 0, bytesRead);
            }
            stream.close();
            responseStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/{resourceId}", method=RequestMethod.DELETE, headers="Accept=*/*")
    public @ResponseBody Status deleteResource(@PathVariable Integer resourceId) {
        System.out.println("DELETE DELETE DELETE!");
        System.out.println(resourceId);
        return resourceService.deleteResource(resourceId);
    }
}