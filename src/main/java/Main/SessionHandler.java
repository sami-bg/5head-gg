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
        password = String.valueOf(password.hashCode());
        User user = null;
        //tries to get the value of the userID from the database
        try {
            user = db.getUser(String.valueOf(username.hashCode()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        //if the user is not found in the database,
        if(user == null) {
                return false;
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


    /**
     * Given a response and request, creates a new password for the posted username/password combo, if the username isnt already taken
     * @param response
     * @param request
     * @param db
     * @return
     */
    public static Boolean createNewAccount(Response response, Request request, DatabaseHandler db){
        Boolean successfulLogin = false;
        try {

            String username = request.queryMap().value("newusername");
            String password = request.queryMap().value("newpassword");
            
            // if the username is already taken, return false
            if(db.getUser(String.valueOf(username.hashCode())) != null){
                return false;
            }
            
            //creates new user in the database
            db.addNewUser(String.valueOf(username.hashCode()), username, startRep , username, String.valueOf(password.hashCode()));
            response.cookie("username", username, cookieDuration);
            response.cookie("password", String.valueOf(password.hashCode()), cookieDuration);
            successfulLogin = true;
        } catch (SQLException e1) {
            System.out.println("There was a problem adding the user to the database");
            successfulLogin = false;
        }
        return successfulLogin;
    }

}