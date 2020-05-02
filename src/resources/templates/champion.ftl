<!doctype html>

<html lang="en">
<head>
  <meta charset="utf-8">

  <title>5Head.gg</title>
  <meta name="description" content="5Head.gg">

  <link rel="stylesheet" href="/css/champion.css">
  <script src="/js/Chart.js"></script>

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

            <a class="sidebarlink" href="mybets">Profile</a>

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
        <div id="champ-image" style="background-image: url('${champSplashimage}')">

            <!-- winrate tab -- no submit button -->
            <div class="rate">

                <div class="graph">
                    ${winrateGraph}
                    <canvas id="wrgraph"> </canvas>
                    <script src="js/wr.js"></script>
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
                    <canvas id="prgraph"> </canvas>
                    <script src="js/pr.js"></script>
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
                    <canvas id="brgraph"> </canvas>
                    <script src="js/br.js"></script>
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