package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {
    //acá se agregan todos los métodos necesarios, por ejemplo para traer información de las tablas
    Game findById(long id);
}