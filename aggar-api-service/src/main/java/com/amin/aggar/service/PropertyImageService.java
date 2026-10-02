package com.amin.aggar.service;

import com.amin.aggar.domain.entity.Property;
import com.amin.aggar.domain.entity.PropertyImage;
import com.amin.aggar.repository.PropertyImageRepository;
import com.amin.aggar.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyRepository propertyRepository;
    private final String uploadDir = "C:/Users/AHMED/Desktop/aggar-website/uploads/images/";

    public PropertyImageService(PropertyImageRepository propertyImageRepository, PropertyRepository propertyRepository) {
        this.propertyImageRepository = propertyImageRepository;
        this.propertyRepository = propertyRepository;
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public List<String> getImages(Long propertyId) {
        List<PropertyImage> images = propertyImageRepository.findByPropertyId(propertyId);
        List<String> urls = new ArrayList<>();
        for (PropertyImage img : images) {
            urls.add(img.getUrl());
        }
        return urls;
    }

    @Transactional
    public List<String> uploadImages(Long propertyId, MultipartFile[] files) {
        if (files == null || files.length > 8) {
            throw new IllegalArgumentException("A maximum of 8 images can be uploaded");
        }
        for (MultipartFile file : files) {
            if (!file.isEmpty() && !isSupportedImage(file.getContentType())) {
                throw new IllegalArgumentException("Only JPEG, PNG, GIF, WebP, AVIF, and BMP images are allowed");
            }
        }

        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (!propertyOpt.isPresent()) {
            throw new RuntimeException("Property not found");
        }
        Property property = propertyOpt.get();
        List<String> uploadedUrls = new ArrayList<>();

        boolean isFirstImage = propertyImageRepository.findByPropertyId(propertyId).isEmpty();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            try {
                String extension = imageExtension(file.getContentType());
                String fileName = propertyId + "_" + UUID.randomUUID() + extension;
                Path filePath = Paths.get(uploadDir).resolve(fileName);
                Files.copy(file.getInputStream(), filePath);

                String url = "/images/" + fileName;
                PropertyImage image = new PropertyImage();
                image.setProperty(property);
                image.setUrl(url);
                image.setIsPrimary(isFirstImage ? Boolean.TRUE : Boolean.FALSE);
                image.setSortOrder((int) propertyImageRepository.countByPropertyId(propertyId));
                propertyImageRepository.save(image);
                uploadedUrls.add(url);

                if (isFirstImage) {
                    isFirstImage = false;
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
            }
        }
        return uploadedUrls;
    }

    private boolean isSupportedImage(String contentType) {
        return contentType != null && switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/png", "image/gif", "image/webp", "image/avif", "image/bmp" -> true;
            default -> false;
        };
    }

    private String imageExtension(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "image/avif" -> ".avif";
            case "image/bmp" -> ".bmp";
            default -> throw new IllegalArgumentException("Unsupported image type");
        };
    }

    @Transactional
    public void deleteImage(Long propertyId, String imageUrl) {
        propertyImageRepository.deleteByPropertyIdAndUrl(propertyId, imageUrl);
    }
}
