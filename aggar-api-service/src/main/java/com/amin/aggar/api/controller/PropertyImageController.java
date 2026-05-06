package com.amin.aggar.api.controller;

import com.amin.aggar.service.PropertyImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/property-images")
public class PropertyImageController {

    private final PropertyImageService service;

    public PropertyImageController(PropertyImageService service) {
        this.service = service;
    }

    @GetMapping
    public List<String> getImages(@RequestParam Long propertyId) {
        return service.getImages(propertyId);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImages(
            @RequestParam Long propertyId,
            @RequestParam("files") MultipartFile[] files) {
        List<String> urls = service.uploadImages(propertyId, files);
        return ResponseEntity.created(URI.create("/api/property-images?propertyId=" + propertyId)).body(urls);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImage(
            @RequestParam Long propertyId,
            @RequestParam String imageUrl) {
        service.deleteImage(propertyId, imageUrl);
        return ResponseEntity.noContent().build();
    }
}
