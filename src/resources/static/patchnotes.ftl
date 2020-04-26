<!doctype html>

<html lang="en">
<head>
  <meta charset="utf-8">

  <title>5Head.gg</title>
  <meta name="description" content="5Head.gg">

  <link rel="stylesheet" href="css/patchnotes.css">

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
        <div id="patch-notes-embed" ><iframe src="${currentPatchLink}"></iframe></div>
    </div>
</body>
</html>