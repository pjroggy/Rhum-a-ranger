package projetPj.rhum_a_ranger.rhum;

public record RhumDto(
        Long id,
        String name,
        String origin,
        String description,
        String picture,
        Integer age,
        String alcoholDegree
) {}
