package projetPj.rhum_a_ranger.rhumTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import projetPj.rhum_a_ranger.rhum.Rhum;
import projetPj.rhum_a_ranger.rhum.RhumRepository;
import projetPj.rhum_a_ranger.rhum.RhumService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RhumServiceTest {

    @Mock
    private RhumRepository rhumRepository;

    @InjectMocks
    private RhumService rhumService;

    private Rhum rhum1;
    private Rhum rhum2;

    @BeforeEach
    void setUp() {
        rhum1 = new Rhum();
        rhum1.setId(1L);
        rhum1.setName("Clement");
        rhum1.setOrigin("Martinique");
        rhum1.setDescription("Rhum agricole");
        rhum1.setAlcoholDegree("40");
        rhum1.setAge(2018);

        rhum2 = new Rhum();
        rhum2.setId(2L);
        rhum2.setName("Diplomatico");
        rhum2.setOrigin("Venezuela");
        rhum2.setDescription("Rhum traditionnel");
        rhum2.setAlcoholDegree("43");
        rhum2.setAge(2010);
    }

    @Test
    void getAllRhums_shouldReturnAllRhums() {
        // Given
        List<Rhum> rhums = Arrays.asList(rhum1, rhum2);
        when(rhumRepository.findAll()).thenReturn(rhums);

        // When
        List<Rhum> result = rhumService.getAllRhums();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(rhum1, rhum2);
        verify(rhumRepository, times(1)).findAll();
    }

    @Test
    void getRhumById_shouldReturnRhum_whenRhumExists() {
        // Given
        when(rhumRepository.findById(1L)).thenReturn(Optional.of(rhum1));

        // When
        Optional<Rhum> result = rhumService.getRhumById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(rhum1);
        verify(rhumRepository, times(1)).findById(1L);
    }

    @Test
    void getRhumById_shouldReturnEmpty_whenRhumDoesNotExist() {
        // Given
        when(rhumRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Rhum> result = rhumService.getRhumById(99L);

        // Then
        assertThat(result).isEmpty();
        verify(rhumRepository, times(1)).findById(99L);
    }

    @Test
    void saveRhum_shouldSaveRhum() {
        // Given
        when(rhumRepository.save(any(Rhum.class))).thenReturn(rhum1);

        // When
        Rhum result = rhumService.saveRhum(rhum1);

        // Then
        assertThat(result).isEqualTo(rhum1);
        verify(rhumRepository, times(1)).save(rhum1);
    }

    @Test
    void deleteRhum_shouldDeleteRhum() {
        // Given
        doNothing().when(rhumRepository).deleteById(anyLong());

        // When
        rhumService.deleteRhum(1L);

        // Then
        verify(rhumRepository, times(1)).deleteById(1L);
    }

    @Test
    void getRhumsByOrigin_shouldReturnRhumsByOrigin() {
        // Given
        List<Rhum> martinique = Arrays.asList(rhum1);
        when(rhumRepository.findByOrigin("Martinique")).thenReturn(martinique);

        // When
        List<Rhum> result = rhumService.getRhumsByOrigin("Martinique");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrigin()).isEqualTo("Martinique");
        verify(rhumRepository, times(1)).findByOrigin("Martinique");
    }

    @Test
    void searchRhumsByName_shouldReturnRhumsByNameContaining() {
        // Given
        List<Rhum> diplomaticoRhums = Arrays.asList(rhum2);
        when(rhumRepository.findByNameContainingIgnoreCase("Diplo")).thenReturn(diplomaticoRhums);

        // When
        List<Rhum> result = rhumService.searchRhumsByName("Diplo");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Diplomatico");
        verify(rhumRepository, times(1)).findByNameContainingIgnoreCase("Diplo");
    }

    @Test
    void updateRhum_shouldUpdateRhum_whenRhumExists() {
        // Given
        Rhum rhumToUpdate = new Rhum();
        rhumToUpdate.setName("Clement VSOP");
        rhumToUpdate.setOrigin("Martinique");
        rhumToUpdate.setDescription("Rhum agricole vieilli");
        rhumToUpdate.setAlcoholDegree("42");
        rhumToUpdate.setAge(2015);

        when(rhumRepository.findById(1L)).thenReturn(Optional.of(rhum1));
        when(rhumRepository.save(any(Rhum.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rhum result = rhumService.updateRhum(1L, rhumToUpdate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Clement VSOP");
        assertThat(result.getDescription()).isEqualTo("Rhum agricole vieilli");
        verify(rhumRepository, times(1)).findById(1L);
        verify(rhumRepository, times(1)).save(any(Rhum.class));
    }

    @Test
    void updateRhum_shouldReturnNull_whenRhumDoesNotExist() {
        // Given
        when(rhumRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Rhum result = rhumService.updateRhum(99L, rhum1);

        // Then
        assertThat(result).isNull();
        verify(rhumRepository, times(1)).findById(99L);
        verify(rhumRepository, never()).save(any(Rhum.class));
    }
}