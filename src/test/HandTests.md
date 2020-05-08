### The tests in this file were run by hand on the frontend. The purpose of these tests are to ensure that all of the session info is correctly processed, and form input is correctly formatted.

# **Splash Page Tests**

- **Blank credentials test:** leaving one or both credentials in either form blank will result in serving the same page with error message.
- **Unknown user login test:** New user credentials are entered in the login forms. Expected result of no log in (since there are no existing credentials that match the entered ones) observed.
- **Create account test:** Creating an account with new credentials logs the new user in with the entered credential, returning the list of bets page.
- **Creating account with existing username test:** Creating an account with the same username as one already in the database will result in serving the same page with error message
- **Creating account or logging in with non alpha numeric characters test:**
  The app will attempt to log you in with a trimmed version of the string you put in. If it cant, it will return the same page with an error.
- **Logging in with existing username but wrong password test:** this will return the same page with an error
- **Logging in with existing credentials test:** this will log in the user and return the list of bets page.

# **Champion page tests**

- **Entering a bet test:** Entering a bet in any of the three rates returns the current page and adds the bet to the user, which can be verified by accessing "Profile" from the sidebar   
- **Numeric forms will not let you put in the wrong format**: Entering an invalid number throws an error
- **Entering existing bet test**: Entering a bet identical to another you have already made will return the samge page and display an error 
- **Betting more rep than you have test:** Wagering more rep than you currently have will return the same page and return and display an error


# **Other tests**
- **Logout test:** clicking the logout button on any page will remove the cookie and return to the main page, where the user will have to log in again
- **Leaderboards test:** going to the page will display up to the top 50 users sorted by reputation and ties broken by time of account creation. The top 3 will have gold, silver, and bronze borders.
- **Not logged in test:** going to any page when the user is not logged in will redirect to the main page and display an error
- **Invalid champion page test:** if a user enters the URL for a page that does not point to a champion, they will be redirected to Aatrox's page. Entering a bet on that page is equivalent to entering a bet for Aatrox.