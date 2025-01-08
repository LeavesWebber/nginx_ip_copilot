package org.example.backend.IPzonemanagement;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/geo-rules")
public class IPzoneManagementController {

    @Autowired
    private IPzoneManagementService ipzoneManagementService;

    @GetMapping
    public ResponseEntity<List<GeoRule>> getAllGeoRules() {
        List<GeoRule> geoRules = ipzoneManagementService.getAllGeoRules();
        return ResponseEntity.ok(geoRules);
    }

    @PostMapping
    public ResponseEntity<GeoRule> addGeoRule(@RequestBody GeoRule geoRule) throws IOException {
        GeoRule savedGeoRule = ipzoneManagementService.addGeoRule(geoRule);
        return ResponseEntity.ok(savedGeoRule);
    }

    @DeleteMapping("/{rule_id}")
    public ResponseEntity<Void> deleteGeoRule(@PathVariable String rule_id) throws IOException {
        ipzoneManagementService.deleteGeoRule(rule_id);
        return ResponseEntity.noContent().build();
    }

}