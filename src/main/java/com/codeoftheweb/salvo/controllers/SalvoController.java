package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.models.Score;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.SalvoRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")//esto le agrega /api a todas las urls que este controller use para que no se superpongan con los rest. En rest el JSON trae toda la información de una clase, mientras que en api puedo controlar lo que contiene el JSON
public class SalvoController {

@Autowired // permite que se cree automáticamente una instancia de Game y la guarde en el repositorio para poder ser usados por Spring
private GameRepository gameRepository;

@Autowired // permite que se cree automáticamente una instancia de GamePlayer y la guarde en el repositorio para poder ser usados por Spring
private GamePlayerRepository gamePlayerRepository;

@Autowired // permite que se cree automáticamente una instancia de Player y la guarde en el repositorio para poder ser usados por Spring
private PlayerRepository playerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(path = "/players", method = RequestMethod.POST) //Como la función register crea un nuevo usuario, se aclara que el método de request es Post (sino, por default es get)
    public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password) {
        ResponseEntity<Object> responseEntity;

        if (username.isEmpty() || password.isEmpty()) {
            responseEntity = new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }else if (playerRepository.findByUsername(username) != null) {
            responseEntity = new ResponseEntity<>("Username already in use", HttpStatus.FORBIDDEN);
        }else {
            playerRepository.save(new Player(username, passwordEncoder.encode(password)));
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        }
        return responseEntity;
    }


    @RequestMapping("/games")
    public Map<String,Object> getGames(Authentication authentication) {
        Map<String,Object> map = new HashMap<>();
        if(isGuest(authentication)){
            map.put("player", "GUEST");
        }
        else{
            map.put("player", authentication.getName());
        }
        /*else {
            map.put("player", playerRepository.findByUsername(authentication.getName()).playerDTO());
        }*/
        map.put("games", gameRepository
                .findAll()
                .stream()
                .map(game -> game.gameDTO())
                .collect(Collectors.toList()));
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping(path ="game_view/{gamePlayerId}") //path varialbe es un valor dinámico de la url
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long gamePlayerId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> responseEntity;

        if (gamePlayerRepository.findById(gamePlayerId) == null) {
            responseEntity = new ResponseEntity<>(makeMap("error", "There's no gameplayer with that ID."), HttpStatus.FORBIDDEN);
        }else{
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId);
            Player player = playerRepository.findByUsername(authentication.getName());
            if(gamePlayer.getPlayer().getId() == player.getId()){
                responseEntity = new ResponseEntity<>(gamePlayer.gameViewDTO(), HttpStatus.OK);
            } else{
                responseEntity = new ResponseEntity<>(makeMap("error", "not your game."), HttpStatus.FORBIDDEN);
            }
        }
        return responseEntity;
    }


    /*para unirse a un juego hay que chequear que:
    1) el user esté logueado
    2) el player no quiera unirse a un juego contra si mismo
    3) que el juego no esté lleno (i.e que tenga menos de dos jugadores)
    */
    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameId, Authentication authentication){
        ResponseEntity<Map<String, Object>> responseEntity;
        if(isGuest(authentication)){
            responseEntity = new ResponseEntity<>(makeMap("error","Not logged in"), HttpStatus.FORBIDDEN);
        }
       /* Optional<Game> game = gameRepository.findById(gameId);
        if(!game.isPresent()){
            return new ResponseEntity<>(makeMap("error","Game does not exist"), HttpStatus.FORBIDDEN);
        }
        if(game.get().getGamePlayers().size() > 1){
            return new ResponseEntity<>(makeMap("error","Game is full"), HttpStatus.FORBIDDEN);
        }

        */
        Game game = gameRepository.findById(gameId);
        if(game == null){
            responseEntity = new ResponseEntity<>(makeMap("error", "No such game."), HttpStatus.FORBIDDEN);
        }
        if(game.getGamePlayers().size() != 1){
            responseEntity = new ResponseEntity<>(makeMap("error", "This game is full."), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUsername(authentication.getName());
        if(game.getGamePlayers().stream().anyMatch(gamePlayer -> gamePlayer.getPlayer().getId() == player.getId())){
            responseEntity = new ResponseEntity<>(makeMap("error", "You are already playing this game."), HttpStatus.FORBIDDEN);
        }else{
            GamePlayer gamePlayer = new GamePlayer(LocalDateTime.now(), game ,player);
            gamePlayerRepository.save(gamePlayer);
            responseEntity = new ResponseEntity<>(makeMap("gpId", gamePlayer.getId()),HttpStatus.CREATED);
        }

        return responseEntity;
    }




/*//join a game: hay que chequear que esté logueada la persona, el player no quiera unirse a un juego contra si mismo y que el juego no esté lleno (i.e ya hay dos jugadores)
    @RequestMapping(path ="game_view/{gameId}/players") //path varialbe es un valor dinámico de la url
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameId, Authentication authentication) {
        Game game = gameRepository.findById(gameId);
        Player player = playerRepository.findByUsername(authentication);

        Map<String,Object> map = new HashMap<>();
        if(isGuest(authentication)){
            map.put("player", "GUEST");
        }
        else if (game == null){
            response = new ResponseEntity<>(makeMap("error", "missing data."), HttpStatus.FORBIDDEN);
        }

        ResponseEntity<Map<String, Object>> responseEntity;
        if (gamePlayerRepository.findById(gamePlayerId) == null) {
            responseEntity = new ResponseEntity<>(makeMap("error", "missing data."), HttpStatus.FORBIDDEN);
        }else{
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId);
            Player player = playerRepository.findByUsername(authentication.getName());
            if(gamePlayer.getPlayer().getId() == player.getId()){
                responseEntity = new ResponseEntity<>(gamePlayer.gameViewDTO(), HttpStatus.OK);
            } else{
                responseEntity = new ResponseEntity<>(makeMap("error", "not your game."), HttpStatus.FORBIDDEN);
            }
        }
        return responseEntity;
    }


//añadir barcos
@PostMapping(path ="games/{gamePlayerId}/ships")

public ResponseEntity addShips(Authentication authentication, @PathVariable long gamePlayerId){
    @RequestBody(List<Ship>ships){
        if is guest...
        else if(gamePlayerId == null){
            mesaje de error // si no se chequea esto antes, en el proximo paso da error 500 de servidor si no existe el usuario
        } else if(gamePlayerId.getPlayer().getId() != player.getId()){

        }else if(gamePlayerId.getShips().size()>0){
            forbidden
        }else if(ships.size()!=5){ //estos ships son el parámetro de arriba (sandwich)
            hay que posicionar 5 ships (forbidden)
        } else if(opcional: ver que los ships que estan sean del tamaño que tienen que ser(ie 1 de 5, 1 de 4, 2 de 3 y 1 de 2)){
            fobidden
        }else{//agregar ships:
            ships.stream().forEach(ship =>){
                gp.addShip(ship)
            }
            gamePlayerRepository.save(gp)
                    response enttity dreated
        }
        return response entity
    }
}




TAREA 4







  */













        private Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new HashMap<>();
        map.put(key,value);
        return map;
    }


    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> responseEntity;

        if (isGuest(authentication)) {
            responseEntity = new ResponseEntity<>(makeMap("error","Not logged in"), HttpStatus.UNAUTHORIZED); //Si ni está logueado, devuelve un error del tipo 401 con el mensaje "Not logged in"
        }else {
            Game game = new Game(LocalDateTime.now());
            gameRepository.save(game);
            Player currentPlayer = playerRepository.findByUsername(authentication.getName());
            GamePlayer gamePlayer = new GamePlayer(LocalDateTime.now(), game ,currentPlayer);
            gamePlayerRepository.save(gamePlayer);
            responseEntity = new ResponseEntity<>(makeMap("gpId", gamePlayer.getId()),HttpStatus.CREATED); //lo que nos pide la plataforma es que se le mande al front end el id de game layer para poder dirigir al usuario a su juego.  Manda un 201 (operación exitosa)
        }
        return responseEntity;
    }



