package com.foogaro.data.services;

import com.foogaro.data.models.Actor;
import com.foogaro.data.repositories.ActorRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames={"actors"})
public class ActorService {

    private final ActorRepository repository;

    public ActorService(ActorRepository repository) {
        this.repository = repository;
    }

    @Cacheable
    public Optional<Actor> findById(Long id) {
        return repository.findById(id);
    }

    @Cacheable
    public List<Actor> findAll() {
        return repository.findAll();
    }

    @CachePut(cacheNames = "actors", key = "#result.id")
    public Actor save(Actor actor) {
        return repository.save(actor);
    }

    @CacheEvict
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @CacheEvict
    public void deleteAll() {
        repository.deleteAll();
    }
}
