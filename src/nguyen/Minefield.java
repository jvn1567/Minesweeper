package nguyen;

import java.util.Random;

/**
 *
 * @author John
 */
public final class Minefield {
    
    boolean[][] grid;
    
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
    
    public boolean hasMine(int row, int col) {
        if (row < 0 || col < 0 || row >= grid.length || col >= grid[0].length) {
            return false;
        }
        return grid[row][col];
    }
   
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
