package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    //----------------ATRIBUTOS-------------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    private List<String> locations;

    private String shipType;



    //---------------CONSTRUCTORES-------------------

    public Ship() {
    }

    public Ship(List<String> locations, String shipType) {
        this.gamePlayer = gamePlayer;
        this.locations = locations;
        this.shipType = shipType;
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

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }


    //------------------DTOS---------------------

    public Map<String, Object> shipsDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("location", this.getLocations());
        dto.put("type",this.getShipType());
        return dto;
    }
}
