package projetPj.rhum_a_ranger.rhumTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import projetPj.rhum_a_ranger.config.H2TestConfig;
import projetPj.rhum_a_ranger.rhum.Rhum;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(H2TestConfig.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
        "spring.jpa.show-sql=true"
})
@Transactional
public class RhumIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    private Rhum createTestRhum() {
        Rhum rhum = new Rhum();
        rhum.setName("Trois Rivières");
        rhum.setOrigin("Martinique");
        rhum.setDescription("Rhum agricole AOC");
        rhum.setAlcoholDegree("45");
        rhum.setAge(2016);
        return rhum;
    }

    private Long createRhumViaApi() throws Exception {
        Rhum newRhum = createTestRhum();
        String rhumJson = objectMapper.writeValueAsString(newRhum);

        MvcResult result = mockMvc.perform(post("/api/rhums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rhumJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Trois Rivières")))
                .andReturn();

        Rhum createdRhum = objectMapper.readValue(result.getResponse().getContentAsString(), Rhum.class);
        return createdRhum.getId();
    }

    @Test
    public void testCreateRhum() throws Exception {
        // Créer un nouveau rhum via l'API
        Rhum newRhum = createTestRhum();
        String rhumJson = objectMapper.writeValueAsString(newRhum);

        mockMvc.perform(post("/api/rhums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rhumJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Trois Rivières")))
                .andExpect(jsonPath("$.origin", is("Martinique")))
                .andExpect(jsonPath("$.description", is("Rhum agricole AOC")))
                .andExpect(jsonPath("$.alcoholDegree", is("45")))
                .andExpect(jsonPath("$.age", is(2016)));
    }

    @Test
    public void testGetRhum() throws Exception {
        // D'abord créer un rhum via l'API
        Long rhumId = createRhumViaApi();

        // Tester la récupération du rhum par son ID
        mockMvc.perform(get("/api/rhums/{id}", rhumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(rhumId.intValue())))
                .andExpect(jsonPath("$.name", is("Trois Rivières")))
                .andExpect(jsonPath("$.origin", is("Martinique")));
    }

    @Test
    public void testGetAllRhums() throws Exception {
        // D'abord créer un rhum via l'API
        Long rhumId = createRhumViaApi();

        // Tester la récupération de tous les rhums
        mockMvc.perform(get("/api/rhums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + rhumId + ")].name", hasItem("Trois Rivières")));
    }

    @Test
    public void testUpdateRhum() throws Exception {
        // D'abord créer un rhum via l'API
        Long rhumId = createRhumViaApi();

        // Récupérer le rhum créé
        MvcResult getResult = mockMvc.perform(get("/api/rhums/{id}", rhumId))
                .andExpect(status().isOk())
                .andReturn();

        Rhum existingRhum = objectMapper.readValue(getResult.getResponse().getContentAsString(), Rhum.class);

        // Modifier le rhum
        existingRhum.setName("Trois Rivières VSOP");
        existingRhum.setDescription("Rhum agricole AOC vieilli");

        // Tester la mise à jour du rhum
        mockMvc.perform(put("/api/rhums/{id}", rhumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingRhum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Trois Rivières VSOP")))
                .andExpect(jsonPath("$.description", is("Rhum agricole AOC vieilli")))
                .andExpect(jsonPath("$.origin", is("Martinique")));
    }

    @Test
    public void testDeleteRhum() throws Exception {
        // D'abord créer un rhum via l'API
        Long rhumId = createRhumViaApi();

        // Tester la suppression du rhum
        mockMvc.perform(delete("/api/rhums/{id}", rhumId))
                .andExpect(status().isNoContent());

        // Vérifier que le rhum a bien été supprimé
        mockMvc.perform(get("/api/rhums/{id}", rhumId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRhumNotFound() throws Exception {
        mockMvc.perform(delete("/api/rhums/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchRhumByOrigin() throws Exception {
        // D'abord créer un rhum via l'API
        Long rhumId = createRhumViaApi();

        // Tester la recherche par origine
        mockMvc.perform(get("/api/rhums/origin/{origin}", "Martinique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + rhumId + ")].name", hasItem("Trois Rivières")));
    }

    @Test
    public void testSearchRhumByKeyword() throws Exception {
        // D'abord créer un rhum via l'API
        Long rhumId = createRhumViaApi();

        // Puis mettre à jour le rhum pour ajouter un mot-clé de recherche
        MvcResult getResult = mockMvc.perform(get("/api/rhums/{id}", rhumId))
                .andExpect(status().isOk())
                .andReturn();

        Rhum existingRhum = objectMapper.readValue(getResult.getResponse().getContentAsString(), Rhum.class);
        existingRhum.setName("Trois Rivières VSOP");

        mockMvc.perform(put("/api/rhums/{id}", rhumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingRhum)))
                .andExpect(status().isOk());

        // Tester la recherche par mot-clé
        mockMvc.perform(get("/api/rhums/search").param("keyword", "VSOP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + rhumId + ")].name", hasItem("Trois Rivières VSOP")));
    }

    @Test
    public void testGetRhumNotFound() throws Exception {
        mockMvc.perform(get("/api/rhums/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchRhumWithNoResult() throws Exception {
        mockMvc.perform(get("/api/rhums/search")
                        .param("keyword", "Inconnu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
