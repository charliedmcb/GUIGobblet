package gobblet;

import gobblet.game_state.Board;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Gobblet extends Application {

    private Board board;
    private Button[][] boardButtons;
    private Button[][] playerButtons;
    private Text status;

    private int currentPlayer = 0;
    private boolean startedPlaceAction = false;
    private boolean startedMoveAction = false;
    private boolean gameOver = false;
    private int actionParameterOne;
    private int actionParameterTwo;

    private synchronized void startMoveAction(int x, int y) {
        if (board.getPlayerForBoardLocation(x, y) != currentPlayer) {
            status.setText("Must select your own piece");
            return;
        }
        actionParameterOne = x;
        actionParameterTwo = y;
        startedPlaceAction = false;
        startedMoveAction = true;
    }

    private synchronized void startPlaceAction(int player, int stack) {
        if (player != currentPlayer) {
            status.setText("Must select your own piece");
            return;
        }
        if (board.getSizeForPlayerStack(player, stack) == -1) {
            status.setText("Must select a stack that contains a piece");
            return;
        }
        actionParameterOne = player;
        actionParameterTwo = stack;
        startedMoveAction = false;
        startedPlaceAction = true;
    }

    private synchronized void finishAction(int x, int y) {
        if (startedMoveAction) {
            if (board.getSizeForBoardLocation(actionParameterOne, actionParameterTwo)
                    <= board.getSizeForBoardLocation(x, y)) {
                status.setText("Must cover a smaller piece");
                return;
            }
            board.movePiece(actionParameterOne, actionParameterTwo, x, y);
            boardButtons[actionParameterOne][actionParameterTwo]
                    .setText(board.getTextForBoardLocation(actionParameterOne, actionParameterTwo));
            switch (board.getPlayerForBoardLocation(actionParameterOne, actionParameterTwo)) {
                case -1:
                    boardButtons[actionParameterOne][actionParameterTwo].setStyle(
                            "-fx-background-color: LightGray; -fx-text-fill: Gray;");
                    break;
                case 0:
                    boardButtons[actionParameterOne][actionParameterTwo].setStyle(
                            "-fx-background-color: White; -fx-text-fill: Black;");
                    break;
                case 1:
                    boardButtons[actionParameterOne][actionParameterTwo].setStyle(
                            "-fx-background-color: Black; -fx-text-fill: White;");
                    break;
            }
        } else {
            if (board.getSizeForPlayerStack(actionParameterOne, actionParameterTwo)
                    <= board.getSizeForBoardLocation(x, y)) {
                status.setText("Must cover a smaller piece");
                return;
            }
            board.placePiece(actionParameterOne, actionParameterTwo, x, y);
            playerButtons[actionParameterOne][actionParameterTwo]
                    .setText(board.getTextForPlayerStack(actionParameterOne, actionParameterTwo));
        }
        boardButtons[x][y].setText(board.getTextForBoardLocation(x, y));
        boardButtons[x][y].setStyle(board.getPlayerForBoardLocation(x, y) == 0 ?
                "-fx-background-color: White; -fx-text-fill: Black;"
                : "-fx-background-color: Black; -fx-text-fill: White;");
        currentPlayer = currentPlayer == 0 ? 1 : 0;
        switch (board.victory()) {
            case 0:
                status.setText("White Wins!");
                gameOver = true;
                break;
            case 1:
                status.setText("Black Wins!");
                gameOver = true;
                break;
            case 2:
                status.setText("The Game is a Tie!");
                gameOver = true;
                break;
            default:
                status.setText(currentPlayer == 0 ? "White's Turn!" : "Black's Turn!");
        }
        startedPlaceAction = false;
        startedMoveAction = false;
    }

    private Button createBoardButton(final int x, final int y) {
        Button btn = new Button();
        btn.setText(board.getTextForBoardLocation(x, y));
        btn.setStyle("-fx-background-color: LightGray; -fx-text-fill: Gray;");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!gameOver) {
                    if (startedPlaceAction || startedMoveAction) {
                        finishAction(x, y);
                    } else {
                        startMoveAction(x, y);
                    }
                }
            }
        });

        btn.setLayoutX(20 + (75 * x));
        btn.setLayoutY(20 + (30 * y));
        return btn;
    }

    private Button createPlayerButton(final int player, final int stack) {
        Button btn = new Button();
        btn.setText(board.getTextForPlayerStack(player, stack));
        btn.setStyle(player == 0 ? "-fx-background-color: White; -fx-text-fill: Black;"
                : "-fx-background-color: Black; -fx-text-fill: White;");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!gameOver) {
                    startPlaceAction(player, stack);
                }
            }
        });

        btn.setLayoutX(20 + (75 * (stack + 1)));
        btn.setLayoutY(20 + (30 * (player + 4)));
        return btn;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        board = Board.getInstance();
        boardButtons = new Button[4][4];
        playerButtons = new Button[2][3];
        primaryStage.setTitle("Gobblet!");

        Pane root = new Pane();

        status = new Text("White's Turn!");
        status.setLayoutX(20);
        status.setLayoutY(230);
        root.getChildren().add(status);

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Button btn = createBoardButton(x, y);
                root.getChildren().add(btn);
                boardButtons[x][y] = btn;
            }
        }

//        Text white = new Text("White");
//        white.setLayoutX(20);
//        white.setLayoutY(20 + (30 * 4));
//        root.getChildren().add(white);
//
//        Text black = new Text("Black");
//        black.setLayoutX(20);
//        black.setLayoutY(20 + (30 * 5));
//        root.getChildren().add(black);

        for (int player = 0; player < 2; player++) {
            for (int stack = 0; stack < 3; stack++) {
                Button btn = createPlayerButton(player, stack);
                root.getChildren().add(btn);
                playerButtons[player][stack] = btn;
            }
        }

        primaryStage.setScene(new Scene(root, 330, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
