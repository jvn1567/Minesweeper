/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nguyen;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author John
 */
public class Minesweeper extends Application {

    //global constants
    private static int MIN_SIZE = 5;
    private static int MAX_SIZE = 40;
    //globals for main window
    private BorderPane parentPane;
    private Scene scene;
    //globals variables for starting menu
    private VBox inputPane;
    private Label[] lblInputs;
    private TextField[] txtInputs;
    private Button btnStart;
    private Label lblStartError;
    //globals variables for game
    private BorderPane gamePane;
    private Button btnRestart;
    private Label lblEndGame;
    private Label[] stats;
    private Button[][] grid;
    private Minefield minefield;
    private int cleared = 0;
    private int safeTiles = 0;

    @Override
    public void start(Stage primaryStage) {
        //starting menu pane
        inputPane = createInputPane();
        inputPane.setAlignment(Pos.CENTER);
        //parent pane
        parentPane = new BorderPane();
        parentPane.setCenter(inputPane);
        //button events
        //scene
        scene = new Scene(parentPane, 300, 250);
        //primary stage
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public VBox createInputPane() {
        //input labels and text fields
        inputPane = new VBox();
        lblInputs = new Label[3];
        txtInputs = new TextField[3];
        lblStartError = new Label();
        for (int i = 0; i < 3; i++) {
            FlowPane line = new FlowPane();
            VBox.setMargin(line, new Insets(0, 0, 10, 0));
            line.setAlignment(Pos.CENTER);
            lblInputs[i] = new Label();
            txtInputs[i] = new TextField();
            line.getChildren().add(lblInputs[i]);
            line.getChildren().add(txtInputs[i]);
            inputPane.getChildren().add(line);
        }
        lblInputs[0].setText("Height: ");
        lblInputs[1].setText("Width:  ");
        lblInputs[2].setText("Mines:  ");
        //start button and error label
        btnStart = new Button("Start Game");
        btnStart.setOnAction(e -> checkInput());
        inputPane.getChildren().add(btnStart);
        VBox.setMargin(btnStart, new Insets(0, 0, 10, 0));
        inputPane.getChildren().add(lblStartError);
        return inputPane;
    }

    public void checkInput() {
        try {
            int rows = Integer.parseInt(txtInputs[0].getText());
            int cols = Integer.parseInt(txtInputs[1].getText());
            int mines = Integer.parseInt(txtInputs[2].getText());
            if (rows * cols <= mines) {
                lblStartError.setText("Too many mines.");
            } else if (rows > MAX_SIZE || cols > MAX_SIZE) {
                lblStartError.setText("Please enter height/width smaller than "
                        + MAX_SIZE + ".");
            } else if (rows < MIN_SIZE || cols < MIN_SIZE) {
                lblStartError.setText("Please enter height/width larger than "
                        + MIN_SIZE + ".");
            } else {
                gamePane = new BorderPane();
                minefield = new Minefield(rows, cols, mines);
                setGameArea(rows, cols, mines);
                setGameStats(rows, cols, mines);
            }
        } catch (NumberFormatException ex) {
            lblStartError.setText("Please enter valid integer values.");
        }
    }

    public void setGameArea(int rows, int cols, int mines) {
        //main game area grid and button events
        GridPane boxes = new GridPane();
        boxes.setAlignment(Pos.CENTER);
        grid = new Button[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button box = new Button();
                box.setMinSize(25, 25);
                box.setMaxSize(25, 25);
                final int frow = row;
                final int fcol = col;
                box.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        handleClick(frow, fcol);
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        setFlag(frow, fcol);
                    }
                });
                boxes.add(box, row, col);
                grid[row][col] = box;
            }
        }
        boxes.setBackground(new Background(new BackgroundFill(
                Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        gamePane.setCenter(boxes);
        parentPane.setCenter(gamePane);
    }

    public void setGameStats(int rows, int cols, int mines) {
        //main game menu stats
        VBox menuPane = new VBox();
        menuPane.setAlignment(Pos.CENTER);
        lblEndGame = new Label();
        menuPane.getChildren().add(lblEndGame);
        VBox.setMargin(lblEndGame, new Insets(0, 50, 10, 50));
        stats = new Label[3];
        for (int i = 0; i < stats.length; i++) {
            stats[i] = new Label();
            menuPane.getChildren().add(stats[i]);
            VBox.setMargin(stats[i], new Insets(0, 50, 10, 50));
        }
        cleared = 0;
        safeTiles = rows * cols - mines;
        stats[0].setText("Mines: " + mines);
        stats[1].setText("Tiles cleared: 0");
        stats[2].setText("Safe Tiles: " + safeTiles);
        //main game menu button
        btnRestart = new Button("New Game");
        btnRestart.setOnAction(e -> {
            parentPane.setCenter(inputPane);
            parentPane.setRight(null);
        });
        menuPane.getChildren().add(btnRestart);
        VBox.setMargin(btnRestart, new Insets(0, 50, 10, 50));
        parentPane.setRight(menuPane);
    }

    public void handleClick(int row, int col) {
        int neighbors = minefield.getNeighbors(row, col);
        if (minefield.hasMine(row, col)) {
            setMine(row, col);
            grid[row][col].setDisable(true);
            endGame();
        } else if (neighbors == 0) {
            cleared++;
            clearArea(row, col);
        } else {
            placeNumber(row, col);
        }
        stats[1].setText("Tiles cleared: " + cleared);
        if (cleared == safeTiles) {
            endGame();
        }
    }
    
    public void clearArea(int row, int col) {
        grid[row][col].setDisable(true);
        //adjacent
        clearLocation(row - 1, col);
        clearLocation(row, col + 1);
        clearLocation(row + 1, col);
        clearLocation(row, col - 1);
        //corners
        clearLocation(row + 1, col + 1);
        clearLocation(row + 1, col - 1);
        clearLocation(row - 1, col - 1);
        clearLocation(row - 1, col + 1);
    }
    
    public void clearLocation(int row, int col) {
        if (minefield.getNeighbors(row, col) == 0 && !isDisabled(row, col)) {
            placeNumber(row, col);
            clearArea(row, col);
        } else if (minefield.getNeighbors(row, col) > 0 && !isDisabled(row, col)) {
            placeNumber(row, col);
        }
    }
    
    public void placeNumber(int row, int col) {
        grid[row][col].setDisable(true);
        cleared++;
        grid[row][col].setGraphic(null);
        if (minefield.getNeighbors(row, col) != 0) {
            grid[row][col].setText("" + minefield.getNeighbors(row, col));
        }
    }
    
    //out of bounds considered already disabled
    public boolean isDisabled(int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) {
            return true;
        } else {
            return grid[row][col].isDisabled();
        }
    }
    
    public void setFlag(int row, int col) {
        Image png = new Image(getClass().getResourceAsStream(
                "/images/flag.png"));
        ImageView flag = new ImageView(png);
        if (grid[row][col].getGraphic() != null) {
            grid[row][col].setGraphic(null);
        } else {
            grid[row][col].setGraphic(flag);
        }
    }
    
    public void setMine(int row, int col) {
        Image png = new Image(getClass().getResourceAsStream(
                "/images/mine.png"));
        ImageView mine = new ImageView(png);
        grid[row][col].setGraphic(mine);
    }
    
    public void endGame() {
        if (cleared == safeTiles) {
            lblEndGame.setText("YOU WIN!");
            revealMines();
        } else {
            lblEndGame.setText("GAME OVER!");
            revealMines();
        }
    }
    
    public void revealMines() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                //seems cheap but works
                grid[row][col].setOnMouseClicked(e -> System.out.print(""));
                if (minefield.hasMine(row, col)) {
                    setMine(row, col);
                }
            }
        }
    }
}
