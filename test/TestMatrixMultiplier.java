import matrixMultiplication.MatrixMultiplier;
import org.junit.Test;

import java.util.Arrays;
import static org.junit.Assert.assertArrayEquals;

public class TestMatrixMultiplier {

    // Non-threaded matrix-vector multiplication
    public static int[] nonThreadedMatrixMultiply(int[][] matrix, int[] vector) {
        int[] result = new int[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }

        return result;
    }
    @Test
    public void testMultiplier() {
        int[][] matrixSizes = {
                {64, 64},
                {512, 512},
                {1024, 1024},
                {2048, 2048},
                {4096, 4096},
                {8192, 8192},
                {16384, 16384}
        };

        int[] threadCounts = {1, 2, 4, 8, 16}; // Test different numbers of threads

        for (int[] size : matrixSizes) {
            int rows = size[0];
            int columns = size[1];

            System.out.println("Matrix size: " + rows + "x" + columns);

            // Create matrix and vector
            int[][] matrix = MatrixMultiplier.createMatrix(rows, columns);
            int[] vector = MatrixMultiplier.createVector(columns);


            for (int threads : threadCounts) {
                System.out.print("Threads: " + threads + "\t");
                MatrixMultiplier.main(new String[]{String.valueOf(rows), String.valueOf(columns), String.valueOf(threads)});
            }
            System.out.println("--------------------------");
        }
    }

}
