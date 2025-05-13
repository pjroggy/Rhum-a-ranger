package projetPj.rhum_a_ranger.rhumTest;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import projetPj.rhum_a_ranger.rhum.Rhum;
import projetPj.rhum_a_ranger.rhum.RhumDto;
import projetPj.rhum_a_ranger.rhum.RhumMapper;
import projetPj.rhum_a_ranger.rhum.RhumRepository;
import projetPj.rhum_a_ranger.rhum.RhumService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        rhum1 = Rhum.builder()
                .id(1L)
                .name("Clement")
                .origin("Martinique")
                .description("Rhum agricole")
                .alcoholDegree("40")
                .age(2018)
                .build();

        rhum2 = Rhum.builder()
                .id(2L)
                .name("Diplomatico")
                .origin("Venezuela")
                .description("Rhum traditionnel")
                .alcoholDegree("43")
                .age(2010)
                .build();
    }

    @Test
    void getAllRhums_shouldReturnAllRhums() {
        when(rhumRepository.findAll()).thenReturn(Arrays.asList(rhum1, rhum2));

        List<RhumDto> result = rhumService.getAllRhums();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Clement");
        verify(rhumRepository, times(1)).findAll();
    }

    @Test
    void getRhumById_shouldReturnRhum_whenRhumExists() {
        when(rhumRepository.findById(1L)).thenReturn(Optional.of(rhum1));

        Optional<RhumDto> result = rhumService.getRhumById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(RhumMapper.toDto(rhum1));
        verify(rhumRepository, times(1)).findById(1L);
    }

    @Test
    void getRhumById_shouldReturnEmpty_whenRhumDoesNotExist() {
        when(rhumRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<RhumDto> result = rhumService.getRhumById(99L);

        assertThat(result).isEmpty();
        verify(rhumRepository, times(1)).findById(99L);
    }

    @Test
    void saveRhum_shouldSaveRhum() {
        when(rhumRepository.save(any(Rhum.class))).thenReturn(rhum1);

        RhumDto result = rhumService.saveRhum(RhumMapper.toDto(rhum1));

        assertThat(result).isEqualTo(RhumMapper.toDto(rhum1));
        verify(rhumRepository, times(1)).save(rhum1);
    }

    @Test
    void deleteRhum_shouldDeleteExistingRhum() {
        Long id = 1L;
        when(rhumRepository.existsById(id)).thenReturn(true);
        doNothing().when(rhumRepository).deleteById(id);

        rhumService.deleteRhum(id);

        verify(rhumRepository).existsById(id);
        verify(rhumRepository).deleteById(id);
    }

    @Test
    void deleteRhum_shouldThrowExceptionForNonExistingRhum() {
        Long id = 999L;
        when(rhumRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> rhumService.deleteRhum(id));
        verify(rhumRepository).existsById(id);
        verify(rhumRepository, never()).deleteById(id);
    }

    @Test
    void getRhumsByOrigin_shouldReturnRhumsByOrigin() {
        when(rhumRepository.findByOrigin("Martinique")).thenReturn(List.of(rhum1));

        List<RhumDto> result = rhumService.getRhumsByOrigin("Martinique");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).origin()).isEqualTo("Martinique");
        verify(rhumRepository, times(1)).findByOrigin("Martinique");
    }

    @Test
    void searchRhumsByName_shouldReturnRhumsByNameContaining() {
        when(rhumRepository.findByNameContainingIgnoreCase("Diplo")).thenReturn(List.of(rhum2));

        List<RhumDto> result = rhumService.searchRhumsByName("Diplo");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Diplomatico");
        verify(rhumRepository, times(1)).findByNameContainingIgnoreCase("Diplo");
    }

    @Test
    void updateRhum_shouldUpdateRhum_whenRhumExists() {
        RhumDto rhumToUpdate = new RhumDto(
                1L,
                "Clement VSOP",
                "Martinique",
                "Rhum agricole vieilli",
                null,
                2015,
                "42"
        );

        when(rhumRepository.findById(1L)).thenReturn(Optional.of(rhum1));
        when(rhumRepository.save(any(Rhum.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RhumDto result = rhumService.updateRhum(1L, rhumToUpdate);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Clement VSOP");
        assertThat(result.description()).isEqualTo("Rhum agricole vieilli");
        verify(rhumRepository, times(1)).findById(1L);
        verify(rhumRepository, times(1)).save(any(Rhum.class));
    }

    @Test
    void updateRhum_shouldThrowException_whenRhumDoesNotExist() {
        when(rhumRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rhumService.updateRhum(99L, RhumMapper.toDto(rhum1)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Rhum not found");

        verify(rhumRepository, times(1)).findById(99L);
        verify(rhumRepository, never()).save(any(Rhum.class));
    }
}