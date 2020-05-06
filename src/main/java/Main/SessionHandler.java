package Main;

import Database.DatabaseHandler;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class SessionHandler {


    private static final String startRep = "20000";
    private static final int cookieDuration = 360000;

    /**
     * Logs in a user using the request and response cookies.
     * @param request The post request sent from Spark
     * @param response The post response sent from spark
     * @param db       The database where users are stored
     * @return Boolean that indicates whether login was successful
     */
    public static Boolean loginUser(Request request, Response response, DatabaseHandler db){
        Boolean successfulLogin = false;
        String username = request.queryMap().value("username");
        String password = request.queryMap().value("password");
        User user = null;
        //tries to get the value of the userID from the database
        try {
            user = db.getUser(String.valueOf(username.hashCode()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //if the user is not found in the database,
        if(user == null) {
                try {
                    //creates new user in the database
                    db.addNewUser(String.valueOf(username.hashCode()), username, startRep , username, password);
                    response.cookie("username", username, cookieDuration);
                    response.cookie("password", password, cookieDuration);
                    successfulLogin = true;
                } catch (SQLException e1) {
                    System.out.println("There was a problem adding the user to the database");
                    successfulLogin = false;
                }
                return successfulLogin;
            }
        //if user is already in the database, compare the password input
        //to the stored password
         if(user.getAuth().equals(password)){

             response.cookie("username", username, cookieDuration);
                response.cookie("password", password, cookieDuration);
                successfulLogin = true;
            } else {
                successfulLogin = false;
            }

        return successfulLogin;
    }

    /**
     * Gets the user stored in a request cookie.
     * @param req The post request sent from spark
     * @param db The database where users are stored
     * @return The user, or null if none is found
     */
    public static User getUserFromRequestCookie(Request req, DatabaseHandler db){
        String username = req.cookie("username");
        String password = req.cookie("password");
        //tries to get the user with the given username and password
        try {
            User u = db.getUser(username, password);
            return u;
        } catch (SQLException e) {
            System.out.println("There was a problem getting the logged in User's profile");
            return null;
        } 

    }

    /**
     * Logs the user out of the website by removing cookie.
     * @param response
     */
    public static void logoutUser(Response response){
        response.removeCookie("username");
        response.removeCookie("password");
    }

    /**
     * Checks if a user is logged in.
     * @param req Request sent from Spark
     * @return Whether there is a valid user stored in the cookie
     */
    public static boolean isUserLoggedIn(Request req){
        String username = req.cookie("username");
        String password = req.cookie("password");
        if (username == null || username.equals("") || password == null || password.equals("")) {
            return false;
        }
        return true;
    }

}