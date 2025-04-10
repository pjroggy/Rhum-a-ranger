package projetPj.rhum_a_ranger.rhum;

public class RhumMapper {

    public static RhumDto toDto(Rhum rhum) {
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

    public static Rhum toEntity(RhumDto dto) {
        return Rhum.builder()
                .id(dto.id())
                .name(dto.name())
                .origin(dto.origin())
                .description(dto.description())
                .picture(dto.picture())
                .age(dto.age())
                .alcoholDegree(dto.alcoholDegree())
                .build();
    }
}