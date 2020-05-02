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
            <a class="sidebarlink" href="currpatch"> <p style="font-weight: bold; color: #FEFEFE">Current Main.Patch</p></a>

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
	${success}
       <form method="POST" action="/mybets/success">
    <label for="rep" style="font-family: georgia">
    Enter your bet here:
    </label><br>
	  <p style="font-family: georgia"> <b>Amount of reputation:</b></p>
<input type="number" id="rep" name="rep"
       step="1">
	   
	   <b>Main.Champion:</b>
	   <select class="dropbtn" name="champion" value="Aatrox">
  <div class="dropdown-content">
    ${champOptions}
	</select>
  </div>
  <b>Statistic of bet:</b>
    <select class="dropbtn" name="betType" value="pick">
  <div class="dropdown-content">
    <option value="pick">Pick rate</option>
    <option value="ban">Ban rate</option>
	<option value="win">Win rate</option>
	</select>
  </div>
    <input type="submit" alt="Submit">
    </form>
    </div>
</body>
</html>