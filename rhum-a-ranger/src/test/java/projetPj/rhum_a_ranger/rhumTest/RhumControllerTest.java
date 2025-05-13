package projetPj.rhum_a_ranger.rhumTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import projetPj.rhum_a_ranger.config.TestSecurityConfig;
import projetPj.rhum_a_ranger.rhum.RhumController;
import projetPj.rhum_a_ranger.rhum.RhumDto;
import projetPj.rhum_a_ranger.rhum.RhumService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RhumController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RhumControllerTest {

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
    private RhumService rhumService;

    @Autowired
    private ObjectMapper objectMapper;

    private RhumDto rhum1;
    private RhumDto rhum2;

    @BeforeEach
    void setUp() {
        Mockito.reset(rhumService);

        rhum1 = new RhumDto(
                1L,
                "Clement",
                "Martinique",
                "Rhum agricole",
                null,
                2018,
                "40"
        );

        rhum2 = new RhumDto(
                2L,
                "Diplomatico",
                "Venezuela",
                "Rhum traditionnel",
                null,
                2010,
                "43"
        );
    }

    @Test
    @DisplayName("GET /api/rhums - Devrait retourner tous les rhums")
    void getAllRhums_shouldReturnAllRhums() throws Exception {
        List<RhumDto> rhums = Arrays.asList(rhum1, rhum2);
        when(rhumService.getAllRhums()).thenReturn(rhums);

        mockMvc.perform(get("/api/rhums")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andDo(MockMvcResultHandlers.print()) // Afficher les détails de la réponse
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Clement")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Diplomatico")));

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
                .andExpect(jsonPath("$.name", is("Clement")))
                .andExpect(jsonPath("$.origin", is("Martinique")));

        verify(rhumService, times(1)).getRhumById(1L);
    }

    @Test
    void getRhumById_shouldReturn404_whenRhumDoesNotExist() throws Exception {
        when(rhumService.getRhumById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rhums/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(rhumService, times(1)).getRhumById(99L);
    }

    @Test
    void createRhum_shouldCreateRhum() throws Exception {
        when(rhumService.saveRhum(any(RhumDto.class))).thenReturn(rhum1);

        mockMvc.perform(post("/api/rhums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rhum1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Clement")));

        verify(rhumService, times(1)).saveRhum(any(RhumDto.class));
    }

    @Test
    void updateRhum_shouldUpdateRhum_whenRhumExists() throws Exception {
        when(rhumService.updateRhum(eq(1L), any(RhumDto.class))).thenReturn(rhum1);

        mockMvc.perform(put("/api/rhums/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rhum1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Clement")));

        verify(rhumService, times(1)).updateRhum(eq(1L), any(RhumDto.class));
    }

    @Test
    void updateRhum_shouldReturn404_whenRhumDoesNotExist() throws Exception {
        when(rhumService.updateRhum(eq(99L), any(RhumDto.class))).thenThrow(new RuntimeException("Rhum not found"));

        mockMvc.perform(put("/api/rhums/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rhum1)))
                .andExpect(status().isNotFound());

        verify(rhumService, times(1)).updateRhum(eq(99L), any(RhumDto.class));
    }

    @Test
    void deleteRhum_shouldDeleteRhum() throws Exception {
        doNothing().when(rhumService).deleteRhum(1L);

        mockMvc.perform(delete("/api/rhums/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(rhumService, times(1)).deleteRhum(1L);
    }

    @Test
    void deleteRhum_shouldReturnNoContentForExistingRhum() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(rhumService).deleteRhum(id);

        // Act & Assert
        mockMvc.perform(delete("/api/rhums/{id}", id))
                .andExpect(status().isNoContent());

        verify(rhumService).deleteRhum(id);
    }

    @Test
    void deleteRhum_shouldReturnNotFoundForNonExistingRhum() throws Exception {
        // Arrange
        Long id = 999L;
        doThrow(new EntityNotFoundException("Rhum not found")).when(rhumService).deleteRhum(id);

        // Act & Assert
        mockMvc.perform(delete("/api/rhums/{id}", id))
                .andExpect(status().isNotFound());

        verify(rhumService).deleteRhum(id);
    }

    @Test
    void getRhumsByOrigin_shouldReturnRhumsByOrigin() throws Exception {
        List<RhumDto> martinique = Arrays.asList(rhum1);
        when(rhumService.getRhumsByOrigin("Martinique")).thenReturn(martinique);

        mockMvc.perform(get("/api/rhums/origin/{origin}", "Martinique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].origin", is("Martinique")));

        verify(rhumService, times(1)).getRhumsByOrigin("Martinique");
    }

    @Test
    void searchRhumsByName_shouldReturnRhumsByNameContaining() throws Exception {
        List<RhumDto> diplomaticoRhums = Arrays.asList(rhum2);
        when(rhumService.searchRhumsByName("Diplo")).thenReturn(diplomaticoRhums);

        mockMvc.perform(get("/api/rhums/search").param("keyword", "Diplo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Diplomatico")));

        verify(rhumService, times(1)).searchRhumsByName("Diplo");
    }
}