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
    Optional<Recette> findById(@Param("id") Long id);

    @Query("SELECT DISTINCT r FROM Recette r JOIN r.recetteIngredients ri JOIN ri.ingredient i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredientName, '%'))")
    List<Recette> findByIngredientNameContainingIgnoreCase(@Param("ingredientName") String ingredientName);

    @Query("SELECT DISTINCT r FROM Recette r JOIN r.recetteRhum rr JOIN rr.rhum rhum WHERE LOWER(rhum.name) LIKE LOWER(CONCAT('%', :rhumName, '%'))")
    List<Recette> findByRhumNameContainingIgnoreCase(@Param("rhumName") String rhumName);

    @Query("SELECT DISTINCT r FROM Recette r JOIN r.recetteIngredients ri WHERE ri.ingredient.id = :ingredientId")
    List<Recette> findByIngredientId(@Param("ingredientId") Long ingredientId);

    @Query("SELECT DISTINCT r FROM Recette r JOIN r.recetteRhum rr WHERE rr.rhum.id = :rhumId")
    List<Recette> findByRhumId(@Param("rhumId") Long rhumId);
}