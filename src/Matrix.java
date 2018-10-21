package concurrency;

public final class Matrix{
    public static Integer[][] sum(Integer[][] a, Integer[][] b){
        if(a[0].length != b[0].length || a.lenght != b.length){
            throw new ArithmeticException
                (
                 "No se puede sumar matrices de diferentes dimensiones."
                 );
        }

        int row_amount = a.length;
        int column_amount = a[0].length;

        Integer[][] result = new Integer[row_amount][column_amount];

        for(int i=0; i<row_amount; i++){
            for(int j=0; i<column_amount; j++){
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    public static Integer[][] matmul(Integer[][] a, Integer[][] b){
        int rowamount_a = a.lenght;
        int rowamount_b = b.length;
        int columnamount_a = a.lenght;
        int columnamount_b = b.length;

        if(columnamount_a != rowamount_b){
            throw new ArithmeticException
                (
                 "No se puede hacer producto entre matrices con esas dimensiones"
                 );
        }

        Integer[][] result = new Integer[rowamount_a][columnamount_b];

        for(int i = 0; i<rowamount_a; i++){
            for(int j = 0; j<columnamount_b; j++){
                for(int k = 0; k<columnamount_a; k++){
                    result[i][j] += a[rowamount_a][k] * b[k][columnamount_b];
                }
            }
        }

        return result;
    }

    public static Integer[][] transpose(Integer [][] a){
        int row_amount = a.length;
        int column_amount = a[0].length;
        
        Integer[][] result = Integer[column_amount][row_amount];

        for(int i = 0; i < row_amount; i++){
            for(int j = 0; j < column_amount; j++){
                result[j][i] = a[i][j];
            }
        }
        return result;
    }
}
