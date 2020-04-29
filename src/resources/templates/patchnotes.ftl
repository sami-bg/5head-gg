<!doctype html>

<html lang="en">
<head>
  <meta charset="utf-8">

  <title>5Head.gg</title>
  <meta name="description" content="5Head.gg">

  <link rel="stylesheet" href="/css/patchnotes.css">

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

            <a class="sidebarlink" href="mybets"><p style="font-weight: normal; color: #84929E"">Profile</p></a>

            <a class="sidebarlink" href="leaderboard"><p style="font-weight: normal; color: #84929E"">Leaderboards</p></a>
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
        <div id="patchnotes-embed">
            <div class="content-border">
                <div class="patch-change-block white-stone accent-before">
                    <div>
                        <a class="reference-link" href="http://gameinfo.na.leagueoflegends.com/en/game-info/champions/kayn/"><img src="https://am-a.akamaihd.net/image?f=http://ddragon.leagueoflegends.com/cdn/10.8.1/img/champion/Kayn.png"></a>
                        <h3 class="change-title" id="patch-kayn"><a href="http://gameinfo.na.leagueoflegends.com/en/game-info/champions/kayn/">Kayn</a></h3>
                        <p class="summary">Q cooldown decreased; bonus damage now applies to minions. W decaying slow increased.</p>
                        <blockquote class="blockquote context">Our last few adjustments to Kayn haven’t pulled him out of the shadows as we’d expected, so we're adding a dash of spice to his overall game impact, from ganks to duels. We're hoping that the addition of minion damage on his Q will give Kayn more positional flexibility, and that lower cooldowns and increased slows will help players capitalize on faster clearing and early skirmishes.</blockquote>

                        <hr class="divider">
                        <h4 class="change-detail-title ability-title"><img src="https://am-a.akamaihd.net/image?f=http://ddragon.leagueoflegends.com/cdn/10.8.1/img/spell/KaynQ.png">Q - Reaping Slash</h4>
                        <div class="attribute-change"><span class="attribute">COOLDOWN</span>
                            <span class="attribute-before">7/6.5/6/5.5/5 seconds</span>
                            <span class="change-indicator">⇒</span>
                            <span class="attribute-after">6/5.5/5/4.5/4 seconds</span>
                        </div>
                        <div class="attribute-change"><span class="attribute"><span class="new">new</span>MONSTER MASH</span>
                            <span class="attribute-after">Bonus damage against monsters now also applies to minions</span>
                        </div>

                        <h4 class="change-detail-title ability-title"><img src="https://am-a.akamaihd.net/image?f=http://ddragon.leagueoflegends.com/cdn/10.8.1/img/spell/KaynW.png">W - Blade’s Reach</h4>
                        <div class="attribute-change"><span class="attribute">DECAYING SLOW</span>
                            <span class="attribute-before">70%</span>
                            <span class="change-indicator">⇒</span>
                            <span class="attribute-after">90%</span>
                        </div>

                    </div>
                </div>
            </div>
        <div id="champlist"> ${championDivs} </div>
        <!-- div for each champion -->
    </div>
</body>
</html>