package projetPj.rhum_a_ranger.ingredient;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll()
                .stream()
                .map(IngredientMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<IngredientDto> getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .map(IngredientMapper::toDto);
    }

    public IngredientDto saveIngredient(IngredientDto ingredientDto) {
        Ingredient ingredient = IngredientMapper.toEntity(ingredientDto);
        Ingredient saved = ingredientRepository.save(ingredient);
        return IngredientMapper.toDto(saved);
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new EntityNotFoundException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }

    public List<IngredientDto> getIngredientsByCategory(String category) {
        return ingredientRepository.findByCategory(category)
                .stream()
                .map(IngredientMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<IngredientDto> searchIngredientsByName(String keyword) {
        return ingredientRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(IngredientMapper::toDto)
                .collect(Collectors.toList());
    }

    public IngredientDto updateIngredient(Long id, IngredientDto ingredientDto) {
        return ingredientRepository.findById(id)
                .map(existing -> {
                    existing.setName(ingredientDto.name());
                    existing.setCategory(ingredientDto.category());
                    existing.setDescription(ingredientDto.description());
                    existing.setPicture(ingredientDto.picture());
                    return IngredientMapper.toDto(ingredientRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
    }
}
