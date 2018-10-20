package concurrency;

public final class Matrix{
    public static Integer[][] sum(Integer[][] a, Integer[][] b){
        if(a[0].length != b[0].length || a.lenght != b.length){
            throw new ArithmeticException
                (
                 "No se puede sumar matrices de diferentes dimensiones."
                 )
        }

        int row_size = a.length;
        int column_size = a[0].length;

        Integer[row_size][column_size] result;

        for(int i=0; i<row_size; i++){
            for(int j=0; i<column_size; j++){
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }
}
