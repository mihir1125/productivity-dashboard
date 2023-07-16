package com.example.dashboard;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.PushbackReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private FlowPane listOfTasks;
    @FXML
    private Button btn_addItem;
    @FXML
    private TextField tf_task;
    @FXML
    private LineChart taskChart;
    @FXML
    private Label noTasksLabel;
    @FXML
    private Button btn_logout;
    @FXML
    private Button btn_reset;
    private String currentUser;

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_addItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!tf_task.getText().trim().isEmpty()) {
                    DBUtils.addTask(currentUser, tf_task.getText().trim());
                    DBUtils.changeScene(actionEvent, "home.fxml", "Welcome", currentUser);
                }
            }
        });
        btn_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent, "login.fxml", "Log in", null);
            }
        });
        btn_reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.resetTaskList(currentUser);
                DBUtils.changeScene(actionEvent, "home.fxml", "Log in", currentUser);
            }
        });
    }
    public void setUserInfo(String username) {
        welcomeText.setText("Welcome " + username + "!");
        List<Task> tasks;
        tasks = DBUtils.getTasks(currentUser);
        System.out.println(tasks.toString());
        for(Task task : tasks) {
            CheckBox c = new CheckBox(task.taskName);
            c.setStyle("-fx-font-family: Cambria; -fx-font-size: 14px; -fx-id: " + task.taskID + ";");
            c.setId(Integer.toString(task.taskID));
            System.out.println("Created CheckBox with ID: " + task.taskID);
            c.setOnAction(this::handleRemoveTask);
            if(task.taskCounter == 10)
                c.setSelected(true);
            listOfTasks.getChildren().add(c);
        }
        if(tasks.size() == 0) {
            taskChart.setVisible(false);
            noTasksLabel.setVisible(true);
        } else {
            noTasksLabel.setVisible(false);
            taskChart.setVisible(true);
            taskChart.setTitle("Task Completion");
            XYChart.Series<String, Integer> series = new XYChart.Series<String, Integer>();
            series.setName("Tasks");

            for(Task task : tasks) {
                series.getData().add(new XYChart.Data(task.taskName, task.taskCounter*10));
            }

            taskChart.getData().add(series);
        }
    }
    private void handleRemoveTask(ActionEvent actionEvent) {
        String id = ((Control) actionEvent.getSource()).getId();
        System.out.println("Clicked Button with ID: " + id);
        DBUtils.handleDoneTask(currentUser, Integer.parseInt(id));
        DBUtils.changeScene(actionEvent, "home.fxml", "Welcome!", currentUser);
    }
}