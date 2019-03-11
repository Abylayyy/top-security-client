package kz.topsecurity.client.service.trackingService.kalmanFilter;

public class Matrix {

    Double[][] dataList;
    int rows ;
    int columns;

    public Double[][] getDataList() {
        return dataList;
    }

    public void setDataList(Double[][] dataList) {
        this.dataList = dataList;
    }


    public Matrix(int rows , int columns){
        this.rows = rows;
        this.columns = columns;
        dataList = new Double[rows][columns];
    }

    public Matrix(int rows , int columns , double initValue){
        this.rows = rows;
        this.columns = columns;
        dataList = new Double[rows][columns];
        setValues(initValue);
    }


    private void setValues(double initValue) {
        for (int i=0; i < rows ; i++){
            for (int j =0 ; j<columns ; j++){
                dataList[i][j] = initValue;
            }
        }
    }

    public void setValue(int row_index, int column_index , double _value){
        dataList[row_index][column_index] = _value;
    }

    public Double getValue(int i, int j) {
        return dataList[i][j];
    }

    public void transpose() {
        Double[][] temp = new Double[columns][rows];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                temp[j][i] = dataList[i][j];
            }
        }
        dataList = temp;
    }

    public static Matrix getIdentityMatrix(int dim)
    {
        return getDiagonalMatrix(dim,1.0);
    }


    public static Matrix getDiagonalMatrix(int dim, Double value) {
        Matrix identityMatrix = new Matrix(dim, dim,0.0);

        for(int i=0; i<dim; i++)
        {
            for(int j=0; j<dim; j++)
            {
                if (i == j)
                {
                    identityMatrix.setValue(i,j,value);
                }
            }
        }

        return identityMatrix;
    }

}
