package kz.topsecurity.client.service.trackingService.kalmanFilter;


import android.util.Log;

public class HCMatrixObject {

    //MARK: - HCMatrixObject properties

    /// Number of Rows in Matrix
    private int rows;

    /// Number of Columns in Matrix
    private int columns;

    /// Surge Matrix object
    private Matrix matrix;

    public void setMatrix(Matrix matrix){
        this.matrix = matrix;
    }

    public Matrix getMatrix(){
        return matrix;
    }
        //MARK: - Initialization

        /// Initailization of matrix with specified numbers of rows and columns
    public HCMatrixObject (int rows ,int columns ) {
        this.rows = rows;
        this.columns = columns;
        this.matrix = new Matrix(rows, columns, 0.0);
    }

    //MARK: - HCMatrixObject functions

    /// getIdentityMatrix Function
    /// ==========================
    /// For some dimension dim, return identity matrix object
    ///
    /// - parameters:
    ///   - dim: dimension of desired identity matrix
    /// - returns: identity matrix object
    public static HCMatrixObject getIdentityMatrix(int dim)
    {
        return getDiagonalMatrix(dim,1.0);
    }

    public static HCMatrixObject getDiagonalMatrix(int dim,double value)
    {
        HCMatrixObject identityMatrix = new HCMatrixObject(dim, dim);

        for(int i=0; i<dim; i++)
        {
            for(int j=0; j<dim; j++)
            {
                if (i == j)
                {
                    identityMatrix.getMatrix().setValue(i,j,value);
                }
            }
        }

        return identityMatrix;
    }
    /// addElement Function
    /// ===================
    /// Add double value on (i,j) position in matrix
    ///
    /// - parameters:
    ///   - i: row of matrix
    ///   - j: column of matrix
    ///   - value: double value to add in matrix
    public void addElement(int i,int j,double value)
    {
        if (this.matrix.rows <= i && this.matrix.columns <= j)
        {
            Log.e("HCMATRIX","print");
        }
        else
        {
            matrix.setValue(i,j,value);
        }
    }

    /// setMatrix Function
    /// ==================
    /// Set complete matrix
    ///
    /// - parameters:
    ///   - matrix: array of array of double values


    /// getElement Function
    /// ===================
    /// Returns double value on specific position of matrix
    ///
    /// - parameters:
    ///   - i: row of matrix
    ///   - j: column of matrix

    public Double getElement(int i, int j)
    {
        if (this.matrix.rows <= i && this.matrix.columns <= j)
        {
            Log.e("HCMATRIX","print");
            return null;
        }
        else
        {
            return matrix.getValue(i,j);
        }
    }

    /// Transpose Matrix Function
    /// =========================
    /// Returns result HCMatrixObject of transpose operation
    ///
    /// - returns: transposed HCMatrixObject object
    public HCMatrixObject transpose()
    {
        HCMatrixObject result = new HCMatrixObject(rows, columns);
        result.setMatrix(matrix);
//        result.matrix = matrix.transpose(self.matrix);
        result.matrix.transpose();
        return result;
    }

    /// Inverse Matrix Function
    /// =======================
    /// Returns inverse matrix object
    ///
    /// - returns: inverse matrix object
//    public HCMatrixObject inverseMatrix()
//    {
//        HCMatrixObject result = new HCMatrixObject( rows, columns);
//
//        result.matrix = Surge.inv(self.matrix)
//
//        return result
//    }

    /// Print Matrix Function
    /// =====================
    /// Printing the entire matrix


    //MARK: - Predefined HCMatrixObject operators

    /// Predefined + operator
    /// =====================
    /// Returns result HCMatrixObject of addition operation
    ///
    /// - parameters:
    ///   - left: left addition HCMatrixObject operand
    ///   - right: right addition HCMatrixObject operand
    /// - returns: result HCMatrixObject object of addition operation
    public static HCMatrixObject plus(HCMatrixObject left, HCMatrixObject right)
    {
        HCMatrixObject result = new HCMatrixObject(left.rows, left.columns);

        result.matrix = add(left.matrix,  right.matrix);

        return result;
    }

    private static Matrix add(Matrix matrix1, Matrix matrix2) {
        Matrix sum = new Matrix(matrix1.rows,matrix1.columns);
        for(int i = 0; i < matrix1.rows; i++) {
            for (int j = 0; j < matrix1.columns; j++) {
                sum.setValue(i,j,matrix1.getValue(i,j) + matrix2.getValue(i,j));
            }
        }
        return sum;
    }

