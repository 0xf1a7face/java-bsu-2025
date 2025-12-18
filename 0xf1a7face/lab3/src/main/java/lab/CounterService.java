package lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CounterService {

    @Autowired
    private CounterRepository counterRepository;

    @Transactional
    public synchronized long addToCounter(long increment) {
        Counter counter = getOrCreateCounter();
        counter.setValue(counter.getValue() + increment);
        counterRepository.save(counter);
        return counter.getValue();
    }

    @Transactional
    public synchronized long resetCounter() {
        Counter counter = getOrCreateCounter();
        counter.setValue(0);
        counterRepository.save(counter);
        return counter.getValue();
    }

    @Transactional(readOnly = true)
    public long getCounterValue() {
        Counter counter = counterRepository.findById(1L).orElse(null);
        return counter != null ? counter.getValue() : 0;
    }

    private Counter getOrCreateCounter() {
        return counterRepository.findById(1L)
                .orElseGet(() -> {
                    Counter newCounter = new Counter(0);
                    newCounter.setId(1L);
                    return counterRepository.save(newCounter);
                });
    }
}
