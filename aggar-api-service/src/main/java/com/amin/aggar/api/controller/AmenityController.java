package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.AmenityDto;
import com.amin.aggar.service.AmenityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @GetMapping
    public Page<AmenityDto> list(Pageable pageable) {
        return amenityService.list(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityDto> get(@PathVariable Integer id) {
        return amenityService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AmenityDto> create(@RequestBody AmenityDto dto) {
        AmenityDto created = amenityService.create(dto);
        return ResponseEntity.created(URI.create("/api/amenities/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmenityDto> update(@PathVariable Integer id, @RequestBody AmenityDto dto) {
        return amenityService.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean removed = amenityService.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

