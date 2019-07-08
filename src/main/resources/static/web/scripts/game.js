//Vue.js - app
var app = new Vue({
    el: '#app',
//data in Vue.js
    data: {
     usernameP1: "",
     usernameP2: "",
     currentPlayer: ""

    }

})


fetch("/api/game_view/"+ paramObj(location.search).gp, {mode:'no-cors'})
.then(function(response) {

    return response.json()

}).then(function(json) {
    if(json.error != undefined){
        console.log(json.error)
        document.getElementById('error').classList.add('show')
        document.getElementById('app').style.display = 'none'
    } else{
        document.getElementById('error').classList.remove('show')
        document.getElementById('app').style.display = 'block'
        gameView = json;
        printPlayersUsername(); // calling the funtion that prints the game players username
        loadGrid(); // loads grid when it loads the page
        salvoes();// loads salvoes when it loads the page
    }
    if(paramObj(location.search).join != undefined && paramObj(location.search).join == 'true'){
                swal({text:"Hey " + app.currentPlayer + ", you've joined a new game. Have fun!", icon:"success", button: {text:"Thanks", className:"join-button"}})
            }
     if(paramObj(location.search).newgame != undefined && paramObj(location.search).newgame == 'true'){
                       swal({text:"Hey " + app.currentPlayer +", you've created a new game! Have fun!", icon:"success", button: {text:"Great", className:"createdGame-button"}})
                   }

}).catch(function(error){
    console.log(error.error)
    //swal({text:"Invalid Username or Password", icon:"warning", button:{className:"fail-login-button"}});
});








/*fetch
let gameView=[];

fetch("/api/game_view/"+ paramObj(location.search).gp, {mode:'no-cors'})
.then(function(response) {
    return response.json()
}).then(function(json) {
    gameView = json;
    printPlayersUsername(); // calling the funtion that prints the game players username
    loadGrid(); // loads grid when it loads the page
    salvoes();// loads salvoes when it loads the page
});*/


// printing game players usernames depending on the id
function printPlayersUsername(){
    if(gameView.gamePlayers[0].id == paramObj(location.search).gp){
        app.usernameP1 = gameView.gamePlayers[0].player.user;
        app.currentPlayer = gameView.gamePlayers[0].player.id
        app.usernameP2 = gameView.gamePlayers[1].player.user;
    }else{
        app.usernameP1 = gameView.gamePlayers[1].player.user;
        app.usernameP2 = gameView.gamePlayers[0].player.user;
        app.currentPlayer = gameView.gamePlayers[1].player.id
    }
}

// function that makes the symbols compatible on the url
function paramObj(search) {
    var obj = {};
    var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

    search.replace(reg, function(match, param, val) {
      obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });

    return obj;
}

/*SHIPS GRID*/

//main function that shoots the gridstack.js framework and load the grid with the ships
const loadGrid = function () {
    var options = {
        //10 x 10 grid
        width: 10,
        height: 10,
        //space between elements (widgets)
        verticalMargin: 0,
        //height of cells
        cellHeight: 45,
        //disables resizing of widgets
        disableResize: true,
        //floating widgets
		float: true,
        //removeTimeout: 100,
        //allows the widget to occupy more than one column
        disableOneColumnMode: true,
        //false allows widget dragging, true denies it
        staticGrid: false,
        //activates animations
        animate: true
    }


    //grid initialization
    $('.grid-stack').gridstack(options);

    grid = $('#grid').data('gridstack');

    //adding the ships already created in the back-end
    for(i=0;i<gameView.ships.length;i++){
        let shipType = gameView.ships[i].type; //type
        let x = +(gameView.ships[i].location[0].slice(1)) - 1; //number
        let y = gameView.ships[i].location[0].slice(0,1).toUpperCase().charCodeAt(0)-65; //letter
        let w; //width
        let h; //height

        if(gameView.ships[i].location[0].slice(0,1) == gameView.ships[i].location[1].slice(0,1)){
            w = gameView.ships[i].location.length;
            h = 1;
            grid.addWidget($('<div id="'+shipType+'"><div class="grid-stack-item-content '+shipType+'Horizontal"></div><div/>'), x, y, w, h);
            //check that the shipType is written in camel case in the back-end Salvo Application
        } else{
            h = gameView.ships[i].location.length;
            w = 1;
            grid.addWidget($('<div id="'+shipType+'"><div class="grid-stack-item-content '+shipType+'Vertical"></div><div/>'), x, y, w, h);
            //check that the shipType is written in camel case in the back-end Salvo Application
        }
    }


    createGrid(11, $(".grid-ships"), 'ships')

    rotateShips("carrier", 5)
    rotateShips("battleship", 4)
    rotateShips("submarine",3)
    rotateShips("destroyer", 3)
    rotateShips("patrolBoat",2)

    listenBusyCells('ships')
    $('.grid-stack').on('change', function(){listenBusyCells('ships')})


    //all the functionalities are explained in the gridstack github
    //https://github.com/gridstack/gridstack.js/tree/develop/doc

}


