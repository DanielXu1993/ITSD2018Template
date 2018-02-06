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

<body>
<!-- Call the initalize method when the page loads -->

<div class="container">
    <div style="background-color: black; font-size: 30; color: white;">Top
        Trumps Game
    </div>
    <div style="height: 20"></div>
    <div class="row text-center">

        <div class="col-sm-6">
            <ul class="list-group">
                <li class="list-group-item" style="background-color: cornsilk">New Game</li>
                <li class="list-group-item list-group-item-light"
                    onclick="javascript:window.location.href='/toptrumps/game'">Start a new Top Trumps
                    Game
                </li>
            </ul>
        </div>

        <div class="col-sm-6">
            <ul class="list-group">
                <li class="list-group-item" style="background-color: cornsilk">Game Statistics</li>
                <li class="list-group-item list-group-item-light"
                    onclick="javascript:window.location.href='/toptrumps/stats'">Get Statistics from past
                    Game
                </li>
            </ul>
        </div>


    </div>
</div>
</body>
</html>