<!doctype html>

<html lang="en">
<head>
  <meta charset="utf-8">

  <title>5Head.gg</title>
  <meta name="description" content="5Head.gg">
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300&display=swap" rel="stylesheet"> 
  <link rel="stylesheet" href="/css/splash.css">
  <link rel="icon" href="/css/favicon.png">

</head>

<body>
    <h1 id="title">5HEAD.gg</h1>
    <h2 id="explanation">
      <p>Welcome to 5Head!</p><br>
      Want to put your LoL analytic powers to the test?<br>
      Predict a champion's winrate, pickrate, or banrate <br> with each patch and watch your reputation grow!<br>
      <p>Channel your inner 5HEAD</P>
    </h2>
    <div id="layout">
      <div><img src="/css/5head_logo.png"></div>

          <div id="login">
            <p> Log in or create an account below</p>
            <p style="color:red">${incorrectPassword}<p>
            <div id="entryforms">
              <form method="POST" action="/mybets">
                <div>
                <label for="username"> Username </label>
                <input type=text name="username">
                <label for="password"> Password </label>
                <input type="password" name="password">
                <input type="submit" value="Log In">
                </div>
                <div>
                <label for="newusername"> Username </label>
                <input type=text name="newusername">
                <label for="newpassword"> Password </label>
                <input type="password" name="newpassword">
                <input type="submit" value="Create Account">
                </div>
              </form>
        </div>
        </div>
    </div>

</body>
</html>