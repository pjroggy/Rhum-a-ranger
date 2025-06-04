package projetPj.rhum_a_ranger.recette;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recettes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecetteController {

    private final RecetteService recetteService;

    @GetMapping
    public ResponseEntity<List<RecetteDto>> getAllRecettes() {
        List<RecetteDto> recettes = recetteService.findAll();
        return ResponseEntity.ok(recettes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetteDto> getRecetteById(@PathVariable Long id) {
        return recetteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecetteDto>> searchRecettes(@RequestParam String name) {
        List<RecetteDto> recettes = recetteService.findByName(name);
        return ResponseEntity.ok(recettes);
    }

    @PostMapping
    public ResponseEntity<RecetteDto> createRecette(@RequestBody RecetteDto recetteDto) {
        try {
            RecetteDto savedRecette = recetteService.save(recetteDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecette);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecetteDto> updateRecette(@PathVariable Long id, @RequestBody RecetteDto recetteDto) {
        try {
            return recetteService.update(id, recetteDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecette(@PathVariable Long id) {
        if (recetteService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}