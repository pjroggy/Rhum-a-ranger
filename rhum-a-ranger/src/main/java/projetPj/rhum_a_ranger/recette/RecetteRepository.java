package projetPj.rhum_a_ranger.recette;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetteRepository extends JpaRepository<Recette, Long> {

    List<Recette> findByNameContainingIgnoreCase(String name);

    @Query("SELECT r FROM Recette r LEFT JOIN FETCH r.recetteRhum LEFT JOIN FETCH r.recetteIngredients WHERE r.id = :id")
    Optional<Recette> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT r FROM Recette r LEFT JOIN FETCH r.recetteRhum rr LEFT JOIN FETCH rr.rhum WHERE r.id = :id")
    Optional<Recette> findByIdWithRhum(@Param("id") Long id);
}