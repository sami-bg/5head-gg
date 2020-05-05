package Main;

import Database.DatabaseHandler;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class SessionHandler {
    

    public static Boolean loginUser(Request request, Response response, DatabaseHandler db){
        Boolean successfulLogin = false;
        String username = request.queryMap().value("username");
        String password = request.queryMap().value("password");
        User user = null;
        try {
            user = db.getUser(String.valueOf(username.hashCode()));
        } catch (SQLException throwables) {
            System.out.println("threw sql error here");
            throwables.printStackTrace();
        }
        if(user == null) {
                try {
                    System.out.println("this branch");
                    db.addNewUser(String.valueOf(username.hashCode()), username, "5000", username, password);
                    response.cookie("username", username, 3600);
                    response.cookie("password", password, 3600);
                    successfulLogin = true;
                } catch (SQLException e1) {
                    System.out.println("There was a problem adding the user to the database");
                    successfulLogin = false;
                }
                return successfulLogin;
            }

         if(user.getAuth().equals(password)){
             System.out.println("this other branch");

             response.cookie("username", username, 3600);
                response.cookie("password", password, 3600);
                successfulLogin = true;
            } else {
                successfulLogin = false;
            }

        return successfulLogin;
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

    public static boolean isUserLoggedIn(Request req){
        String username = req.cookie("username");
        String password = req.cookie("password");
        if (username == null || username.equals("") || password == null || password.equals("")) {
            return false;
        }
        return true;
    }



}