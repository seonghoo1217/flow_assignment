package assignment.flow.domain.entity;

import assignment.flow.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "block_extensions")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockExtension extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    private String extensionName;

    @Enumerated(EnumType.STRING)
    private ExtensionType extensionType;

    private boolean enabled;

    public BlockExtension(String extensionName, ExtensionType extensionType, boolean enabled) {
        this.extensionName = extensionName;
        this.extensionType = extensionType;
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }
}
