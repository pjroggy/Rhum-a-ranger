package projetPj.rhum_a_ranger.rhumTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import projetPj.rhum_a_ranger.rhum.Rhum;
import projetPj.rhum_a_ranger.rhum.RhumController;
import projetPj.rhum_a_ranger.rhum.RhumService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RhumController.class)
@AutoConfigureMockMvc
@WithMockUser
public class RhumControllerTest {

    // Configuration de test pour remplacer @MockBean
    @TestConfiguration
    static class TestConfig {
        @Bean
        public RhumService rhumService() {
            return Mockito.mock(RhumService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RhumService rhumService; // Injecté depuis TestConfig

    @Autowired
    private ObjectMapper objectMapper;

    private Rhum rhum1;
    private Rhum rhum2;

    @BeforeEach
    void setUp() {
        // Réinitialiser les mocks avant chaque test
        Mockito.reset(rhumService);

        rhum1 = new Rhum();
        rhum1.setId(1L);
        rhum1.setName("Clement");
        rhum1.setOrigin("Martinique");
        rhum1.setDescription("Rhum agricole");
        rhum1.setAlcoholDegree("40");
        rhum1.setYear(2018);

        rhum2 = new Rhum();
        rhum2.setId(2L);
        rhum2.setName("Diplomatico");
        rhum2.setOrigin("Venezuela");
        rhum2.setDescription("Rhum traditionnel");
        rhum2.setAlcoholDegree("43");
        rhum2.setYear(2010);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/rhums - Devrait retourner tous les rhums")
    void getAllRhums_shouldReturnAllRhums() throws Exception {
        List<Rhum> rhums = Arrays.asList(rhum1, rhum2);
        when(rhumService.getAllRhums()).thenReturn(rhums);

        mockMvc.perform(get("/api/rhums"))
                .andDo(MockMvcResultHandlers.print()) // Afficher les détails de la réponse
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$", hasSize(2), "La liste devrait contenir 2 rhums"))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nom", is("Clement")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nom", is("Diplomatico")));

        verify(rhumService, times(1)).getAllRhums();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/rhums/{id} - Devrait retourner un rhum quand il existe")
    void getRhumById_shouldReturnRhum_whenRhumExists() throws Exception {
        when(rhumService.getRhumById(1L)).thenReturn(Optional.of(rhum1));

        mockMvc.perform(get("/api/rhums/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nom", is("Clement")))
                .andExpect(jsonPath("$.origine", is("Martinique")));

        verify(rhumService, times(1)).getRhumById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRhumById_shouldReturn404_whenRhumDoesNotExist() throws Exception {
        when(rhumService.getRhumById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rhums/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(rhumService, times(1)).getRhumById(99L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRhum_shouldCreateRhum() throws Exception {
        when(rhumService.saveRhum(any(Rhum.class))).thenReturn(rhum1);

        mockMvc.perform(post("/api/rhums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rhum1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nom", is("Clement")));

        verify(rhumService, times(1)).saveRhum(any(Rhum.class));
    }

    @Test
    void updateRhum_shouldUpdateRhum_whenRhumExists() throws Exception {
        when(rhumService.updateRhum(eq(1L), any(Rhum.class))).thenReturn(rhum1);

        mockMvc.perform(put("/api/rhums/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rhum1)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nom", is("Clement")));

        verify(rhumService, times(1)).updateRhum(eq(1L), any(Rhum.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateRhum_shouldReturn404_whenRhumDoesNotExist() throws Exception {
        when(rhumService.updateRhum(eq(99L), any(Rhum.class))).thenReturn(null);

        mockMvc.perform(put("/api/rhums/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rhum1)))
                .andExpect(status().isNotFound());

        verify(rhumService, times(1)).updateRhum(eq(99L), any(Rhum.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteRhum_shouldDeleteRhum() throws Exception {
        doNothing().when(rhumService).deleteRhum(anyLong());

        mockMvc.perform(delete("/api/rhums/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(rhumService, times(1)).deleteRhum(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRhumsByOrigin_shouldReturnRhumsByOrigin() throws Exception {
        List<Rhum> martinique = Arrays.asList(rhum1);
        when(rhumService.getRhumsByOrigin("Martinique")).thenReturn(martinique);

        mockMvc.perform(get("/api/rhums/origine/{origine}", "Martinique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].origine", is("Martinique")));

        verify(rhumService, times(1)).getRhumsByOrigin("Martinique");
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchRhumsByName_shouldReturnRhumsByNameContaining() throws Exception {
        List<Rhum> diplomaticoRhums = Arrays.asList(rhum2);
        when(rhumService.searchRhumsByName("Diplo")).thenReturn(diplomaticoRhums);

        mockMvc.perform(get("/api/rhums/search").param("keyword", "Diplo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is("Diplomatico")));

        verify(rhumService, times(1)).searchRhumsByName("Diplo");
    }
}