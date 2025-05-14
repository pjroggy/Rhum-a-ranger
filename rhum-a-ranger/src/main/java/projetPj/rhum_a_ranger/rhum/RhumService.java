package projetPj.rhum_a_ranger.rhum;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RhumService {

    private final RhumRepository rhumRepository;

    public List<RhumDto> getAllRhums() {
        return rhumRepository.findAll()
                .stream()
                .map(RhumMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<RhumDto> getRhumById(Long id) {
        return rhumRepository.findById(id)
                .map(RhumMapper::toDto);
    }

    public RhumDto saveRhum(RhumDto rhumDto) {
        Rhum rhum = RhumMapper.toEntity(rhumDto);
        Rhum saved = rhumRepository.save(rhum);
        return RhumMapper.toDto(saved);
    }

    public void deleteRhum(Long id) {
        if (!rhumRepository.existsById(id)) {
            throw new EntityNotFoundException("Rhum not found with id: " + id);
        }
        rhumRepository.deleteById(id);
    }

    public List<RhumDto> getRhumsByOrigin(String origin) {
        return rhumRepository.findByOrigin(origin)
                .stream()
                .map(RhumMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RhumDto> searchRhumsByName(String keyword) {
        return rhumRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(RhumMapper::toDto)
                .collect(Collectors.toList());
    }

    public RhumDto updateRhum(Long id, RhumDto rhumDto) {
        return rhumRepository.findById(id)
                .map(existing -> {
                    existing.setName(rhumDto.name());
                    existing.setOrigin(rhumDto.origin());
                    existing.setDescription(rhumDto.description());
                    existing.setPicture(rhumDto.picture());
                    existing.setAge(rhumDto.age());
                    existing.setAlcoholDegree(rhumDto.alcoholDegree());
                    return RhumMapper.toDto(rhumRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Rhum not found"));
    }
}
