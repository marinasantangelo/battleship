package com.codeoftheweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
public class Score {

    //----------------ATRIBUTOS-------------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    private double score;

    private LocalDateTime finishDate;


    //---------------CONSTRUCTORES-------------------
    public Score() {
    }

    public Score(Player player, Game game, double score, LocalDateTime finishDate) {
        this.player = player;
        this.game = game;
        this.score = score;
        this.finishDate = finishDate;
    }


    //-------------GETTERS Y SETTERS---------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player playerId) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game gameId) {
        this.game = game;
    }

    public double getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }




}
