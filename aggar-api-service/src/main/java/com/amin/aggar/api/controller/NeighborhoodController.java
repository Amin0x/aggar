package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.NeighborhoodDto;
import com.amin.aggar.service.NeighborhoodService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/neighborhoods")
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;

    public NeighborhoodController(NeighborhoodService neighborhoodService) {
        this.neighborhoodService = neighborhoodService;
    }

    @GetMapping
    public Page<NeighborhoodDto> list(Pageable pageable) {
        return neighborhoodService.list(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NeighborhoodDto> get(@PathVariable Integer id) {
        return neighborhoodService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NeighborhoodDto> create(@RequestBody NeighborhoodDto dto) {
        NeighborhoodDto created = neighborhoodService.create(dto);
        return ResponseEntity.created(URI.create("/api/neighborhoods/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NeighborhoodDto> update(@PathVariable Integer id, @RequestBody NeighborhoodDto dto) {
        return neighborhoodService.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean removed = neighborhoodService.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

