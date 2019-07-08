package com.codeoftheweb.salvo.repositories;


import com.codeoftheweb.salvo.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
   //acá se agregan todos los métodos necesarios, por ejemplo para traer información de las tablas
   Player findByUsername(String username);
}