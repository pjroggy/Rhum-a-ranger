package projetPj.rhum_a_ranger.rhum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rhums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rhum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String origin;

    @Column(length = 1000)
    private String description;

    private String picture;

    private Integer age;

    private String alcoholDegree;
}