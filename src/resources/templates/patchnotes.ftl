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
            <p id="repsubtitle">REPUTATION</p>

        </div>

        <div class="left-aligned" id="buttons">
            <a class="sidebarlink" href="currpatch"> <p style="font-weight: bold; color: #FEFEFE">Current Patch</p></a>

            <a class="sidebarlink" href="mybets"><p style="font-weight: normal;">Profile</p></a>

            <a class="sidebarlink" href="leaderboard"><p style="font-weight: normal;">Leaderboards</p></a>
        </div>

        <div id="bettingstatus">
            ${bettingStatus}
        </div>
        
        <div id="profile">
            ${profileImage}
            ${profileName}
            <a href="profile"><img src="up-arrow"></a>
        </div>
		
		<div id="logout">
		<form action="/" method="post">
		<button type="submit">Logout</button>
		</form>
		</div>
    </div>
    <div id="canvas">
        <div id="patchnotes-embed"> ${currentPatch} </div>
        <!-- div for each champion -->
        <div id="champlist"> ${championDivs} </div>
    </div>
</body>
</html>