    /// Predefined - operator
    /// =====================
    /// Returns result HCMatrixObject of subtraction operation
    ///
    /// - parameters:
    ///   - left: left subtraction HCMatrixObject operand
    ///   - right: right subtraction HCMatrixObject operand
    /// - returns: result HCMatrixObject object of subtraction operation
    public static HCMatrixObject minus(HCMatrixObject left, HCMatrixObject right)
    {
        HCMatrixObject result = new HCMatrixObject( left.rows, left.columns);

        result.matrix = substract(left.matrix,  right.matrix);

        return result;

    }

    private static Matrix substract(Matrix matrix1, Matrix matrix2) {
        Matrix sum = new Matrix(matrix1.rows,matrix1.columns);
        for(int i = 0; i < matrix1.rows; i++) {
            for (int j = 0; j < matrix1.columns; j++) {
                sum.setValue(i,j,matrix1.getValue(i,j) - matrix2.getValue(i,j));
            }
        }
        return sum;
    }

    /// Predefined * operator
    /// =====================
    /// Returns result HCMatrixObject of multiplication operation
    ///
    /// - parameters:
    ///   - left: left multiplication HCMatrixObject operand
    ///   - right: right multiplication HCMatrixObject operand
    /// - returns: result HCMatrixObject object of multiplication operation
    public static HCMatrixObject multiply (HCMatrixObject left, HCMatrixObject right)
    {
        Matrix mul = mul(left.matrix, right.matrix);
        HCMatrixObject  resultMatrix = new HCMatrixObject(mul.rows,mul.columns);
        resultMatrix.setMatrix(mul);

        return resultMatrix;
    }

    private static Matrix mul(Matrix matrix, Matrix p1) {
        int m1 =matrix.rows;
        int n1 = matrix.columns;
        int m2 =p1.rows;
        int n2 =p1.columns;
        Matrix product = new Matrix(matrix.rows,p1.columns,0.0);
        for(int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < p1.columns; j++) {
                for (int k = 0; k < matrix.columns; k++) {
                    Double v = product.getValue(i,j)  + matrix.getValue(i, k) * p1.getValue(k, j);
                    if(!v.isNaN() )
                        product.setValue(i,j, v);
                    else
                        product.setValue(i,j,0.0);
                }
            }
        }
        return product;
    }

    public static double[][] multiply(double[][] a, double[][] b) {
        int m1 = a.length;
        int n1 = a[0].length;
        int m2 = b.length;
        int n2 = b[0].length;
        if (n1 != m2) throw new RuntimeException("Illegal matrix dimensions.");
        double[][] c = new double[m1][n2];
        for (int i = 0; i < m1; i++)
            for (int j = 0; j < n2; j++)
                for (int k = 0; k < n1; k++)
                    c[i][j] += a[i][k] * b[k][j];
        return c;
    }

    public HCMatrixObject inverse(){
        Double[][] invert = invert(getMatrix().getDataList(), rows, columns);

        HCMatrixObject hcMatrixObject = new HCMatrixObject(rows, columns);
        hcMatrixObject.getMatrix().setDataList(invert);
        return hcMatrixObject;
    }

    private static Double[][] invert(Double a[][] , int n, int m)
    {
        Double x[][] = new Double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i=0; i<n; ++i)
            b[i][i] = 1;

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i=0; i<n-1; ++i)
            for (int j=i+1; j<n; ++j)
                for (int k=0; k<n; ++k)
                    b[index[j]][k]
                            -= a[index[j]][i]*b[index[i]][k];

        // Perform backward substitutions
        for (int i=0; i<n; ++i)
        {
            x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
            for (int j=n-2; j>=0; --j)
            {
                x[j][i] = b[index[j]][i];
                for (int k=j+1; k<n; ++k)
                {
                    x[j][i] -= a[index[j]][k]*x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

// Method to carry out the partial-pivoting Gaussian
// elimination.  Here index[] stores pivoting order.

    private static void gaussian(Double a[][], int index[])
    {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i=0; i<n; ++i)
            index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i=0; i<n; ++i)
        {
            double c1 = 0;
            for (int j=0; j<n; ++j)
            {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j=0; j<n-1; ++j)
        {
            double pi1 = 0;
            for (int i=j; i<n; ++i)
            {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1)
                {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i=j+1; i<n; ++i)
            {
                double pj = a[index[i]][j]/a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l=j+1; l<n; ++l)
                    a[index[i]][l] -= pj*a[index[j]][l];
            }
        }
    }
}
