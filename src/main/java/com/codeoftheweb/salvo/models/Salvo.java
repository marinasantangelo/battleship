package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    //----------------ATRIBUTOS-------------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id") //tells Spring to create a gamePlayer_id column in the Salvo class column
    private GamePlayer gamePlayer;

    @ElementCollection
    private List<String> locations;

    private int turn;

    //---------------CONSTRUCTORES-------------------

    public Salvo() {
    }

    public Salvo(List<String> locations, int turn) {
        this.locations = locations;
        this.turn = turn;
    }

    //-------------GETTERS Y SETTERS---------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }


    //------------------DTOS---------------------

    public Map<String, Object> salvoesDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn",this.getTurn());
        dto.put("locations", this.getLocations());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        return dto;
    }


}
