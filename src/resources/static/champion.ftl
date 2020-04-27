<!doctype html>

<html lang="en">
<head>
  <meta charset="utf-8">

  <title>5Head.gg</title>
  <meta name="description" content="5Head.gg">

  <link rel="stylesheet" href="css/champion">

</head>

<body>

    <div id="sidebar">
        <div id="logo">

        </div>

        <div class="left-aligned" id="reputation">
            ${userReputation}
            <p style="color:#84929B;">REPUTATION</p>

        </div>

        <div class="left-aligned" id="buttons">
            <a class="sidebarlink" href="currpatch"> <p style="font-weight: bold; color: #FEFEFE">Current Patch</p></a>

            <a class="sidebarlink" href="mybets">My Bets</a>

            <a class="sidebarlink" href="leaderboard">Leaderboards</a>
        </div>

        <div id="bettingstatus">
            ${bettingStatus}
        </div>
        
        <div id="profile">
            ${profileImage}
            ${profileName}
            <a href="profile"><img src="up-arrow"></a>
        </div>
    </div>
    <div id="canvas">
        <div id="champ-image" style="background-image: '${champSplashimage}'">

            <!-- winrate tab -- no submit button -->
            <div class="rate">

                <div class="graph">
                    ${winrateGraph}
                </div>

                <div class="bet">
                    <div class="plus-button">
                        +
                    </div>
                    <form id="percentage">
                        <input type="text">
                    </form>
                    <div class="minus-button">
                        -
                    </div>
                </div>
                
                <div class="amount">
                    <div class="plus-button">
                        +
                    </div>
                    <form id="stake">
                        <input type="text">
                    </form>
                    <div class="minus-button">
                        -
                    </div>
                </div>

                <div class="winnings">
                    9999
                </div>

            </div>

            <!-- pickrate tab -- no submit button -->
            <div class="rate">

                <div class="graph">
                    ${pickrateGraph}
                </div>

                <div class="bet">
                    <div class="plus-button">
                        +
                    </div>
                    <form id="percentage">
                        <input type="text">
                    </form>
                    <div class="minus-button">
                        -
                    </div>
                </div>
                
                <div class="amount">
                    <div class="plus-button">
                        +
                    </div>
                    <form id="stake">
                        <input type="text">
                    </form>
                    <div class="minus-button">
                        -
                    </div>
                </div>

                <div class="winnings">
                    9999
                </div>

            </div>

            <!-- banrate tab -- no submit button -->
            <div class="rate">

                <div class="graph">
                    ${banrateGraph}
                </div>

                <div class="bet">
                    <div class="plus-button">
                        +
                    </div>
                    <form id="percentage">
                        <input type="text">
                    </form>
                    <div class="minus-button">
                        -
                    </div>
                </div>
                
                <div class="amount">
                    <div class="plus-button">
                        +
                    </div>
                    <form id="stake">
                        <input type="text">
                    </form>
                    <div class="minus-button">
                        -
                    </div>
                </div>

                <div class="winnings">
                    9999
                </div>

            </div>


        </div>
    </div>
</body>
</html>