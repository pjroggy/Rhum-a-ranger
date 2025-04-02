package projetPj.rhum_a_ranger.rhum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RhumService {

    private final RhumRepository rhumRepository;

    public List<Rhum> getAllRhums() {
        return rhumRepository.findAll();
    }

    public Optional<Rhum> getRhumById(Long id) {
        return rhumRepository.findById(id);
    }

    public Rhum saveRhum(Rhum rhum) {
        return rhumRepository.save(rhum);
    }

    public void deleteRhum(Long id) {
        rhumRepository.deleteById(id);
    }

    public List<Rhum> getRhumsByOrigin(String origin) {
        return rhumRepository.findByOrigin(origin);
    }

    public List<Rhum> searchRhumsByName(String keyword) {
        return rhumRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Rhum updateRhum(Long id, Rhum rhumDetails) {
        return rhumRepository.findById(id)
                .map(existingRhum -> {
                    existingRhum.setName(rhumDetails.getName());
                    existingRhum.setOrigin(rhumDetails.getOrigin());
                    existingRhum.setDescription(rhumDetails.getDescription());
                    existingRhum.setPicture(rhumDetails.getPicture());
                    existingRhum.setYear(rhumDetails.getYear());
                    existingRhum.setAlcoholDegree(rhumDetails.getAlcoholDegree());
                    return rhumRepository.save(existingRhum);
                })
                .orElse(null); // Retourne null si le rhum n'est pas trouvé
    }
}
