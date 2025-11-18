package cs.youtrade.autotrade.client.keygen.repositories;

import cs.youtrade.autotrade.client.keygen.entities.InnerKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InnerKeyRepository extends JpaRepository<InnerKeyEntity, Long> {
    Optional<InnerKeyEntity> findByKeyName(String keyName);
}
