

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {
    	
    	System.out.println("Qui ho cambiato :)");
    	
    	System.out.println("Qui secondo commit!");
    	
    	System.out.println("Secondo branch di prova");
    	System.out.println("Sempre il secondo branch");

        if(args.length == 0) {
            out.println("Nessun file di gioco è stato passato come argomento al programma.");
            return;
        }

        SudokuSolver ss = new SudokuSolver(args[0]);

        ss.printBoard(ss.getBoard());
        out.println("-----------------------");
        long lStartTime = System.currentTimeMillis();

        //Celle vuote
        out.println("Celle vuote:\t\t" + ss.emptyCells(ss.getBoard()));
        out.println("Fattore di riempimento:\t" + (int)(100.0/81.0*(81-ss.emptyCells(ss.getBoard()))) + "%");

        //Calcolo parallelo
        out.println("\nEsecuzione in parallelo");
        long sspStartTime = System.currentTimeMillis();
        try {
            out.println("Numero soluzioni:\t" + ss.parallel(ss.getBoard(), 1));
        } catch (InterruptedException ie) {
            out.println("Interrupted Excpetion");
        }
        long sspEndTime = System.currentTimeMillis();
        long tn = sspEndTime - sspStartTime;
        out.println("Tempo di esecuzione:\t" + tn + "ms");

        //Calcolo sequenziale
        out.println("\nEsecuzione sequenziale");
        long ssnStartTime = System.currentTimeMillis();
        out.println("Numero soluzioni:\t" + ss.sequential(ss.getBoard(), 0));
        long ssnEndTime = System.currentTimeMillis();
        long ts = ssnEndTime - ssnStartTime;
        out.println("Tempo di esecuzione:\t" + ts + "ms");

        //Speedup
        float su = ((float) ts)/((float)tn);
        out.println("\nSpeedup: " + su + "\n");

        //Tempo totale
        long lEndTime = System.currentTimeMillis();
        out.println("Tempo totale: " + (lEndTime - lStartTime) + "ms");

    }
}
