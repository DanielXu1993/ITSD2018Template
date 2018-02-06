<html>

<head>
    <!-- Web page title -->
    <title>Top Trumps</title>

    <!-- Import JQuery, as it provides functions you will probably find useful (see https://jquery.com/) -->
    <script src="https://code.jquery.com/jquery-2.1.1.js"></script>
    <script src="https://code.jquery.com/ui/1.11.1/jquery-ui.js"></script>
    <link rel="stylesheet"
          href="https://code.jquery.com/ui/1.11.1/themes/flick/jquery-ui.css">

    <!-- Optional Styling of the Website, for the demo I used Bootstrap (see https://getbootstrap.com/docs/4.0/getting-started/introduction/) -->
    <link rel="stylesheet"
          href="http://dcs.gla.ac.uk/~richardm/TREC_IS/bootstrap.min.css">
    <script src="http://dcs.gla.ac.uk/~richardm/vex.combined.min.js"></script>
    <script>
        vex.defaultOptions.className = 'vex-theme-os';
    </script>
    <link rel="stylesheet"
          href="http://dcs.gla.ac.uk/~richardm/assets/stylesheets/vex.css"/>
    <link rel="stylesheet"
          href="http://dcs.gla.ac.uk/~richardm/assets/stylesheets/vex-theme-os.css"/>
    <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">

</head>

<body onload="initalize()">
<!-- Call the initalize method when the page loads -->

<div class="container">

    <div class="col-sm-12" style="background-color: black; font-size: 30; color: white;">
        Top Trumps Game
    </div>
    <div class="col-sm-12" style="background-color: dodgerblue; font-size: 20; color: white;height:40" id="infomation"></div>
    <br/>
    <div class="row text-center">
        <div class="col-sm-3" id="control">
            <button type="button" class="btn btn-success" id="startButton" onclick="gameControl()">NEXT ROUND</button>
        </div>
        <div class="col-sm-9">
            <div class="row" id="cards"></div>
        </div>

    </div>
</div>
<script type="text/javascript">
    var currentPlayer;
    var category;
    var cardNum;
    var currentRound;
    var drawNum;
    var runStatus;
    var winnerInfo;

    $(function () {
    	newGame();
    	getCurrentRound();
    	setValues();
    })

    function setValues()
    {
    	cateInfo();
        getCurrentPlayer();
        cardCount();
        getDrawNum();
        getRunStatus();
        
    }
    // Method that is called on page load
    function initalize() {
        $("#startButton").click();
    }

    function gameControl() {
        $activePlayer = $("<div class='card text-white bg-info'>"
                + "<div class='card-body'><h5 class='card-title'>The active player is</h5><br/>"
                + "<h5 class='card-text' id='activePlayer'></h5></div></div>");
        $("#control").html($activePlayer);
        $select = $("<div class='card text-white bg-success' id = 'selection'>"
                + "<div class='card-body'><button class='btn btn-success'"
                + "onclick='categorySelection()'>NEXT:CATEGORY SELECTION</button></div></div>");
        $("#control").append($select);
        if (currentPlayer == 0) {
            $("#activePlayer").text("You");
        } else {
            $("#activePlayer").text("AI Player " + currentPlayer);
        }
        setFirstCards();
        
        $("#infomation").text("Round " + currentRound
                + " : Players have drawn their cards");
        //activeColor();
    }
    function categorySelection() {
    	$("#selection").removeClass("bg-success");
        if (currentPlayer == 0) {
            humanSelect = "<ul class='list-group list-group-flush'>";
            $.each(category, function (itemIndex, item) {
                if (itemIndex != 0) {
                    humanSelect = humanSelect
                            + "<li class='list-group-item list-group-item-action "
                            + "list-group-item-success' onclick ='categorySelected("
                            + itemIndex + ")'>SELECT:"
                            + item.toUpperCase() + "</li>"
                }
            });
            humanSelect = humanSelect + "</ul>";
            $("#infomation").text("Round " + currentRound
                    + " : Waitting on You to select a category");
            $("#selection").html(humanSelect);
        } else {
            categorySelected(-1);
        }

    }

    function categorySelected(index) {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/selectionAndResult?index=" + index); // Request type and URL+parameters

        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }

        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            var cateAndWinner = eval(responseText);
            setValues();
            finalWinnerInfo();
            showSelection(cateAndWinner[0]);
            showWinner(cateAndWinner[1]);
        };

        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();
    }

    function showSelection(selectedIndex) {
        $selecctionInfo = $("<div class='card-body'><h5 class='card-title' style='color:black' >They selected</h5>"
                + "<h5 class='card-text' style='color:black'>\""
                + category[selectedIndex] + "\"</h5></div>");
        $("#selection").html($selecctionInfo);
        var playerName;
        if (currentPlayer == 0) {
            playerName = "You";
        } else {
            playerName = "AI Player" + currentPlayer;
        }
        $("#infomation").text("Round " + currentRound + " : " + playerName + " selected "
                + category[selectedIndex]);
    }

    function showWinner(winnerIndex) {
        $winner = $("<div class='card text-white bg-success'>"
                + "<div class='card-body'>"
                + "<button class='btn btn-success' onclick = 'nextRound("
                + winnerIndex + ")'>SHOW WINNER</button>" + "</div></div>");
        $("#control").append($winner);
        $(".AICards").css("visibility","visible");
    }

    function nextRound(winnerIndex) {
    	if(runStatus){
        	$("#control").html(
                "<button type='button' class='btn btn-success' onclick='gameControl()'>NEXT ROUND</button>");
        	$("#cards").html("");	
    	}
    	else{
    		$gameOver = $("<h5 class='card-title'>The game is over </h5>");            
            $("#activePlayer").parent().html($gameOver);
    		$gameInfo =$("<ul class='list-group'>"
			+"<li class='list-group-item bg-success' "
			+"onclick='gameSelect()'>CLICK TO RETURN TO THE SELECT SCREEN</li>"
			+"<li class='list-group-item list-group-item-light'><pre class='card-text' style='text-align:left;white-space:pre-wrap;'>"+winnerInfo+"</pre></li></ul>");	
    		$("#selection").html($gameInfo);
    		$("#cards").html("");
    		setFirstCards();
    		storeResult();
    	}
        if (winnerIndex == -1) {
            $("#infomation").text(
                    "Round " + currentRound
                    + " : This was a Draw,common pile now has "
                    + drawNum + " cards");
        } else {
            if (winnerIndex == 0) {
                winnerName = "You";
            } else {
                winnerName = "AI Player" + winnerIndex;
            }
            $("#infomation").text(
                    "Round " + currentRound + " : Player " + winnerName
                    + " won this round");
        }
        getCurrentRound(); 
    }
	function gameSelect(){
		window.location.href="/toptrumps";
	}
    function cateInfo() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/cateInfo"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            category = eval(responseText);
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();
    }

    function cardCount() {

        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/cardCount"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            cardNum = eval(responseText);
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();
    }

    function setFirstCards() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/firstCards"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            var $firstCards = eval(responseText);
            $("#cards").text("");
            $.each($firstCards, function (index, card) {
                var cardInfo = new Array();
                if (card != null) {
                    cardInfo = card.split(" ");
                    var playerCard = "";
                    if (index == 0) {
                        playerCard = playerCard
                                + "<div class='col-sm-4'><div class='card text-white bg-danger' id = 'humanCard'>"
                                + "<div class='card-body'>"
                                + "<h5 class='card-title'>You</h5>"
                    } else {
                        playerCard = playerCard
                                + "<div class='col-sm-4 AICards' style = 'visibility:hidden'><div class='card text-white bg-danger' id = 'AICard"+index+"'>"
                                + "<div class='card-body'>"
                                + "<h5 class='card-title'>AI Player "
                                + index + "</h5>"
                    }
                    playerCard = playerCard
                            + "</div></div><div class='card text-black'><div class='card-body'>";

                    $.each(cardInfo, function (itemIndex,item) {
                        if (itemIndex == 0) {
                            playerCard = playerCard
                                    + "<h5 class='card-title'>"
                                    + item
                                    + "  <span class='badge badge-info'>"
                                    + cardNum[index]
                                    + "</span></h5>";
                        } else {
                            playerCard = playerCard
                                    + "<h5 class='card-title'>"
                                    + category[itemIndex]
                                    + ", "
                                    + item
                                    + "</h5>";
                        }
                    });
                    playerCard = playerCard + "</div></div>";

                    $("#cards").append(playerCard);
                }
                
            });
            activeColor();
        };

        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();
    }

    function activeColor(){
		if(currentPlayer == 0){
			$("#humanCard").removeClass("bg-danger");
			$("#humanCard").addClass("bg-success"); 
		}else
		{
			$("#AICard"+currentPlayer).removeClass("bg-danger");
			$("#AICard"+currentPlayer).addClass("bg-success"); 
		}
	}
    
    function getCurrentPlayer() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/getCurrentPlayer"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            currentPlayer = eval(responseText);
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();
    }

    function getCurrentRound() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/getCurrentRound"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            currentRound = eval(responseText);
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();

    }

    function getDrawNum() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/communalPileLength"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            drawNum = eval(responseText);
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();

    }

    function getRunStatus() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/getRunStatus"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            runStatus = eval(responseText);
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();
    }
    
    function finalWinnerInfo() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/finalWinner"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
            winnerInfo = eval(responseText);
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();

    }
    
    function  storeResult() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/storeResult"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();

    }
    function  newGame() {
        // First create a CORS request, this is the message we are going to send (a get request in this case)
        var xhr = createCORSRequest('GET',
                "http://localhost:7777/toptrumps/newGame"); // Request type and URL
        // Message is not sent yet, but we can check that the browser supports CORS
        if (!xhr) {
            alert("CORS not supported");
        }
        // CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
        // to do when the response arrives
        xhr.onload = function (e) {
            var responseText = xhr.response; // the text of the response
        };
        // We have done everything we need to prepare the CORS request, so send it
        xhr.send();

    }

    // This is a reusable method for creating a CORS request. Do not edit this.
    function createCORSRequest(method, url) {
        var xhr = new XMLHttpRequest();
        if ("withCredentials" in xhr) {

            // Check if the XMLHttpRequest object has a "withCredentials" property.
            // "withCredentials" only exists on XMLHTTPRequest2 objects.
            xhr.open(method, url, true);

        } else if (typeof XDomainRequest != "undefined") {

            // Otherwise, check if XDomainRequest.
            // XDomainRequest only exists in IE, and is IE's way of making CORS requests.
            xhr = new XDomainRequest();
            xhr.open(method, url);

        } else {

            // Otherwise, CORS is not supported by the browser.
            xhr = null;

        }
        return xhr;
    }
</script>
</body>
</html>