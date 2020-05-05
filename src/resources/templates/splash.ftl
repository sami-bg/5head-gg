<!doctype html>

<html lang="en">
<head>
  <meta charset="utf-8">

  <title>5Head.gg</title>
  <meta name="description" content="5Head.gg">
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300&display=swap" rel="stylesheet"> 
  <link rel="stylesheet" href="/css/splash.css">

</head>

<body>
    <h1 id="title">5HEAD.gg</h1>
    <div><img src="/css/5head.png"></div>
    <div id="login">
      <p> Log in or create an account below.
      <br> 
      If you don't have an account, one will be made for you!</p>
	  ${incorrectPassword}
      <form method="POST" action="/mybets">
      <input type=text name="username">
      <input type="password" name="password">
      <input type="submit" value="Log In">
      </form>
    </div>

</body>
</html>