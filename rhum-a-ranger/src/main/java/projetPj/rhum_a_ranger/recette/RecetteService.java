package projetPj.rhum_a_ranger.recette;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projetPj.rhum_a_ranger.ingredient.Ingredient;
import projetPj.rhum_a_ranger.ingredient.IngredientRepository;
import projetPj.rhum_a_ranger.rhum.Rhum;
import projetPj.rhum_a_ranger.rhum.RhumRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecetteService {

    private final RecetteRepository recetteRepository;
    private final RhumRepository rhumRepository;
    private final IngredientRepository ingredientRepository;
    private final RecetteMapper recetteMapper;

    public List<RecetteDto> findAll() {
        return recetteRepository.findAll()
                .stream()
                .map(recetteMapper::toDto)
                .toList();
    }

    public Optional<RecetteDto> findById(Long id) {
        return recetteRepository.findById(id)
                .map(recetteMapper::toDto);
    }

    public List<RecetteDto> findByName(String name) {
        return recetteRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(recetteMapper::toDto)
                .toList();
    }

    public List<RecetteDto> findByIngredientName(String ingredientName) {
        return recetteRepository.findByIngredientNameContainingIgnoreCase(ingredientName)
                .stream()
                .map(recetteMapper::toDto)
                .toList();
    }

    public List<RecetteDto> findByRhumName(String rhumName) {
        return recetteRepository.findByRhumNameContainingIgnoreCase(rhumName)
                .stream()
                .map(recetteMapper::toDto)
                .toList();
    }

    public List<RecetteDto> findByIngredientId(Long ingredientId) {
        return recetteRepository.findByIngredientId(ingredientId)
                .stream()
                .map(recetteMapper::toDto)
                .toList();
    }

    public List<RecetteDto> findByRhumId(Long rhumId) {
        return recetteRepository.findByRhumId(rhumId)
                .stream()
                .map(recetteMapper::toDto)
                .toList();
    }

    @Transactional
    public RecetteDto save(RecetteDto recetteDto) {
        Recette recette = recetteMapper.toEntity(recetteDto);

        if (recetteDto.rhum() != null) {
            Rhum rhum = rhumRepository.findById(recetteDto.rhum().rhum().id())
                    .orElseThrow(() -> new RuntimeException("Rhum non trouvé"));

            RecetteRhum recetteRhum = RecetteRhum.builder()
                    .recette(recette)
                    .rhum(rhum)
                    .volume(recetteDto.rhum().volume())
                    .unite(recetteDto.rhum().unite())
                    .build();
            recette.setRecetteRhum(recetteRhum);
        }

        if (recetteDto.ingredients() != null) {
            for (var ingredientDto : recetteDto.ingredients()) {
                Ingredient ingredient = ingredientRepository.findById(ingredientDto.ingredient().id())
                        .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));

                RecetteIngredient recetteIngredient = RecetteIngredient.builder()
                        .recette(recette)
                        .ingredient(ingredient)
                        .quantity(ingredientDto.quantity())
                        .unite(ingredientDto.unite())
                        .build();
                recette.getRecetteIngredients().add(recetteIngredient);
            }
        }

        Recette saved = recetteRepository.save(recette);
        return recetteMapper.toDto(saved);
    }

    @Transactional
    public Optional<RecetteDto> update(Long id, RecetteDto recetteDto) {
        return recetteRepository.findById(id)
                .map(existingRecette -> {
                    existingRecette.setName(recetteDto.name());
                    existingRecette.setDescription(recetteDto.description());
                    existingRecette.setAdvise(recetteDto.advise());
                    existingRecette.setMacerationTime(recetteDto.macerationTime());
                    existingRecette.setImageUrl(recetteDto.imageUrl());
                    existingRecette.setDifficulty(
                            recetteDto.difficulty() != null ?
                                    Recette.RecetteDifficulty.valueOf(recetteDto.difficulty()) : null
                    );

                    if (recetteDto.rhum() != null) {
                        Rhum rhum = rhumRepository.findById(recetteDto.rhum().rhum().id())
                                .orElseThrow(() -> new RuntimeException("Rhum non trouvé"));

                        if (existingRecette.getRecetteRhum() == null) {
                            existingRecette.setRecetteRhum(RecetteRhum.builder()
                                    .recette(existingRecette)
                                    .rhum(rhum)
                                    .volume(recetteDto.rhum().volume())
                                    .unite(recetteDto.rhum().unite())
                                    .build());
                        } else {
                            existingRecette.getRecetteRhum().setRhum(rhum);
                            existingRecette.getRecetteRhum().setVolume(recetteDto.rhum().volume());
                            existingRecette.getRecetteRhum().setUnite(recetteDto.rhum().unite());
                        }
                    }

                    existingRecette.getRecetteIngredients().clear();
                    if (recetteDto.ingredients() != null) {
                        for (var ingredientDto : recetteDto.ingredients()) {
                            Ingredient ingredient = ingredientRepository.findById(ingredientDto.ingredient().id())
                                    .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));

                            RecetteIngredient recetteIngredient = RecetteIngredient.builder()
                                    .recette(existingRecette)
                                    .ingredient(ingredient)
                                    .quantity(ingredientDto.quantity())
                                    .unite(ingredientDto.unite())
                                    .build();
                            existingRecette.getRecetteIngredients().add(recetteIngredient);
                        }
                    }

                    return recetteMapper.toDto(recetteRepository.save(existingRecette));
                });
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (recetteRepository.existsById(id)) {
            recetteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}