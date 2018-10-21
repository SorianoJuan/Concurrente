package test;

import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.assertTrue;

import main.Matrix;


public class MatrixTest{
    int[][] a;
    int[][] b;

    @Before
    public void setUp(){
        this.a = new int[][]
            {
                { 5, 8, 1},
                {-6, 6, 9},
                { 4, 3, 2}
            };

        this.b = new int[][]
            {
                { 8, 7, 2},
                {-3, 1,-9},
                {-4, 3,10}
            };
    }

    @Test
    public void sumTest(){
        int[][] expected = {
            {13, 15,  3},
            {-9,  7,  0},
            { 0,  6, 12}
        };

        assertTrue(Arrays.deepEquals(Matrix.sum(this.a, this.b), expected));
    }

    @Test
    public void matmultTest(){
        int[][] expected = {
            {  12,   46,  -52},
            {-102,   -9,   24},
            {  15,   37,    1}
        };

        assertTrue(Arrays.deepEquals(Matrix.matmul(this.a, this.b), expected));
    }

    @Test
    public void transposeTest(){
        int[][] expected = {
            {5, -6,  4},
            {8,  6,  3},
            {1,  9,  2}
        };

        assertTrue(Arrays.deepEquals(Matrix.transpose(this.a), expected));
    }
}
