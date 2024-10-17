import java.util.Arrays;
import java.util.Random;

/**
 * This class represents a threaded matrix-multiplier.
 * Each thread is tasked with multiplying a certain number of rows of a matrix by a constant vector. The number of
 * rows is determined by the total number of rows divided by the number of threads that are created.
 */
public class MatrixMultiplier extends Thread{
    private final int[][] matrix;
    private final int[] vector;
    private final int[] result;
    private final int start;
    private final int end;

    public MatrixMultiplier(int[][] matrix, int[] vector, int[] result, int start, int end) {
        this.matrix = matrix;
        this.vector = vector;
        this.result = result;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for (int i = this.start; i < this.end; i++)
            for (int j = 0; j < this.vector.length; j++)
                this.result[i] += matrix[i][j] * this.vector[j];
    }

    public static int[][] createMatrix(int rows, int columns) {
        Random rand = new Random();

        int[][] matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                matrix[i][j] = rand.nextInt(11);

        return matrix;
    }

    public static int[] createVector(int rows) {
        Random rand = new Random();

        int[] vector = new int[rows];
        for (int i = 0; i < rows; i++)
            vector[i] = rand.nextInt(11);

        return vector;
    }

    /**
     * @param args Given in the exact order: rows, columns, threads
     */
    public static void main(String[] args) throws IllegalArgumentException {

        // Parsing arguments
        if (args.length != 3) {
            throw new IllegalArgumentException("Requires exactly 3 integers for numbers of rows, columns and threads.");
        }

        int rows = Integer.parseInt(args[0]);
        int columns = Integer.parseInt(args[1]);
        int threads = Integer.parseInt(args[2]);

        // If rows are less than the number of threads, then create exact #rows threads.
        if (rows < threads)
            threads = rows;
        // Since rows > threads, each thread should operate on more than one row
        int rowsPerThread = rows / threads;

        // Create matrices and vectors
        int[][] matrix = createMatrix(rows,columns);
        int[] vector = createVector(columns);
        int[] result = new int[rows];

        // Create threads
        MatrixMultiplier[] multipliers = new MatrixMultiplier[threads];
        for (int i = 0; i < threads; i++) {
            int start = i * rowsPerThread;
            int end = (i == threads - 1)? rows : start + rowsPerThread;
            multipliers[i] = new MatrixMultiplier(matrix,vector,result,start,end);
        }

        long startTS = System.currentTimeMillis();  // Begin capturing time for thread operations
        for (int i = 0; i < threads; i++)
            multipliers[i].start();

        // Fetch results
        try {
            for (MatrixMultiplier multiplier : multipliers) {
                multiplier.join();
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        long finishTS = System.currentTimeMillis(); // End capturing time for thread operations


        // Print matrix A
        System.out.print("A=");
        for (int i = 0; i<rows; i++)
            System.out.println("\t" + Arrays.toString(matrix[i]));
        System.out.println("--------------------------");

        // Print vector v
        System.out.print("v=");
        System.out.println("\t" + Arrays.toString(vector));
        System.out.println("--------------------------");

        // Print result A*v
        System.out.print("A*v=");
        System.out.println(Arrays.toString(result));
        System.out.println("--------------------------");
        System.out.println("Total time taken for threads:\t" + (finishTS-startTS) + " ms");
    }
}
