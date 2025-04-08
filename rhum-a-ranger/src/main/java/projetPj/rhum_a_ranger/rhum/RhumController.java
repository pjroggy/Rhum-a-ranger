package projetPj.rhum_a_ranger.rhum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rhums")
@RequiredArgsConstructor
public class RhumController {

    private final RhumService rhumService;

    @GetMapping
    public List<Rhum> getAllRhums() {
        return rhumService.getAllRhums();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rhum> getRhumById(@PathVariable Long id) {
        return rhumService.getRhumById(id)
                .map(rhum -> ResponseEntity.ok().body(rhum))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rhum createRhum(@RequestBody Rhum rhum) {
        return rhumService.saveRhum(rhum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rhum> updateRhum(@PathVariable Long id, @RequestBody Rhum rhum) {
        Rhum updatedRhum = rhumService.updateRhum(id, rhum);
        return updatedRhum != null ?
                ResponseEntity.ok(updatedRhum) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRhum(@PathVariable Long id) {
        boolean deleted = rhumService.deleteRhum(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/origin/{origin}")
    public List<Rhum> getRhumsByOrigin(@PathVariable String origin) {
        return rhumService.getRhumsByOrigin(origin);
    }

    @GetMapping("/search")
    public List<Rhum> searchRhumsByName(@RequestParam String keyword) {
        return rhumService.searchRhumsByName(keyword);
    }
}