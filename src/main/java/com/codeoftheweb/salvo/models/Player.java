package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*Player es una de las "piezas" que van a conformar la batalla naval (junto con juegos, tiros, naves, etc).
Cada una tiene que tener una tabla con un id único y las relaciones con los elementos de las otras tablas (i.e. un tiro en un juego hecho por un jugador, etc)
 */

@Entity // esto crea una tabla para la clase Player en la base de datos de Spring
public class Player {

    //----------------ATRIBUTOS-------------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String username;

    @OneToMany(mappedBy ="player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>(); //A HashSet stores items in a table in a way that makes testing for membership very fast. However, a HashSet does not keep track of the order in which items are added.  If this order is important, used the slightly more expensive LinkedHashSet

    @OneToMany(mappedBy="player", fetch= FetchType.EAGER) //when fetching a Player JPA should automatically fetch the scores
    private Set<Score> scores = new HashSet<>();

    private String password;

    //---------------CONSTRUCTORES-------------------

    public Player() {} // siempre tiene que estar el constructor vacío (Spring)

    public Player(String username, String password) { // Player tiene un id y un user name.  Al armar el constructor de esta forma, una nueva instancia de Player se crea con el user name (el id se genera automáticamente por Srping)
        this.username = username;
        this.password = password;
    }

    //-------------GETTERS Y SETTERS---------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    //------------------DTOS---------------------

    public Map<String, Object> playerDTO(){
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", this.getId());
        dto.put("user", this.getUsername());

        return dto;
    }

    //-------------MÉTODOS DE CLASE-------------

    //Adds a GamePlayer
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    //Gets the Score from an specific game
    public Score getScore(Game game){
       return this.getScores().stream()
                //.peek(score -> System.out.println("processing: " + score))
                .filter(score -> score.getGame().getId() == game.getId())
                .findFirst()
                .orElse(null);
    //return gameScore;

    }

}