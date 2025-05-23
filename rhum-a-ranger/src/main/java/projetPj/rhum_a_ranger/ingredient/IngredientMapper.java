package projetPj.rhum_a_ranger.ingredient;

public class IngredientMapper {

    public static IngredientDto toDto(Ingredient rhum) {
        return new IngredientDto(
                rhum.getId(),
                rhum.getName(),
                rhum.getCategory(),
                rhum.getDescription(),
                rhum.getPicture()
        );
    }

    public static Ingredient toEntity(IngredientDto dto) {
        return Ingredient.builder()
                .id(dto.id())
                .name(dto.name())
                .category(dto.category())
                .description(dto.description())
                .picture(dto.picture())
                .build();
    }
}
