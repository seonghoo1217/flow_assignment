package assignment.flow.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "extension_counter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtensionCounter {
    @Id
    @Column(name = "counter_id", length = 20)
    private String counterId;

    @Column(name = "count_value", nullable = false)
    private long countValue;

    @Version
    @Column(name = "version", nullable = false)
    private long version;


    public void incrementCount() {
        this.countValue++;
    }

    public void decrementCount() {
        if (this.countValue > 0) {
            this.countValue--;
        }
    }

    public ExtensionCounter(String counterId, long countValue, long version) {
        this.counterId = counterId;
        this.countValue = countValue;
        this.version = version;
    }
}
