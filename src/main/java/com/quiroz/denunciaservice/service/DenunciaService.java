package com.quiroz.denunciaservice.service;

import com.quiroz.denunciaservice.entity.Denuncia;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DenunciaService {
    Denuncia create(Denuncia denuncia);
    Page<Denuncia> findAll(int offset, int limit);
    Denuncia findById(Integer id);
    List<Denuncia> findByDni(String dni);
    Denuncia atender(Integer id);
}
