package projetPj.rhum_a_ranger.rhum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rhums")
@RequiredArgsConstructor
public class RhumController {

    private final RhumService rhumService;

    @GetMapping
    public List<RhumDto> getAllRhums() {
        return rhumService.getAllRhums();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RhumDto> getRhumById(@PathVariable Long id) {
        return rhumService.getRhumById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RhumDto> createRhum(@RequestBody RhumDto rhumDto) {
        RhumDto saved = rhumService.saveRhum(rhumDto);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RhumDto> updateRhum(@PathVariable Long id, @RequestBody RhumDto rhumDto) {
        try {
            RhumDto updated = rhumService.updateRhum(id, rhumDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRhum(@PathVariable Long id) {
        rhumService.deleteRhum(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/origin/{origin}")
    public List<RhumDto> getRhumsByOrigin(@PathVariable String origin) {
        return rhumService.getRhumsByOrigin(origin);
    }

    @GetMapping("/search")
    public List<RhumDto> searchRhumsByName(@RequestParam String keyword) {
        return rhumService.searchRhumsByName(keyword);
    }
}