package projetPj.rhum_a_ranger.recette;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recettes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String advise;

    @Column(nullable = false)
    private Integer macerationTime;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RecetteDifficulty difficulty = RecetteDifficulty.FACILE;

    @OneToOne(mappedBy = "recette", cascade = CascadeType.ALL, orphanRemoval = true)
    private RecetteRhum recetteRhum;

    @OneToMany(mappedBy = "recette", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecetteIngredient> recetteIngredients = new ArrayList<>();

    public enum RecetteDifficulty {
        FACILE, MOYEN, DIFFICILE
    }
}