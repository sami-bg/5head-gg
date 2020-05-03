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
            <a class="sidebarlink" href="/currpatch"> <p style="font-weight: normal;">Current Patch</p></a>

            <a class="sidebarlink" href="/mybets"><p style="font-weight: normal;">Profile</p></a>

            <a class="sidebarlink" href="/leaderboard"><p style="font-weight: normal;">Leaderboards</p></a>
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
            <p id="champname"> ${champname} </p>

            <!-- winrate tab -- no submit button -->
            <div class="rate">

                <div class="graph">
                    ${winrateGraph}
                    <canvas id="wrgraph"> </canvas>
                </div>

                <div class="bet">
                    <div class="plus-button">
                        AMOUNT
                    </div>
                    <form method="/post">
                        <input id="wpercentage" type="number" value="0">
                    <div class="minus-button">
                        
                    </div>
                </div>
                
                <div class="amount">
                    <div class="plus-button">
                        REP STAKED
                    </div>
                        <input id="wstaked" type="number" value="0">
                    <div class="minus-button">
                        
                    </div>
                </div>

                <div class=submit>
                    <input class="button" type="submit" value="Submit Winrate Bet">
                    </form>
                </div> 

            </div>

            <!-- pickrate tab -- no submit button -->
            <div class="rate">

                <div class="graph">
                    ${pickrateGraph}
                    <canvas id="prgraph"> </canvas>
                    <#--  <script src="../js/prate.js"></script>  -->
                </div>

                <div class="bet">
                    <div class="plus-button">
                        AMOUNT
                    </div>
                    <form method="/post">
                        <input id= "ppercentage" type="number" value="0">
                    <div class="minus-button">
                        
                    </div>
                </div>
                
                <div class="amount">
                    <div class="plus-button">
                        REP STAKED
                    </div>
                        <input id="pstaked" type="number" value="0">
                    <div class="minus-button">
                        
                    </div>
                </div>

                <div class=submit>
                    <input class="button" type="submit" value="Submit Pickrate Bet">
                    </form>
                </div> 

            </div>

            <!-- banrate tab -- no submit button -->
            <div class="rate">

                <div class="graph">
                    ${banrateGraph}
                    <canvas id="brgraph"> </canvas>
                    <#--  <script src="../js/brate.js"></script>  -->
                </div>

                <div class="bet">
                    <div class="plus-button">
                        AMOUNT
                    </div>
                    <form method="/post">
                        <input id= "bpercentage" type="number" value="0">
                    <div class="minus-button">
                        
                    </div>
                </div>
                
                <div class="amount">
                    <div class="plus-button">
                        REP STAKED
                    </div>
                        <input id="bstake" type="number" value="0">
                    <div class="minus-button">
                        
                    </div>
                </div>

                <div class=submit>
                    <input class="button" type="submit" value="Submit Banrate Bet">
                    </form>
                </div> 
            </div>


        </div>
    </div>
    <script src="../js/wrate.js"></script>
</body>
</html>