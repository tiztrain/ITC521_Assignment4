package chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class Client extends Application {
    // IO streams
    DataOutputStream outputToServer = null;
    DataInputStream inputFromServer = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextArea messages = new TextArea();
        messages.setPrefHeight(300);

        TextField input = new TextField();
        input.setPrefHeight(50);

        VBox vBox = new VBox(50, messages, input);

        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Client Chat Box");
        primaryStage.setScene(new Scene(vBox, 600, 400));
        primaryStage.show();


        new Thread(() -> {
            input.setOnAction(e -> {
                try {


                    String text = input.getText();

                    // send text to the server
                    System.out.println("text is " + text);
                    outputToServer.writeUTF(text);
                    outputToServer.flush();

                    // display to the text area
                    messages.appendText("Client: " + text + '\n');

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            try {
                // create a socket to connect to server
                Socket socket = new Socket("localhost", 8000);
                Platform.runLater(() ->
                        messages.appendText("Client started at " + new Date() + "\n"));


                // create data input and output streams
                inputFromServer = new DataInputStream(socket.getInputStream());
                outputToServer = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                messages.appendText(ex.toString() + '\n');
            }
        }).start();


    }
}