//creates the grid structure
const createGrid = function(size, element, id){

    let wrapper = document.createElement('DIV')
    wrapper.classList.add('grid-wrapper')

    for(let i = 0; i < size; i++){
        let row = document.createElement('DIV')
        row.classList.add('grid-row')
        row.id =id+`grid-row${i}`
        wrapper.appendChild(row)

        for(let j = 0; j < size; j++){
            let cell = document.createElement('DIV')
            cell.classList.add('grid-cell')
            if(i > 0 && j > 0)
            cell.id = id+`${i - 1}${ j - 1}`

            if(j===0 && i > 0){
                let textNode = document.createElement('SPAN')
                textNode.innerText = String.fromCharCode(i+64)
                cell.appendChild(textNode)
            }
            if(i === 0 && j > 0){
                let textNode = document.createElement('SPAN')
                textNode.innerText = j
                cell.appendChild(textNode)
            }
            row.appendChild(cell)
        }
    }

    element.append(wrapper)
}

//adds a listener to the ships, wich shoots its rotation when clicked
const rotateShips = function(shipType, cells){

        $(`#${shipType}`).click(function(){
            let x = +($(this).attr('data-gs-x'))
            let y = +($(this).attr('data-gs-y'))
        if($(this).children().hasClass(`${shipType}Horizontal`)){
            if(y + cells - 1 < 10){
                grid.resize($(this),1,cells);
                $(this).children().removeClass(`${shipType}Horizontal`);
                $(this).children().addClass(`${shipType}Vertical`);
            } else{
                grid.update($(this), null, 10 - cells)
                grid.resize($(this),1,cells);
                $(this).children().removeClass(`${shipType}Horizontal`);
                $(this).children().addClass(`${shipType}Vertical`);

            }

        }else{
            if(x + cells - 1  < 10){
                grid.resize($(this),cells,1);
                $(this).children().addClass(`${shipType}Horizontal`);
                $(this).children().removeClass(`${shipType}Vertical`);
            } else{
                grid.update($(this), 10 - cells)
                grid.resize($(this),cells,1);
                $(this).children().addClass(`${shipType}Horizontal`);
                $(this).children().removeClass(`${shipType}Vertical`);
            }

        }
    });

}

//loops over all the grid cells, verifying if they are empty or busy
const listenBusyCells = function(id){
    for(let i = 0; i < 10; i++){
        for(let j = 0; j < 10; j++){
            if(!grid.isAreaEmpty(i,j)){
                $(`#${id}${j}${i}`).addClass('busy-cell').removeClass('empty-cell')
            } else{
                $(`#${id}${j}${i}`).removeClass('busy-cell').addClass('empty-cell')
            }
        }
    }
}

/*SALVOES GRID*/

createGrid(11, $(".grid-salvoes"), 'salvoes')


  //adding the salvoes already created in the back-end
  function salvoes(){
    for(i=0;i<gameView.salvoes.length;i++){
        for(j=0; j<gameView.salvoes[i].locations.length;j++){
            let turn = gameView.salvoes[i].turn; //turn
            let player = gameView.salvoes[i].player;
            let x = +(gameView.salvoes[i].locations[j][1])-1; //number
            let y = gameView.salvoes[i].locations[j][0].slice(0,1).toUpperCase().charCodeAt(0)-65; //letter

            if(player == app.currentPlayer){
                document.getElementById("salvoes"+y+x).classList.add("salvo");
                document.getElementById("salvoes"+y+x).innerHTML="Turn " + turn;

            }else{
                if( document.querySelector("#ships"+y+x).className.indexOf("busy-cell") != -1){
                    document.getElementById("ships"+y+x).classList.add("salvo");
                    document.getElementById("ships"+y+x).innerHTML="Turn " + turn;
                }
            }
        }
    }
}






