package Main;

import Database.DatabaseHandler;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class SessionHandler {
    

    public static void loginUser(Request request, Response response, DatabaseHandler db){
        String username = request.queryMap().value("username");
        String password = request.queryMap().value("password");
        try {
            db.getUser(username, password);
        } catch (SQLException e) {
            // if it throws this exception, then the user is not in the database, so we should add them. 
            try {
                db.addNewUser(String.valueOf(username.hashCode()), username, "5000", username, password);
            } catch (SQLException e1) {
                System.out.println("There was a problem adding the user to the database");
            }
        }
        response.cookie("username", username, 3600);
        response.cookie("password", password, 3600);
    }


    public static User getUserFromRequestCookie(Request req, DatabaseHandler db){
        String username = req.cookie("username");
        String password = req.cookie("password");
        try {
            User u = db.getUser(username, password);
            return u;
        } catch (SQLException e) {
            System.out.println("There was a problem getting the logged in User's profile");
            return null;
        } 

    }

    public static void logoutUser(Response response){
        response.removeCookie("username");
        response.removeCookie("password");
    }



}