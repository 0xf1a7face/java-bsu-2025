package lab;

import jakarta.persistence.*;

@Entity
@Table(name = "counter")
public class Counter {
    @Id
    @Column(name = "id", nullable = false)
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long value;

    public Counter() {}

    public Counter(long value) {
        this.value = value;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getValue() { return value; }
    public void setValue(long value) { this.value = value; }
}
