<!doctype html>

<html lang="en">
<head>
  <meta charset="utf-8">

  <title>5Head.gg</title>
  <meta name="description" content="5Head.gg">
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300&display=swap" rel="stylesheet"> 
  <link rel="stylesheet" href="/css/patchnotes.css">
  <link rel="icon" href="/css/favicon.png">

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
            <p style="font-weight: bold; color: #FEFEFE">${profileName}</p>
        </div>
		
		<div id="logout">
		<form action="/" method="post">
		<button class="logout" type="submit">Logout</button>
		</form>
		</div>
    </div>
    <div id="canvas">
        <div id="patchnotes-embed"> 
		<div id="patchexplanation"><p style="font-weight: bold; color: #FEFEFE">Current Patch Notes:</p></div>
		${currentPatch} </div>
        <!-- div for each champion -->
        <div id="champlist"><div id="champexplanation">
		<p style="font-weight: bold; color: #FEFEFE">Click on a portrait to go to that champion's page!</p>
		</div>
		${championDivs} </div>
    </div>
</body>
</html>