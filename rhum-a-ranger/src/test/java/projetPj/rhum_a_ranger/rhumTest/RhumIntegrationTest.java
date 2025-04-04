package projetPj.rhum_a_ranger.rhumTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import projetPj.rhum_a_ranger.rhum.Rhum;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"  // Désactiver Spring Security
})
@Transactional
public class RhumIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRhumCRUDOperations() throws Exception {
        // Créer un nouveau rhum via l'API
        Rhum newRhum = new Rhum();
        newRhum.setName("Trois Rivières");
        newRhum.setOrigin("Martinique");
        newRhum.setDescription("Rhum agricole AOC");
        newRhum.setAlcoholDegree("45");
        newRhum.setYear(2016);

        String rhumJson = objectMapper.writeValueAsString(newRhum);

        // Test création (POST)
        String responseContent = mockMvc.perform(post("/api/rhums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rhumJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Trois Rivières")))
                .andReturn().getResponse().getContentAsString();

        Rhum createdRhum = objectMapper.readValue(responseContent, Rhum.class);
        Long rhumId = createdRhum.getId();

        // Test lecture (GET one)
        mockMvc.perform(get("/api/rhums/{id}", rhumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(rhumId.intValue())))
                .andExpect(jsonPath("$.name", is("Trois Rivières")));

        // Test mise à jour (PUT)
        createdRhum.setName("Trois Rivières VSOP");
        createdRhum.setDescription("Rhum agricole AOC vieilli");

        mockMvc.perform(put("/api/rhums/{id}", rhumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdRhum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Trois Rivières VSOP")))
                .andExpect(jsonPath("$.description", is("Rhum agricole AOC vieilli")));

        // Test lecture de tous (GET all)
        mockMvc.perform(get("/api/rhums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + rhumId + ")].name", hasItem("Trois Rivières VSOP")));

        // Test recherche par origin
        mockMvc.perform(get("/api/rhums/origin/{origin}", "Martinique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + rhumId + ")].name", hasItem("Trois Rivières VSOP")));

        // Test recherche par mot-clé
        mockMvc.perform(get("/api/rhums/search").param("keyword", "VSOP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + rhumId + ")].name", hasItem("Trois Rivières VSOP")));

        // Test suppression (DELETE)
        mockMvc.perform(delete("/api/rhums/{id}", rhumId))
                .andExpect(status().isNoContent());

        // Vérifier que le rhum a bien été supprimé
        mockMvc.perform(get("/api/rhums/{id}", rhumId))
                .andExpect(status().isNotFound());
    }
}
