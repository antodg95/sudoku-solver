

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by Davide on 14/12/2016.
 */
public class ParallelSolver extends RecursiveTask<Integer>{

    private final int TDIM = 3;
    private final int DIM = TDIM*3;
    private final Character[] LIST_NUMS = new Character[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private char[] board;
    private int k;
    private int cutoff;
    private int cores;

    public ParallelSolver(char[] board, int k, int cutoff, int cores) {
        this.board = board;
        this.k = k;
        this.cutoff = cutoff;
        this.cores = cores;
    }

    private char getNum(char[] board, int i, int j) {
        return board[j*DIM +i];
    }

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

    @Override
    protected Integer compute() {
        int sol = 0;
        if(cutoff < cores*cores) {
            if (isComplete(board))
                return 1;
            if (k == DIM * DIM)
                return 0;
            while (board[k] != '.') {
                k++;
                if (isComplete(board)) {
                    return 1;
                }
                if (k == DIM * DIM)
                    return 0;
            }

            List<Character> vn = new ArrayList<>();
            for (int n = 0; n < DIM; n++) {
                if (isAValidNumber(board, k % DIM, k / DIM, LIST_NUMS[n])) {
                    vn.add(LIST_NUMS[n]);
                }
            }
            List<ParallelSolver> recList = new ArrayList<>();
            for(int n=0; n<vn.size(); n++) {
                board[k] = vn.get(n);
                ParallelSolver rec = new ParallelSolver(Arrays.copyOf(board, board.length), k + 1, cutoff * vn.size(), cores); //copyBoard(board)
                recList.add(rec);
                board[k] = '.';
            }

            for (ParallelSolver p : ForkJoinTask.invokeAll(recList)) {
                try {
                    sol += p.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Errore nel get");
                }
            }
        }
        //cutoff
        else {
            sol += sequential(board, k);
        }

        return sol;
    }

    private int sequential(char[] board, int k) {
        int sol = 0;
        if (isComplete(board))
            return 1;
        if (k == DIM*DIM)
            return 0;
        if (board[k] != '.') {
            sol += sequential(board, k + 1);
        }
        else {
            for (int n = 0; n < 9; n++) {
                if(board[k] == '.') {
                    if (isAValidNumber(board, k%DIM, k/DIM, LIST_NUMS[n])) {
                        board[k] = LIST_NUMS[n];
                        sol += sequential(board, k + 1);
                    }
                }
                board[k] = '.';
            }
        }
        return sol;
    }
}
