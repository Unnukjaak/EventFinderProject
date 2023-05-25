package org.example;
import java.util.Locale;
import java.util.Scanner;
import java.sql.*;

public class Database{
    private final String dbURL = "jdbc:mysql://localhost:3306/events";
    private final String user = "root";
    private final String password = "Kapsas123";


    public int createUser(String userName, String pswd, String fullName, String email){
        try (Connection conn = DriverManager.getConnection(dbURL, user, password)) {

            String sql = "INSERT INTO users (username, password, fullname, email) VALUES (?,?,?,?);";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, pswd);
            preparedStatement.setString(3, fullName);
            preparedStatement.setString(4, email);
            preparedStatement.executeUpdate();


            //to get the ID of the current user
            String sqlID = "SELECT * FROM users WHERE username ='" + userName + "' and password ='" + pswd + "'";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlID);


            if (resultSet.next()) {
                return resultSet.getInt(1);//returns current users ID Nr.
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  0;
    }

    public int checkUserName(String userName){

        try (Connection conn = DriverManager.getConnection(dbURL, user, password)) {
            String sqlUser = "SELECT * FROM users WHERE username ='" + userName + "'";

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlUser);

            if (resultSet.next()) {

                //returns Users ID Nr.
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int checkLogin(String userName, String pswd){

        try (Connection conn = DriverManager.getConnection(dbURL, user, password)) {
            String sql = "SELECT * FROM users WHERE username ='" + userName + "' and password ='" + pswd + "'";

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                //returns current Users ID Nr.
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
