package com.quiroz.denunciaservice.service.impl;

import com.quiroz.denunciaservice.config.OffsetLimitPageRequest;
import com.quiroz.denunciaservice.entity.Denuncia;
import com.quiroz.denunciaservice.exception.BadRequestException;
import com.quiroz.denunciaservice.exception.ResourceNotFoundException;
import com.quiroz.denunciaservice.repository.DenunciaRepository;
import com.quiroz.denunciaservice.service.DenunciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DenunciaServiceImpl implements DenunciaService {

    @Autowired
    private DenunciaRepository repository;

    @Transactional
    @Override
    public Denuncia create(Denuncia denuncia) {
        validateDenuncia(denuncia);
        denuncia.setId(null);
        denuncia.setEstado(1);
        return repository.save(denuncia);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Denuncia> findAll(int offset, int limit) {
        if (offset < 0) {
            throw new BadRequestException("El parámetro offset no puede ser negativo");
        }
        if (limit <= 0) {
            throw new BadRequestException("El parámetro limit debe ser mayor a 0");
        }
        Pageable pageable = new OffsetLimitPageRequest(offset, limit, Sort.by("id").ascending());
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Denuncia findById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El ID proporcionado no es válido: " + id);
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denuncia no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Denuncia> findByDni(String dni) {
        if (dni == null || !dni.trim().matches("^[0-9]{8}$")) {
            throw new BadRequestException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        return repository.findByDni(dni.trim());
    }

    @Transactional
    @Override
    public Denuncia atender(Integer id) {
        Denuncia denuncia = this.findById(id);
        denuncia.setEstado(0);
        return repository.save(denuncia);
    }

    private void validateDenuncia(Denuncia denuncia) {
        if (denuncia == null) {
            throw new BadRequestException("El cuerpo de la denuncia no puede ser nulo");
        }
        if (denuncia.getDni() == null || !denuncia.getDni().trim().matches("^[0-9]{8}$")) {
            throw new BadRequestException("El DNI es obligatorio y debe contener exactamente 8 dígitos numéricos");
        }
        if (denuncia.getFecha() == null) {
            throw new BadRequestException("La fecha es obligatoria y debe tener un formato válido (ejemplo: 'yyyy-MM-dd HH:mm:ss' o 'yyyy-MM-ddTHH:mm:ss')");
        }
        if (denuncia.getTipo() == null || denuncia.getTipo().trim().isEmpty()) {
            throw new BadRequestException("El tipo de denuncia es obligatorio");
        }
        if (denuncia.getTipo().trim().length() > 30) {
            throw new BadRequestException("El tipo de denuncia no puede superar los 30 caracteres");
        }
        if (denuncia.getDireccion() == null || denuncia.getDireccion().trim().isEmpty()) {
            throw new BadRequestException("La dirección es obligatoria");
        }
        if (denuncia.getDireccion().trim().length() > 200) {
            throw new BadRequestException("La dirección no puede superar los 200 caracteres");
        }
        if (denuncia.getDescripcion() == null || denuncia.getDescripcion().trim().isEmpty()) {
            throw new BadRequestException("La descripción es obligatoria");
        }
        if (denuncia.getDescripcion().trim().length() > 255) {
            throw new BadRequestException("La descripción no puede superar los 255 caracteres");
        }
    }
}
