package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.PropertyImageDto;
import com.amin.aggar.service.PropertyImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/property-images")
public class PropertyImageController {

    private final PropertyImageService service;

    public PropertyImageController(PropertyImageService service) {
        this.service = service;
    }

    @GetMapping
    public Page<PropertyImageDto> list(Pageable pageable) { return service.list(pageable); }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyImageDto> get(@PathVariable Long id) { return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }

    @PostMapping
    public ResponseEntity<PropertyImageDto> create(@RequestBody PropertyImageDto dto) { PropertyImageDto created = service.create(dto); return ResponseEntity.created(URI.create("/api/property-images/" + created.getId())).body(created); }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyImageDto> update(@PathVariable Long id, @RequestBody PropertyImageDto dto) { return service.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { boolean removed = service.delete(id); return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}

