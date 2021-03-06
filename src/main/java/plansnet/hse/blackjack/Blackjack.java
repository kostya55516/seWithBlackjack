package plansnet.hse.blackjack;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import plansnet.hse.blackjack.Model.Card;
import plansnet.hse.blackjack.Model.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Blackjack extends Application {
    private static final String pathToUI = "/plansnet/hse/blackjack/";

    @FXML
    private Button serverButton;
    @FXML
    private Button clientButton;

    @FXML
    private TextField otherIp;
    @FXML
    private Label myIp;

    @FXML
    private Label selfScore;
    @FXML
    private Label otherScore;

    @FXML
    private Label selfDeck;
    @FXML
    private Label otherDeck;

    private Game game = new Game();

    enum Turn {
        GET,
        PASS
    }

    enum Result {
        SERVER_WON,
        CLIENT_WON,
        IN_PROGRESS
    }

    static private class State {
        public Result result;
        public ArrayList<Card> clientHand;
        public ArrayList<Card> serverHand;
    }

    private Player player;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = openScene();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Scene openScene() throws Exception {
        Parent root = FXMLLoader
                .load(getClass().getResource(pathToUI + "Blackjack.fxml"));
        return new Scene(root);
    }

    @FXML
    private void initialize() {
        updateScene();
    }

    private void updateScene() {
        selfScore.setText("Self " + scores(game.getSelfHand()));
        otherScore.setText("Other " + scores(game.getOtherHand()));

        selfDeck.setText(deck(game.getSelfHand()));
        otherDeck.setText(deck(game.getOtherHand()));
    }

    private String scores(List<Card> hand) {
        return "score: " + Game.CardEvaluator.getHandScore(hand);
    }

    private String deck(List<Card> hand) {
        return "Deck: " + String.join(" ", hand.stream().map(Card::toString).collect(Collectors.toList()));
    }

    private void endGame(int result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(
                result == 1 ? "You won!" : "Other won!"
        );
        alert.showAndWait();
        restartGame();
    }

    private void restartGame() {
        game = new Game();
        updateScene();
    }

    @FXML
    private void get() {
        if (player != null && player.isMyTurn()) {
            player.makeTurn(Turn.GET);
            return;
        }
        game.getCard();
        updateScene();
        if (game.isOver()) {
            endGame(game.getWinner() ? 2 : 1);
        }
    }

    @FXML
    private void pass() {
        if (player != null && player.isMyTurn()) {
            player.makeTurn(Turn.PASS);
            return;
        }
        game.pass();
        if (game.isOver()) {
            endGame(game.getWinner() ? 2 : 1);
        }
    }

    interface Player {
        boolean isMyTurn();
        void makeTurn(Turn turn);
    }

    @FXML
    private void connect() throws IOException {
        serverButton.setDisable(true);
        clientButton.setDisable(true);
        player = new Player() {

            private Socket socket = new Socket(otherIp.getText(), 30239);

            @Override
            public boolean isMyTurn() {
                return false;
            }

            @Override
            public void makeTurn(Turn turn) {

            }
        };
    }

    @FXML
    private void startServer() throws IOException {
        serverButton.setDisable(true);
        clientButton.setDisable(true);
        player = new Player() {

            private ServerSocket socket = new ServerSocket( 30239);

            @Override
            public boolean isMyTurn() {
                return false;
            }

            @Override
            public void makeTurn(Turn turn) {

            }
        };
    }
}
