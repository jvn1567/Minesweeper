package nguyen;

import java.util.Random;

/**
 * This class contains a 2D array of boolean values representing whether the
 * location on the game grid contains a mine or not.
 *
 * @author John
 */
public final class Minefield {

    boolean[][] grid;

    /**
     * Constructs a Minefield with the passed in number of rows and columns. The
     * passed in number of mines are placed randomly in the grid.
     *
     * @param rows number of rows to create
     * @param cols number of columns to create
     * @param mines number of mines (true) to place
     */
    public Minefield(int rows, int cols, int mines) {
        Random rand = new Random();
        grid = new boolean[rows][cols];
        while (mines > 0) {
            int row = rand.nextInt(rows);
            int col = rand.nextInt(cols);
            if (!grid[row][col]) {
                grid[row][col] = true;
                mines--;
            }
        }
    }

    /**
     * Returns whether the location has a mine
     *
     * @param row the row to check
     * @param col the column to check
     * @return true if a mine is there (returns the boolean at array location)
     */
    public boolean hasMine(int row, int col) {
        if (row < 0 || col < 0 || row >= grid.length || col >= grid[0].length) {
            return false;
        }
        return grid[row][col];
    }

    /**
     * Returns the number of mines in the 8 surrounding tiles of the passed
     * location.
     *
     * @param row row of the location to check
     * @param col column of the location to check
     * @return the number of mines in surrounding tiles
     */
    public int getNeighbors(int row, int col) {
        //handle off-grid locations
        if (row < 0 || col < 0 || row > grid.length || col > grid[0].length) {
            return -1;
        }
        //counts mines in 3x3 grid around passed in location
        int neighbors = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (hasMine(row + i, col + j)) {
                    neighbors++;
                }
            }
        }
        //removes center mine if there is one
        if (hasMine(row, col)) {
            neighbors--;
        }
        return neighbors;
    }

}
