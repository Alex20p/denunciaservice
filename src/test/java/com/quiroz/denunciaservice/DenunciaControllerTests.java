package com.quiroz.denunciaservice;

import com.quiroz.denunciaservice.controller.DenunciaController;
import com.quiroz.denunciaservice.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class DenunciaControllerTests {

    @Autowired
    private DenunciaController controller;

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    @DisplayName("POST /denuncias/ - Debe crear una denuncia con estado inicial 1 obligatoriamente")
    void testCrearDenunciaExitoso() throws Exception {
        String json = """
                {
                    "dni": "12345678",
                    "fecha": "2026-10-28 10:30:00",
                    "tipo": "Robo al paso",
                    "direccion": "Av. Los Alamos 123",
                    "descripcion": "Sujetos en moto arrebataron celular",
                    "estado": 0
                }
                """;

        mockMvc.perform(post("/denuncias/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.dni").value("12345678"))
                .andExpect(jsonPath("$.tipo").value("Robo al paso"))
                // El estado debe ser 1 aunque se haya enviado 0
                .andExpect(jsonPath("$.estado").value(1));
    }

    @Test
    @DisplayName("POST /denuncias/ - Debe rechazar DNI que no tenga 8 dígitos")
    void testCrearDenunciaDniInvalido() throws Exception {
        String json = """
                {
                    "dni": "1234",
                    "fecha": "2026-10-28 10:30:00",
                    "tipo": "Robo",
                    "direccion": "Av. Principal 456",
                    "descripcion": "Descripción de prueba"
                }
                """;

        mockMvc.perform(post("/denuncias/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("DNI")));
    }

    @Test
    @DisplayName("GET /denuncias/?offset=0&limit=5 - Debe listar de manera paginada y estable")
    void testListarDenunciasPaginado() throws Exception {
        mockMvc.perform(get("/denuncias/")
                        .param("offset", "0")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /denuncias/{id} - Debe retornar 404 para ID inexistente")
    void testBuscarPorIdInexistente() throws Exception {
        mockMvc.perform(get("/denuncias/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /denuncias/ciudadano/{dni} - Debe retornar lista de denuncias por DNI")
    void testBuscarPorDniCiudadano() throws Exception {
        mockMvc.perform(get("/denuncias/ciudadano/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PUT /denuncias/{id} - Debe marcar denuncia como atendida (estado = 0)")
    void testAtenderDenuncia() throws Exception {
        String json = """
                {
                    "dni": "87654321",
                    "fecha": "2026-10-28 11:00:00",
                    "tipo": "Alteración del orden",
                    "direccion": "Calle Las Flores 789",
                    "descripcion": "Ruidos molestos en la vía pública"
                }
                """;

        String responseBody = mockMvc.perform(post("/denuncias/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value(1))
                .andReturn().getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(responseBody);
        assertTrue(matcher.find());
        int id = Integer.parseInt(matcher.group(1));

        // Atender denuncia
        mockMvc.perform(put("/denuncias/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.estado").value(0))
                .andExpect(jsonPath("$.dni").value("87654321"))
                .andExpect(jsonPath("$.tipo").value("Alteración del orden"));
    }
}
