package com.foogaro.data.services;

import com.foogaro.data.models.Movie;
import com.foogaro.data.repositories.MovieRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames={"movies"})
public class MovieService {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    @Cacheable
    public Optional<Movie> findById(Long id) {
        return repository.findById(id);
    }

    @Cacheable
    public List<Movie> findAll() {
        return repository.findAll();
    }

    @CachePut(value = "movies", key = "#result.id")
    public Movie save(Movie movie) { return repository.save(movie); }

    @CacheEvict
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @CacheEvict
    public void deleteAll() {
        repository.deleteAll();
    }
}
