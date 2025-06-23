package projetPj.rhum_a_ranger.recette;

import projetPj.rhum_a_ranger.ingredient.IngredientDto;
import projetPj.rhum_a_ranger.rhum.RhumDto;

import java.util.List;

public record RecetteDto(
        Long id,
        String name,
        String description,
        String advise,
        Integer macerationTime,
        String imageUrl,
        String difficulty,
        RhumQuantityDto rhum,
        List<IngredientQuantityDto> ingredients
) {

    public record RhumQuantityDto(
            RhumDto rhum,
            Double volume,
            String unite
    ) {}

    public record IngredientQuantityDto(
            IngredientDto ingredient,
            Double quantity,
            String unite
    ) {}
}