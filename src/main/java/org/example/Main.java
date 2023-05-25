package org.example;

import java.sql.*;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;



public class Main {

    static public int currentUserId = 0;
    static String topic;
    static char again = 'y';

    static Database dataBase = new Database();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String dbURL = "jdbc:mysql://localhost:3306/events";
        String username = "root";
        String password = "Kapsas123";
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {

            boolean loggedIn = false;
            boolean exit = false;

            do {
                if (!loggedIn) {
                    displayMainMenu();
                }

                char action = scanner.nextLine().charAt(0);

                if (loggedIn) {
                    switch (action) {
                        case 'a':
                            System.out.println("Enter date of the event (yyyy-mm-dd)");
                            String newEventDate = scanner.nextLine();

                            System.out.println("Enter name of the event");
                            String newEventName = scanner.nextLine();

                            System.out.println("Enter location of the event (Tallinn, Tartu, Else)");
                            String newLocation = scanner.nextLine();

                            System.out.println("Enter genre of the event (Concert, Theater, Cinema, Else)");
                            String newGenre = scanner.nextLine();

                            System.out.println("Enter ticket price for the event");
                            String newTicket = scanner.nextLine();

                            System.out.println("Enter type of the event (Adult, Family, Children)");
                            String newTypeOfEvent = scanner.nextLine();

                            insertData(conn, newEventDate, newEventName, newLocation, newGenre, newTicket, newTypeOfEvent);
                            break;

                        case 'd':
                            System.out.println("Enter name of the event you want to delete");
                            String deleteEvent = scanner.nextLine();
                            deleteData(conn, deleteEvent);
                            break;
                        case 'b':
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } else {
                switch (action) {
                    case 'd':
                        System.out.println("Enter the date (yyyy-mm-dd) of the events you want to search");
                        String searchEventDate = scanner.nextLine();
                        searchDataByDate(conn, searchEventDate);
                        break;
                    case 's':
                        System.out.println("Enter the location (Tallinn, Tartu, Else) of the events you want to search");
                        String searchEventLocation = scanner.nextLine();
                        searchDataByLocation(conn, searchEventLocation);
                        break;
                    case 'g':
                        System.out.println("Enter the genre (Concert, Theater, Cinema, Else) you want");
                        String searchGenre = scanner.nextLine();
                        searchDataByGenre(conn, searchGenre);
                        break;
                    case 'p':
                        System.out.println("Enter the min price: ");
                        double minPrice = scanner.nextDouble();
                        System.out.println("Enter the max price: ");
                        double maxPrice = scanner.nextDouble();
                        searchDataByPrice(conn, minPrice, maxPrice);
                        break;
                    case 't':
                        System.out.println("Enter the event type (Adult, Family, Children) you want to search");
                        String searchType = scanner.nextLine();
                        searchDataByType(conn, searchType);
                        break;
                    case 'r':
                        readData(conn);
                        break;
                    case 'l':
                        login();
                        loggedIn = true;
                        break;
                    case 'c':
                        createUser();
                        break;
                    case 'b':
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
                }

                if (exit) {
                    break;
                }
            } while (true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void displayMainMenu() {
        System.out.println("Choose an option (d,s,g,p,t,r,l,c,b)");
        System.out.println("d - search by date (yyyy-mm-dd)");
        System.out.println("s - search by location (Tallinn, Tartu, Else)");
        System.out.println("g - search by genre (Concert, Theater, Cinema, Else)");
        System.out.println("p - search by price range");
        System.out.println("t - search by type of event (Adult, Family, Children)");
        System.out.println("r - get all events");
        System.out.println("l - login");
        System.out.println("c - create an account");
    }

    public static void login () {
        Userinfo currentUser = new Userinfo();

        System.out.println("Enter username");
        currentUser.setUsername(scanner.nextLine());

        System.out.println("Enter password");
        currentUser.setPswd(scanner.nextLine());

        //DataBase class method for login check returns current Users ID Nr.
        int userId = dataBase.checkLogin(currentUser.getUsername(), currentUser.getPswd());
        if (userId > 0) {
            System.out.println("You have logged in successfully!");
            System.out.println("Choose an option (a,d,b)");
            System.out.println("d - delete event");
            System.out.println("a - add event");
            System.out.println("b - back to main menu");
            currentUserId = userId;
        } else {
            System.out.println("Incorrect login or no account!");
            System.out.println("Choose an option (d,s,g,p,t,r,l,c,b)");
        }
    }

    public static void createUser () {
        Userinfo newUser = new Userinfo();
        System.out.println("Enter username");
        newUser.setUsername(scanner.nextLine());

        //to create a method that we can use for others as well
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{3,20}$");
        Matcher matcher = pattern.matcher(newUser.getUsername());
        while (matcher.matches() == false) {
            System.out.println("Please enter a valid username! It should be at least 3 characters!");
            newUser.setUsername(scanner.nextLine());
            pattern = Pattern.compile("^[a-zA-Z0-9]{3,20}$");
            matcher = pattern.matcher(newUser.getUsername());
        }

        //DataBase class method for username check returns 1 if true, 0 if false
        int userId = dataBase.checkUserName(newUser.getUsername());

        while (userId > 0) {
            System.out.println("Username already exists! Try another one!");
            newUser.setUsername(scanner.nextLine());
            pattern = Pattern.compile("^[a-zA-Z0-9+_.!-]]{3,20}$");
            matcher = pattern.matcher(newUser.getUsername());
            while (matcher.matches() == false) {

                System.out.println("Please enter a valid username! It should be at least 3 characters!");
                newUser.setUsername(scanner.nextLine());
                pattern = Pattern.compile("^[a-zA-Z0-9+_.!-]]{3,20}$");
                matcher = pattern.matcher(newUser.getUsername());
            }
            userId = dataBase.checkUserName(newUser.getUsername());

        }

        System.out.println("Enter password");
        newUser.setPswd(scanner.nextLine());

        pattern = Pattern.compile("^[a-zA-Z0-9+_.!-]{3,20}$");
        matcher = pattern.matcher(newUser.getPswd());
        while (matcher.matches() == false) {
            System.out.println("Please enter a valid password! It should be at least 3 characters!");
            newUser.setPswd(scanner.nextLine());
            pattern = Pattern.compile("^[a-zA-Z0-9+_.!-]{3,20}$");
            matcher = pattern.matcher(newUser.getPswd());
        }

        System.out.println("Enter full name");
        newUser.setFullName(scanner.nextLine());

        pattern = Pattern.compile("^([A-Za-z]*((\\s)))+[A-Za-z]*$");
        matcher = pattern.matcher(newUser.getFullName());
        while (matcher.matches() == false) {
            System.out.println("Please enter valid first name and last name!");
            newUser.setFullName(scanner.nextLine());
            pattern = Pattern.compile("^([A-Za-z]*((\\s)))+[A-Za-z]*$");
            matcher = pattern.matcher(newUser.getFullName());
        }

        System.out.println("Enter email");
        newUser.setEmail(scanner.nextLine());
        pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        matcher = pattern.matcher(newUser.getEmail());
        while (matcher.matches() == false) {
            System.out.println("Please enter valid e-mail!");
            System.out.println("Enter email");
            newUser.setEmail(scanner.nextLine());
            pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
            matcher = pattern.matcher(newUser.getEmail());
        }

        //CurrentUserID returns new userID Nr.
        currentUserId = dataBase.createUser(newUser.getUsername(), newUser.getPswd(), newUser.getFullName(), newUser.getEmail());

        if (currentUserId > 0) {
            System.out.println("You have created an account successfully!");
        }
    }
    private static void searchDataByDate (Connection conn, String searchEventDate) throws SQLException {

                String sql = "SELECT * FROM events WHERE eventdate LIKE ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, "%" + searchEventDate + "%");

                ResultSet resultSet = statement.executeQuery();

                int count = 0;

                while (resultSet.next()) {
                    String eventdate = resultSet.getString(1);
                    String eventname = resultSet.getString(2);
                    String location = resultSet.getString(3);
                    String genre = resultSet.getString(4);
                    String ticket = resultSet.getString(5);
                    String typeofevent = resultSet.getString(6);

                    String output = "Event #%d: %s - %s - %s - %s - %s - %s";
                    System.out.println(String.format(output, ++count, eventdate, eventname, location, genre, ticket, typeofevent));
                }

            }

            private static void searchDataByLocation (Connection conn, String searchEventLocation) throws SQLException {
                String sql = "SELECT * FROM events WHERE location LIKE ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, "%" + searchEventLocation + "%");

                ResultSet resultSet = statement.executeQuery();

                int count = 0;

                while (resultSet.next()) {
                    String eventdate = resultSet.getString(1);
                    String eventname = resultSet.getString(2);
                    String location = resultSet.getString(3);
                    String genre = resultSet.getString(4);
                    String ticket = resultSet.getString(5);
                    String typeofevent = resultSet.getString(6);

                    String output = "Event #%d: %s - %s - %s - %s - %s - %s";
                    System.out.println(String.format(output, ++count, eventdate, eventname, location, genre, ticket, typeofevent));
                }

            }


            private static void searchDataByType (Connection conn, String searchType) throws SQLException {
                String sql = "SELECT * FROM events WHERE typeofevent LIKE ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, "%" + searchType + "%");

                ResultSet resultSet = statement.executeQuery();

                int count = 0;

                while (resultSet.next()) {
                    String eventdate = resultSet.getString(1);
                    String eventname = resultSet.getString(2);
                    String location = resultSet.getString(3);
                    String genre = resultSet.getString(4);
                    String ticket = resultSet.getString(5);
                    String typeofevent = resultSet.getString(6);

                    String output = "Event #%d: %s - %s - %s - %s - %s - %s";
                    System.out.println(String.format(output, ++count, eventdate, eventname, location, genre, ticket, typeofevent));
                }

            }

            private static void searchDataByPrice (Connection conn,double minPrice, double maxPrice) throws SQLException
            {
                String sql = "SELECT * FROM events WHERE ticket >= ? AND ticket <= ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setDouble(1, minPrice);
                statement.setDouble(2, maxPrice);
                ResultSet resultSet = statement.executeQuery();
                int count = 0;
                while (resultSet.next()) {
                    String eventdate = resultSet.getString("eventdate");
                    String eventname = resultSet.getString("eventname");
                    String location = resultSet.getString("location");
                    String genre = resultSet.getString("genre");
                    int ticket = resultSet.getInt("ticket");
                    String typeofevent = resultSet.getString("typeofevent");
                    // Print event details
                    String output = "Event #%d: %s - %s - %s - %s - %d - %s";
                    System.out.println(String.format(output, ++count, eventdate, eventname, location, genre, ticket, typeofevent));
                }
            }

            private static void searchDataByGenre (Connection conn, String searchGenre) throws SQLException {
                String sql = "SELECT * FROM events WHERE genre LIKE ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, "%" + searchGenre + "%");

                ResultSet resultSet = statement.executeQuery();
                int count = 0;

                while (resultSet.next()) {
                    String eventdate = resultSet.getString(1);
                    String eventname = resultSet.getString(2);
                    String location = resultSet.getString(3);
                    String genre = resultSet.getString(4);
                    String ticket = resultSet.getString(5);
                    String typeofevent = resultSet.getString(6);

                    String output = "Event #%d: %s - %s - %s - %s - %s - %s";
                    System.out.println(String.format(output, ++count, eventdate, eventname, location, genre, ticket, typeofevent));
                }

            }

            private static void readData (Connection conn) throws SQLException {

                String sql = "SELECT * FROM events";
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                int count = 0;

                while (resultSet.next()) {
                    String eventdate = resultSet.getString(1);
                    String eventname = resultSet.getString(2);
                    String location = resultSet.getString(3);
                    String genre = resultSet.getString(4);
                    String ticket = resultSet.getString(5);
                    String typeofevent = resultSet.getString(6);


                    String output = "Event #%d: %s - %s - %s - %s - %s- %s";
                    System.out.println(String.format(output, ++count, eventdate, eventname, location, genre, ticket, typeofevent));
                }
            }



            private static void insertData (Connection conn, String eventdate, String eventname, String location, String
            genre, String ticket, String typeofevent)
            throws SQLException {

                String sql = "INSERT INTO events (eventdate, eventname, location, genre, ticket, typeofevent) VALUES (?,?,?,?,?,?)";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, eventdate);
                statement.setString(2, eventname);
                statement.setString(3, location);
                statement.setString(4, genre);
                statement.setString(5, ticket);
                statement.setString(6, typeofevent);

                int rowInserted = statement.executeUpdate();

                if (rowInserted >= 1) {
                    System.out.println("A new event is added");
                } else if (rowInserted >= 2) {
                    System.out.println("A new event name is added");
                } else if (rowInserted >= 3) {
                    System.out.println("A new location is added");
                } else if (rowInserted >= 4) {
                    System.out.println("A new genre is added");
                } else if (rowInserted >= 5) {
                    System.out.println("A new ticket price is added");
                } else if (rowInserted >= 6) {
                    System.out.println("A new type of event is added");
                } else {
                    System.out.println("Invalid input!");
                }
            }

            private static void deleteData (Connection conn, String eventname) throws SQLException {

                String sql = "DELETE FROM events WHERE eventname = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, eventname);

                if (statement.executeUpdate() > 0) {
                    System.out.println("Event was deleted successfully");
                } else {
                    System.out.println("Something went wrong");
                }
            }
        }