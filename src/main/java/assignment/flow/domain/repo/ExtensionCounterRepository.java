package assignment.flow.domain.repo;

import assignment.flow.domain.entity.ExtensionCounter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface ExtensionCounterRepository extends Repository<ExtensionCounter, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ExtensionCounter c WHERE c.counterId = :id")
    ExtensionCounter findByCounterIdForUpdate(@Param("id") String id);

    ExtensionCounter save(ExtensionCounter extensionCounter);
}