/*tarea 4
    @PostMapping("games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> addSalvoes(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Salvo salvo){
        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error","forbidden"), HttpStatus.FORBIDDEN);
        }
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if(!gamePlayer.isPresent()){
            return new ResponseEntity<>(makeMap("error","game does not exist"), HttpStatus.BAD_REQUEST);
        }
        if(!gamePlayer.get().getPlayer().getUsername().equals(authentication.getName())){
            return new ResponseEntity<>(makeMap("error","forbidden"), HttpStatus.FORBIDDEN);
        }

        if(gamePlayer.get().getSalvoes().stream().anyMatch(item -> item.getTurn() == salvo.getTurn()) ){
            return new ResponseEntity<>(makeMap("error","forbidden"), HttpStatus.FORBIDDEN);
        }

        Optional<GamePlayer> opponentGamePlayer = gamePlayer.get().getGame().getGamePlayers().stream().filter(gp -> gp.getId() != gamePlayerId).findFirst();

        if(!opponentGamePlayer.isPresent() || salvo.getTurn() -1 > opponentGamePlayer.get().getSalvoes().size()){
            return  new ResponseEntity<>(makeMap("error","forbidden"), HttpStatus.FORBIDDEN);
        }

        if(opponentGamePlayer.get().getTransformers().isEmpty()){
            return  new ResponseEntity<>(makeMap("error","forbidden"), HttpStatus.FORBIDDEN);
        }

        Set<Salvo> salvoSet = new HashSet<>();
        salvoSet.add(salvo);
        gamePlayer.get().addSalvoes(salvoSet);
        GamePlayer gamePlayerSaved = gamePlayerRepository.save(gamePlayer.get());
        if (gamePlayerSaved != null){
            if (gamePlayer.get().getGameState() == GameState.WIN){
                scoreRepository.save(new Score(gamePlayer.get().getPlayer(), gamePlayer.get().getGame(), 1.0F, LocalDateTime.now()));
                scoreRepository.save(new Score(opponentGamePlayer.get().getPlayer(), opponentGamePlayer.get().getGame(), 0.0F, LocalDateTime.now()));
            } else if (gamePlayer.get().getGameState() == GameState.LOSE){
                scoreRepository.save(new Score(gamePlayer.get().getPlayer(), gamePlayer.get().getGame(), 0.0F, LocalDateTime.now()));
                scoreRepository.save(new Score(opponentGamePlayer.get().getPlayer(), opponentGamePlayer.get().getGame(), 1.0F, LocalDateTime.now()));
            } else if (gamePlayer.get().getGameState() == GameState.DRAW){
                scoreRepository.save(new Score(gamePlayer.get().getPlayer(), gamePlayer.get().getGame(), 0.5F, LocalDateTime.now()));
                scoreRepository.save(new Score(opponentGamePlayer.get().getPlayer(), opponentGamePlayer.get().getGame(), 0.5F, LocalDateTime.now()));
            }
        }
        return  new ResponseEntity<>(makeMap(Messages.KEY_CREATED, Messages.MSG_CREATED), HttpStatus.CREATED);
    }
*/



    }
