package main;

public final class Matrix{
    public static int[][] sum(int[][] a, int[][] b){
        if(a[0].length != b[0].length || a.length != b.length){
            throw new ArithmeticException
                (
                 "No se puede sumar matrices de diferentes dimensiones."
                 );
        }

        int row_amount = a.length;
        int column_amount = a[0].length;

        int[][] result = new int[row_amount][column_amount];

        for(int i=0; i<row_amount; i++){
            for(int j=0; j<column_amount; j++){
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    public static int[][] matmul(int[][] a, int[][] b){
        int rowamount_a = a.length;
        int rowamount_b = b.length;
        int columnamount_a = a.length;
        int columnamount_b = b.length;

        if(columnamount_a != rowamount_b){
            throw new ArithmeticException
                (
                 "No se puede hacer producto entre matrices con esas dimensiones"
                 );
        }

        int[][] result = new int[rowamount_a][columnamount_b];

        for(int i = 0; i<rowamount_a; i++){
            for(int j = 0; j<columnamount_b; j++){
                for(int k = 0; k<columnamount_a; k++){
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return result;
    }

    public static int[][] transpose(int[][] a){
        int row_amount = a.length;
        int column_amount = a[0].length;
        
        int[][] result = new int[column_amount][row_amount];

        for(int i = 0; i < row_amount; i++){
            for(int j = 0; j < column_amount; j++){
                result[j][i] = a[i][j];
            }
        }
        return result;
    }
}
