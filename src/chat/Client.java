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
        messages.setEditable(false);

        TextField input = new TextField();
        input.setPrefHeight(50);

        VBox vBox = new VBox(50, messages, input);

        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Client Chat Box");
        primaryStage.setScene(new Scene(vBox, 600, 400));
        primaryStage.show();

        // used to write message to server
        new Thread(() -> {
            // when enter is pressed
            input.setOnAction(e -> {
                try {
                    // assign the text in the input text box to text
                    String text = input.getText();

                    // send text to the server
                    System.out.println("text is " + text);
                    outputToServer.writeUTF(text);
                    outputToServer.flush();

                    // display to the text area
                    messages.appendText("Client: " + text + '\n');

                    // turn the text field blank
                    input.setText("");

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

                while (true) {
                    // used to read message from Client
                    String str = inputFromServer.readUTF();
                    Platform.runLater(() ->
                            messages.appendText("Server: " + str + '\n'));
                    System.out.println("Server: " + str);
                }
            } catch (IOException ex) {
                messages.appendText(ex.toString() + '\n');
            }
        }).start();


    }
}

