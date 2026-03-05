package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.PropertyDto;
import com.amin.aggar.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    @GetMapping
    public Page<PropertyDto> list(
            @RequestParam(required = false) String listingType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            Pageable pageable) { 
        return service.search(listingType, city, category, q, pageable); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> get(@PathVariable Long id) { return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }

    @PostMapping
    public ResponseEntity<PropertyDto> create(@RequestBody PropertyDto dto) { PropertyDto created = service.create(dto); return ResponseEntity.created(URI.create("/api/properties/" + created.getId())).body(created); }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDto> update(@PathVariable Long id, @RequestBody PropertyDto dto) { return service.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { boolean removed = service.delete(id); return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}

