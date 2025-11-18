package cs.youtrade.autotrade.client.keygen.service;

import cs.youtrade.autotrade.client.keygen.entities.InnerKeyEntity;
import cs.youtrade.autotrade.client.keygen.repositories.InnerKeyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InnerKeyManagerService {
    public static final String YOUTRADE_KEY = "youtradeKey";

    private final InnerKeyRepository innerKeyRepository;

    public InnerKeyManagerService(InnerKeyRepository innerKeyRepository) {
        this.innerKeyRepository = innerKeyRepository;
    }

    public Optional<InnerKeyEntity> findByKeyName(String keyName) {
        return innerKeyRepository.findByKeyName(keyName);
    }

    public boolean isValidKey(String name, String value) {
        Optional<InnerKeyEntity> keyOpt = findByKeyName(name);
        return keyOpt
                .map(key ->
                        key.isValidKey(value))
                .orElse(false);

    }

    public List<InnerKeyEntity> findAll() {
        return innerKeyRepository.findAll();
    }
}
