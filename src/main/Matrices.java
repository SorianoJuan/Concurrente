import Jama.Matrix; 

public class Matrices {
   public static void main(String[] args) { 
      Matrix A = new Matrix(new double[][] { { 1,  0,  0 },
                                             { 0,  1,  0 },
                                             { 0,  0,  1 } }); 

      Matrix b = new Matrix(new double[][] { {  4 },
                                             {  6 },
                                             {  8 } });
      Matrix x = A.solve(b);
      Matrix residual = A.times(x).minus(b);
      double rnorm = residual.normInf();
      
      Matrix C = new Matrix(3, 2);
      
      System.out.println("C");
      C.print(9, 6);

      System.out.println("A");
      A.print(9, 6);                // printf("%9.6f");

      System.out.println("b");
      b.print(9, 6);

      System.out.println("x");
      x.print(9, 6);
      System.out.println(x);

      System.out.println("residual");
      residual.print(9, 6);

   }

}


