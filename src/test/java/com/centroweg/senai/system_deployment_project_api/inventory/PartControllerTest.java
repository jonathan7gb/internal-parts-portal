package com.centroweg.senai.system_deployment_project_api.inventory;

import static com.centroweg.senai.system_deployment_project_api.inventory.support.InventorySecurityPostProcessors.asAdmin;
import static com.centroweg.senai.system_deployment_project_api.inventory.support.InventorySecurityPostProcessors.asAlmoxarife;
import static com.centroweg.senai.system_deployment_project_api.inventory.support.InventorySecurityPostProcessors.asColaborador;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class PartControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateListAndUpdatePart() throws Exception {
        mockMvc.perform(post("/parts")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "code": "PAR-001",
                                  "name": "Parafuso M8",
                                  "unit": "un",
                                  "qtyMinimum": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("PAR-001"))
                .andExpect(jsonPath("$.qtyInStock").value(0));

        mockMvc.perform(get("/parts").with(asColaborador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        MvcResult created = mockMvc.perform(get("/parts").with(asColaborador())).andReturn();

        String body = created.getResponse().getContentAsString();
        String id = body.substring(body.indexOf("\"id\":\"") + 6, body.indexOf("\"", body.indexOf("\"id\":\"") + 6));

        mockMvc.perform(put("/parts/" + id)
                        .with(asAlmoxarife())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "name": "Parafuso M8 atualizado",
                                  "unit": "un",
                                  "qtyMinimum": 20,
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Parafuso M8 atualizado"));
    }

    @Test
    void shouldRejectDuplicateCode() throws Exception {
        String payload =
                """
                {
                  "code": "DUP-001",
                  "name": "Peça A",
                  "unit": "un",
                  "qtyMinimum": 0
                }
                """;

        mockMvc.perform(post("/parts")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/parts")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict());
    }
}
