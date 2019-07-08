package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class GamePlayer {

    //-------ATRIBUTOS--------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.EAGER)//hace que cuando se traiga la información del GamePlayer también traiga la información del Player
    @JoinColumn(name="player_id")//crea una columna player_id en la tabla GamePlayer para relacionarlos
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch= FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch= FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();

    //-----------CONSTRUCTORES-------------

    public GamePlayer() {}

    public GamePlayer(LocalDateTime creationDate, Game game, Player player){
        this.creationDate = creationDate;
        this.game = game;
        this.player = player;
    }

    //-----------GETTERS Y SETTERS--------


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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    //----------------DTO----------------
    public Map<String, Object> gamePlayerDTO(){
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().playerDTO());
        if( this.getPlayerScore() != null)
            dto.put("score", this.getPlayerScore().getScore());
        else
            dto.put("score", null);
        return dto;
    }

    public Map<String, Object> gameViewDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getGame().getId());
        dto.put("created", this.getGame().getCreationDate());
        dto.put("gamePlayers", this.getGame().getGamePlayers().stream().map(gp -> gp.gamePlayerDTO()));
        dto.put("ships", this.getShips().stream().map(ship -> ship.shipsDTO()));
        dto.put("salvoes", this.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(salvo -> salvo.salvoesDTO())));
        return dto;
    }


    //-----------MÉTODOS DE CLASE-----------

    //Adding a ship
    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    //Adding a salvo
    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    //getting the score of a game from a player
    public Score getPlayerScore(){
        return player.getScore(game);
    }

}
