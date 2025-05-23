package projetPj.rhum_a_ranger.ingredient;

public record IngredientDto(
        Long id,
        String name,
        String category,
        String description,
        String picture
) {}
