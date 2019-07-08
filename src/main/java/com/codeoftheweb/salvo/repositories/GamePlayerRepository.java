package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    //acá se agregan todos los métodos necesarios, por ejemplo para traer información de las tablas
    GamePlayer findById(long id);
}