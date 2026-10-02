package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.CityDto;
import com.amin.aggar.service.CityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public Page<CityDto> list(Pageable pageable) {
        return cityService.list(pageable);
    }

    @GetMapping("/by-state/{stateId}")
    public List<CityDto> listByState(@PathVariable("stateId") Integer stateId) {
        return cityService.findByStateId(stateId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDto> get(@PathVariable("id") Integer id) {
        return cityService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CityDto> create(@RequestBody CityDto dto) {
        CityDto created = cityService.create(dto);
        return ResponseEntity.created(URI.create("/api/cities/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityDto> update(@PathVariable("id") Integer id, @RequestBody CityDto dto) {
        return cityService.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        boolean removed = cityService.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
