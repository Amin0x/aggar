package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.StateDto;
import com.amin.aggar.service.StateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/states")
public class StateController {

    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @GetMapping
    public List<StateDto> list() {
        return stateService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StateDto> get(@PathVariable Integer id) {
        return stateService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StateDto> create(@RequestBody StateDto dto) {
        StateDto created = stateService.create(dto);
        return ResponseEntity.created(URI.create("/api/states/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StateDto> update(@PathVariable Integer id, @RequestBody StateDto dto) {
        return stateService.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean removed = stateService.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

