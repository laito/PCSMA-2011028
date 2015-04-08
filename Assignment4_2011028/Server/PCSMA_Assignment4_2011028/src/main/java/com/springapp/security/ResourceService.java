package com.springapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Apoorv Singh on 4/7/2015.
 */

@Service
public class ResourceService {

    private static Integer globalID = 0;
    private static ConcurrentHashMap<Integer, Resource> resources = new ConcurrentHashMap<Integer, Resource>();

    public Status addResource(String name, MultipartFile file) {
        Resource newResource = new Resource(name, file.getOriginalFilename());
        newResource.setId(globalID++);
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(newResource.getLocation())));
                stream.write(bytes);
                stream.close();
                resources.put(newResource.getId(), newResource);
                return new Status(200, "You successfully uploaded " + name + "!");
            } catch (Exception e) {
                return new Status(500, "You failed to upload " + name + " => " + e.getMessage());
            }
        } else {
            return new Status(500, "You failed to upload " + name + " because the file was empty.");
        }
    }

    public Resource getResource(Integer id) {
        return resources.get(id);
    }

    public Status deleteResource(Integer id) {
        if(resources.containsKey(id)) {
            resources.remove(id);
            return new Status(200, "Successfully deleted resource with id: "+id);
        } else {
            return new Status(500, "Could not find resource with id: "+id);
        }
    }

    public List<Resource> getResources() {
        List<Resource> resourceList = new ArrayList<Resource>();
        for(Map.Entry<Integer,Resource> map : resources.entrySet()){
            resourceList.add(map.getValue());
        }
        return resourceList;
    }
}