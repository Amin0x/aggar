package com.amin.aggar.api.controller;

import com.amin.aggar.service.PropertyImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("@propertyAuthorization.canView(#propertyId, authentication)")
    public List<String> getImages(@RequestParam("propertyId") Long propertyId) {
        return service.getImages(propertyId);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or @propertyAuthorization.canManage(#propertyId, authentication.name)")
    public ResponseEntity<List<String>> uploadImages(
            @RequestParam("propertyId") Long propertyId,
            @RequestParam("files") MultipartFile[] files) {
        List<String> urls = service.uploadImages(propertyId, files);
        return ResponseEntity.created(URI.create("/api/property-images?propertyId=" + propertyId)).body(urls);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN') or @propertyAuthorization.canManage(#propertyId, authentication.name)")
    public ResponseEntity<Void> deleteImage(
            @RequestParam("propertyId") Long propertyId,
            @RequestParam("imageUrl") String imageUrl) {
        service.deleteImage(propertyId, imageUrl);
        return ResponseEntity.noContent().build();
    }
}
