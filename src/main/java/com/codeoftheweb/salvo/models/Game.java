package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class Game {

    //------ATRIBUTOS----

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;


    @OneToMany(mappedBy ="game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="game", fetch= FetchType.EAGER) //when fetching a game JPA should automatically fetch the scores
    private Set<Score> scores = new HashSet<>();

    private LocalDateTime creationDate;


    //---------CONSTRUCTORES--------

    public Game() {}

    public Game(LocalDateTime creationDate){
        this.creationDate = creationDate;
    }

    //-----GETTERS Y SETTERS---------


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    //---------DTOS---------------

    public Map<String, Object> gameDTO(){
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers", this.gamePlayers.stream().map(gamePlayer -> gamePlayer.gamePlayerDTO()));
        return dto;
    }
    //------MÉTODOS DE CLASE----------

}
