public class Experiment2 {
    public static void main(String[] args){
    int [] startVal = {0, 5, 10, 15, 20, 25, 30, 35, 40};
    System.out.println("numLocked\tSolved\tNo solution\tGave up\tAvg steps");
    for (int numLocked : startVal) {
        int solved = 0;
        int noSolution = 0;
        int gaveUp = 0;
        int totalSteps = 0;
        
        // Inner loop 
        for (int i = 0; i < 10; i++) {
                Sudoku game = new Sudoku(numLocked, 0);
                game.setUseBestCell(false); // Set to true for the second run!
                // second run 
                //game.setUseBestCell(true);
                
                boolean isSolved = game.solve();
            
                // 1. If it solved: increment solved, add game.getSteps() to totalSteps
                if(isSolved){
                    solved++;
                    totalSteps += game.getSteps();
                } else if(game.getSteps() > Sudoku.MAX_STEPS){ // 2. Else if game.getSteps() > Sudoku.MAX_STEPS: increment gaveUp
                    gaveUp++;
                    
                } else {
                // 3. Else: increment noSolution
                noSolution++;
            }
        }
        double avgSteps = (solved > 0) ? (double) totalSteps / solved : 0;
        System.out.printf("%d\t%d\t%d\t%d\t%.1f\n", numLocked, solved, noSolution, gaveUp, avgSteps);
      
    }
}
}



