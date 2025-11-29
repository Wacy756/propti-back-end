package com.propti.auth.controller;

import com.propti.auth.dto.PropertyDto;
import com.propti.auth.service.PropertyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.propti.auth.dto.CreatePropertyRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping("/landlords/{landlordId}/properties")
    public ResponseEntity<List<PropertyDto>> list(@PathVariable String landlordId) {
        return ResponseEntity.ok(propertyService.listForLandlord(landlordId));
    }

    @PostMapping("/landlords/{landlordId}/properties")
    public ResponseEntity<PropertyDto> create(@PathVariable String landlordId, @RequestBody CreatePropertyRequest request) {
        return ResponseEntity.ok(propertyService.createProperty(landlordId, request));
    }

    @GetMapping("/properties/{id}")
    public ResponseEntity<PropertyDto> getOne(@PathVariable String id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    @PutMapping("/properties/{id}")
    public ResponseEntity<PropertyDto> update(@PathVariable String id, @RequestBody CreatePropertyRequest request) {
        return ResponseEntity.ok(propertyService.updateProperty(id, request));
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
