package projetPj.rhum_a_ranger.recette;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import projetPj.rhum_a_ranger.ingredient.Ingredient;

@Entity
@Table(name = "recette_ingredient")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetteIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recette_id", nullable = false)
    private Recette recette;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false, length = 20)
    private String unite;
}