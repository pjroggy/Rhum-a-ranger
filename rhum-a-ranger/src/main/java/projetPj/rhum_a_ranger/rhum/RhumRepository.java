package projetPj.rhum_a_ranger.rhum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RhumRepository extends JpaRepository<Rhum, Long> {
    List<Rhum> findByOrigin(String origin);
    List<Rhum> findByNameContainingIgnoreCase(String keyword);
}
