package com.example.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root = null;
        if(username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                HomeController homeController = loader.getController();
                homeController.setCurrentUser(username);
                homeController.setUserInfo(username);
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
    public static void signUpUser(ActionEvent event, String username, String password) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUsername = null;
        PreparedStatement psMakeUserTable = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dashboard", "root", "");
            psCheckUsername = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUsername.setString(1, username);
            resultSet = psCheckUsername.executeQuery();
            if(resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username.");
                alert.show();
            } else {
                //Signing up user
                psInsert = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.executeUpdate();
                //Making a table for the user's tasks
                psMakeUserTable = connection.prepareStatement("CREATE TABLE " + username + " (id INT PRIMARY KEY AUTO_INCREMENT, task VARCHAR(100), is_completed INT(2))");
                psMakeUserTable.execute();
                changeScene(event, "home.fxml", "Welcome!", username);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void loginUser(ActionEvent event, String username, String password) {
        Connection connection = null;
        PreparedStatement psCheckMatch = null;
        PreparedStatement psGetTasks = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dashboard", "root", "");
            psCheckMatch = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            psCheckMatch.setString(1, username);
            psCheckMatch.setString(2, password);
            resultSet = psCheckMatch.executeQuery();
            if(!resultSet.isBeforeFirst()) {
                System.out.println("Incorrect credentials");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Incorrect credentials");
                alert.show();
            } else {
                DBUtils.changeScene(event, "home.fxml", "Welcome!", username);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addTask(String user, String taskName) {
        Connection connection = null;
        PreparedStatement psAddTask = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dashboard", "root", "");
            psAddTask = connection.prepareStatement("INSERT INTO " + user + " (`task`, `is_completed`) VALUES (?, '0')");
            psAddTask.setString(1, taskName);
            psAddTask.executeUpdate();

        } catch(SQLException e) {
            e.printStackTrace();
        }

        //Query to insert rows: INSERT INTO `test_user_table` (`id`, `task`, `is_completed`) VALUES (NULL, 'some_Task', '0');
    }

    public static List<Task> getTasks(String user) {
        Connection connection = null;
        PreparedStatement psGetTasks = null;
        ResultSet resultSet = null;
        List<Task> list = new ArrayList<Task>();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dashboard", "root", "");
            psGetTasks = connection.prepareStatement("SELECT task, id, is_completed FROM " + user + " WHERE `is_completed` < 11");
            resultSet = psGetTasks.executeQuery();
            while(resultSet.next()) {
                String taskName = resultSet.getString("task");
                int taskID = resultSet.getInt("id");
                int taskCounter = resultSet.getInt("is_completed");
                Task task = new Task(taskName, taskID, taskCounter);
                list.add(task);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public static void handleDoneTask(String user, int id) {
        Connection connection = null;
        PreparedStatement psGetCount = null;
        PreparedStatement psTaskComplete = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dashboard", "root", "");
            psGetCount = connection.prepareStatement("SELECT is_completed FROM " + user +" WHERE id = " + id);
            resultSet = psGetCount.executeQuery();
            resultSet.next();
            int counter = resultSet.getInt("is_completed");
            if(counter != 10) {
                psTaskComplete = connection.prepareStatement("UPDATE " + user + " SET is_completed = " + ++counter + " WHERE id = " + id);
                psTaskComplete.executeUpdate();
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public static void resetTaskList(String user) {
        Connection connection = null;
        PreparedStatement psResetTasks = null;
        PreparedStatement psresetAutoIncrement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dashboard", "root", "");
            psResetTasks = connection.prepareStatement("TRUNCATE TABLE " + user);
            psresetAutoIncrement = connection.prepareStatement("ALTER TABLE " + user + " AUTO_INCREMENT = 1");
            psResetTasks.executeUpdate();
            psresetAutoIncrement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}