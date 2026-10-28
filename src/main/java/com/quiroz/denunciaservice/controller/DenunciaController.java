package com.quiroz.denunciaservice.controller;

import com.quiroz.denunciaservice.entity.Denuncia;
import com.quiroz.denunciaservice.service.DenunciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/denuncias")
public class DenunciaController {

    @Autowired
    private DenunciaService service;

    // POST /denuncias/ o /denuncias
    @PostMapping({"", "/"})
    public ResponseEntity<Denuncia> create(@RequestBody Denuncia denuncia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(denuncia));
    }

    // GET /denuncias/?offset=0&limit=5 o /denuncias?offset=0&limit=5
    @GetMapping({"", "/"})
    public ResponseEntity<Page<Denuncia>> findAll(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(service.findAll(offset, limit));
    }

    // GET /denuncias/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Denuncia> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // GET /denuncias/ciudadano/{dni}
    @GetMapping("/ciudadano/{dni}")
    public ResponseEntity<List<Denuncia>> findByDni(@PathVariable String dni) {
        return ResponseEntity.ok(service.findByDni(dni));
    }

    // PUT /denuncias/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Denuncia> atender(@PathVariable Integer id) {
        return ResponseEntity.ok(service.atender(id));
    }
}
