package projetPj.rhum_a_ranger.recette;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import projetPj.rhum_a_ranger.rhum.Rhum;

@Entity
@Table(name = "recette_rhum")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetteRhum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recette_id", nullable = false)
    private Recette recette;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rhum_id", nullable = false)
    private Rhum rhum;

    @Column(nullable = false)
    private Double volume;

    @Column(nullable = false, length = 10)
    private String unite;
}
