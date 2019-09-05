

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static java.lang.System.out;

/**
 * Created by Davide on 13/12/2016.
 */
public class SudokuSolver {

    private final int TDIM = 3;
    private final int DIM = TDIM*3;
    private final Character[] LIST_NUMS = new Character[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final static ForkJoinPool fjp = new ForkJoinPool();
    private char[] board = new char [DIM*DIM];

    public SudokuSolver(String strPath) {
        Path path = Paths.get(strPath);
        try {
            readBoard(path, board);
        } catch (IOException e) {
            out.println("Errore nella lettura del file.");
        }
    }

    public char[] readBoard(Path p, char[] board) throws IOException {
        if (!Files.isRegularFile(p) || !p.toString().endsWith(".txt"))
            throw new IllegalArgumentException();
        List<String> lines = Files.readAllLines(p);
        for(int j=0; j<lines.size(); j++) {
            String line = lines.get(j);
            for(int i=0; i<line.length(); i++) {
                char c = line.charAt(i);
                board[j*DIM+i] = c;
            }
        }
        return board;
    }

    public void printBoard(char[] board) {
        for(int j=0; j<DIM; j++) {
            for(int i=0; i<DIM; i++) {
                out.print(board[j*DIM+i]);
            }
            out.println();
        }
    }

    public char getNum(char[] board, int i, int j) {
        return board[j*DIM +i];
    }

    public char[] getBoard() {
        return board;
    }

    public int emptyCells(char[] board) {
        int n = 0;
        for(int i=0; i<DIM*DIM; i++) {
            if(board[i] == '.')
                n++;
        }
        return n;
    }

    /*public BigInteger searchSpace(char[] board) {
        BigInteger n = BigInteger.valueOf(1);
        for(int i=0; i<DIM*DIM; i++) {
            if(board[i] == '.') {
                int p = 0;
                for(char c : LIST_NUMS) {
                    if(isAValidNumber(board, i%DIM, i/DIM, c)) {
                        p++;
                    }
                }
                n = n.multiply(BigInteger.valueOf(p));
            }
        }
        return n;
    }*/

    private boolean isAValidNumber (char[] m, int i, int j, char c) {
        int xoff = (i/TDIM)*TDIM;
        int yoff = (j/TDIM)*TDIM;
        for (int r = 0; r < 9; r++) {
            if(getNum(m, r, j) == c) return false;
            if(getNum(m, i, r) == c) return false;
            if(getNum(m, xoff + (r%3), yoff + (r/3)) == c) return false;
        }
        return true;
    }

    private boolean isComplete(char[] board) {
        for (int k = 0; k < DIM*DIM; k++)
            if (board[k] == '.')
                return false;
        return true;
    }

    public int sequential(char[] board, int k) {
        int sol = 0;
        if (isComplete(board))
            return 1;
        if (k == DIM*DIM)
            return 0;
        while (board[k] != '.') {
            k++;
            if (isComplete(board))
                return 1;
            if (k == DIM*DIM)
                return 0;
        }
        for (int n = 0; n < 9; n++) {
            if(board[k] == '.') {
                if (isAValidNumber(board, k%DIM, k/DIM, LIST_NUMS[n])) {
                    board[k] = LIST_NUMS[n];
                    sol += sequential(board, k + 1);
                }
            }
            board[k] = '.';
        }
        return sol;
    }

    public int parallel(char[] board, int cutoff) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        return fjp.invoke(new ParallelSolver(board, 0, cutoff, cores));
    }

}
