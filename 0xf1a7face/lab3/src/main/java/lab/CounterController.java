package lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CounterController {

    @Autowired
    private CounterService counterService;

    @PostMapping("/add")
    public ResponseEntity<Long> addToCounter(@RequestBody long value) {
        return ResponseEntity.ok(counterService.addToCounter(value));
    }

    @PostMapping("/sub")
    public ResponseEntity<Long> subFromCounter(@RequestBody long value) {
        return ResponseEntity.ok(counterService.addToCounter(-value));
    }

    @PostMapping("/reset")
    public ResponseEntity<Long> resetCounter() {
        return ResponseEntity.ok(counterService.resetCounter());
    }

    @GetMapping("/get")
    public ResponseEntity<Long> getCounterValue() {
        return ResponseEntity.ok(counterService.getCounterValue());
    }
}
