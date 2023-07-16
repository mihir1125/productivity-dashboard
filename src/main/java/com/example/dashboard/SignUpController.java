package com.example.dashboard;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    private Button signup_button;
    @FXML
    private Button login_button;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signup_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!username.getText().trim().isEmpty() && !password.getText().isEmpty()) {
                    DBUtils.signUpUser(actionEvent, username.getText().trim(), password.getText());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please fill all fields");
                    alert.show();
                }
            }
        });

        login_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent, "login.fxml", "Login!", null);
            }
        });

    }
}