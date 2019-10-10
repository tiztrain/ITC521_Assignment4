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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server extends Application {
    // IO streams
    DataOutputStream outputToClient = null;
    DataInputStream inputFromClient = null;

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
        primaryStage.setTitle("Server Chat Box");
        primaryStage.setScene(new Scene(vBox, 600, 400));
        primaryStage.show();

        new Thread(() -> {
            try {
                // create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(() ->
                        messages.appendText("Server started at " + new Date() + "\n"));

                // listen for a connection request
                Socket socket = serverSocket.accept();
                Platform.runLater(() ->
                        messages.appendText("Server connected with client\n"));

                // create data input and output streams
                inputFromClient = new DataInputStream(socket.getInputStream());
                outputToClient = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    // used to read message from Client
                    String str = inputFromClient.readUTF();
                    Platform.runLater(() ->
                            messages.appendText("Client: " + str + '\n'));
                    System.out.println("Client: " + str);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

        // used to write message to client
        new Thread(() -> {
            // when enter is pressed
            input.setOnAction(e -> {
                try {
                    // assign the text in the input text box to text
                    String text = input.getText();

                    // send text to the server

                    System.out.println("text is " + text);
                    outputToClient.writeUTF(text);
                    outputToClient.flush();

                    // display to the text area
                    messages.appendText("Server: " + text + '\n');

                    // turn the text field blank
                    input.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }).start();
    }
}