package projetPj.rhum_a_ranger.recette;

import org.springframework.stereotype.Component;
import projetPj.rhum_a_ranger.ingredient.IngredientDto;
import projetPj.rhum_a_ranger.rhum.RhumDto;

import java.util.List;

@Component
public class RecetteMapper {

    public RecetteDto toDto(Recette recette) {
        if (recette == null) return null;

        return new RecetteDto(
                recette.getId(),
                recette.getName(),
                recette.getDescription(),
                recette.getAdvise(),
                recette.getMacerationTime(),
                recette.getImageUrl(),
                recette.getDifficulty() != null ? recette.getDifficulty().name() : null,
                mapRhumQuantity(recette.getRecetteRhum()),
                mapIngredients(recette.getRecetteIngredients())
        );
    }

    public Recette toEntity(RecetteDto dto) {
        if (dto == null) return null;

        return Recette.builder()
                .id(dto.id())
                .name(dto.name())
                .description(dto.description())
                .advise(dto.advise())
                .macerationTime(dto.macerationTime())
                .imageUrl(dto.imageUrl())
                .difficulty(dto.difficulty() != null ? Recette.RecetteDifficulty.valueOf(dto.difficulty()) : null)
                .build();
    }

    private RecetteDto.RhumQuantityDto mapRhumQuantity(RecetteRhum recetteRhum) {
        if (recetteRhum == null) return null;

        return new RecetteDto.RhumQuantityDto(
                mapRhumDto(recetteRhum.getRhum()),
                recetteRhum.getVolume(),
                recetteRhum.getUnite()
        );
    }

    private List<RecetteDto.IngredientQuantityDto> mapIngredients(List<RecetteIngredient> recetteIngredients) {
        if (recetteIngredients == null) return List.of();

        return recetteIngredients.stream()
                .map(ri -> new RecetteDto.IngredientQuantityDto(
                        mapIngredientDto(ri.getIngredient()),
                        ri.getQuantity(),
                        ri.getUnite()
                ))
                .toList();
    }

    private RhumDto mapRhumDto(projetPj.rhum_a_ranger.rhum.Rhum rhum) {
        if (rhum == null) return null;

        return new RhumDto(
                rhum.getId(),
                rhum.getName(),
                rhum.getOrigin(),
                rhum.getDescription(),
                rhum.getPicture(),
                rhum.getAge(),
                rhum.getAlcoholDegree()
        );
    }

    private IngredientDto mapIngredientDto(projetPj.rhum_a_ranger.ingredient.Ingredient ingredient) {
        if (ingredient == null) return null;

        return new IngredientDto(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getCategory(),
                ingredient.getDescription(),
                ingredient.getPicture()
        );
    }
}