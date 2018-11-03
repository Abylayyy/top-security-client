package kz.topsecurity.client.service.trackingService.kalmanFilter;

import android.location.Location;

public class HCKalmanAlgorithm {

    //MARK: - HCKalmanAlgorithm properties

    /// The dimension M of the state vector.
    private static final int stateMDimension = 6;

    /// The dimension N of the state vector.
    private static final int stateNDimension = 1;

    /// Acceleration variance magnitude for GPS
    /// =======================================
    /// **Sigma** value is  value for Acceleration Noise Magnitude Matrix (Qt).
    /// Recommended value for **sigma** is 0.0625, this value is optimal for GPS problem,
    /// it was concluded by researches.
    private  static final double sigma = 0.0625;


    /// Value for Sensor Noise Covariance Matrix
    /// ========================================
    /// Default value is 29.0, this is the recommended value for the GPS problem, with this value filter provides optimal accuracy.
    /// This value can be adjusted depending on the needs, the higher value
    /// of **rVaule** variable will give greater roundness trajectories, and vice versa.
    public Double rValue(){
        return _rValue;
    }

    public void rValue(Double newValue){
        _rValue = newValue;
    }

    private Double _rValue = 29.0;


    /// Previous State Vector
    /// =====================
    /// **Previous State Vector** is mathematical representation of previous state of Kalman Algorithm.
    private HCMatrixObject xk1;


    /// Covariance Matrix for Previous State
    /// ====================================
    /// **Covariance Matrix for Previous State** is mathematical representation of covariance matrix for previous state of Kalman Algorithm.
    private HCMatrixObject Pk1;


    /// Prediction Step Matrix
    /// ======================
    /// **Prediction Step Matrix (A)** is mathematical representation of prediction step of Kalman Algorithm.
    /// Prediction Matrix gives us our next state. It takes every point in our original estimate and moves it to a new predicted location,
    /// which is where the system would move if that original estimate was the right one.
    private HCMatrixObject A;


    /// Acceleration Noise Magnitude Matrix
    /// ===================================
    /// **Acceleration Noise Magnitude Matrix (Qt)** is mathematical representation of external uncertainty of Kalman Algorithm.
    /// The uncertainty associated can be represented with the “world” (i.e. things we aren’t keeping track of)
    /// by adding some new uncertainty after every prediction step.
    private HCMatrixObject Qt;


    /// Sensor Noise Covariance Matrix
    /// ==============================
    /// **Sensor Noise Covariance Matrix (R)** is mathematical representation of sensor noise of Kalman Algorithm.
    /// Sensors are unreliable, and every state in our original estimate might result in a range of sensor readings.
    private HCMatrixObject R;

    /// Measured State Vector
    /// =====================
    /// **Measured State Vector (zt)** is mathematical representation of measuerd state vector of Kalman Algorithm.
    /// Value of this variable was readed from sensor, this is mean value to the reading we observed.
    private HCMatrixObject zt;

    /// Time of last measurement
    /// ========================
    /// This time is used for calculating the time interval between previous and last measurements
    private long previousMeasureTime ;

    /// Previous State Location
    private CLLocation previousLocation = new CLLocation();


    //MARK: - HCKalmanAlgorithm initialization

    /// Initialization of Kalman Algorithm Constructor
    /// ==============================================
    /// - parameters:
    ///   - initialLocation: this is CLLocation object which represent initial location
    ///                      at the moment when algorithm start
    public void init(CLLocation initialLocation)
    {
        this.previousMeasureTime = System.currentTimeMillis();
        this.previousLocation = new CLLocation();

        this.xk1 = new HCMatrixObject( stateMDimension,  stateNDimension);
        this.Pk1 = new HCMatrixObject( stateMDimension,  stateMDimension);
        this.A = new HCMatrixObject( stateMDimension,  stateMDimension);
        this.Qt = new HCMatrixObject( stateMDimension,  stateMDimension);
        this.R = new HCMatrixObject( stateMDimension,  stateMDimension);
        this.zt = new HCMatrixObject( stateMDimension,  stateNDimension);

        initKalman(initialLocation);
    }

    //MARK: - HCKalmanAlgorithm functions

    /// Initialization of Kalman Algorithm Function
    /// ===========================================
    /// This set up Kalman filter matrices to the default values
    /// - parameters:
    ///   - initialLocation: this is CLLocation object which represent initial location
    ///                      at the moment when algorithm start
    public HCKalmanAlgorithm(Location location){
        CLLocation clLocation = new CLLocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), System.currentTimeMillis(),location.getAccuracy());
        init(clLocation);
    }

    private void initKalman(CLLocation initialLocation)
    {
        // Set timestamp for start of measuring
        previousMeasureTime = initialLocation.getTimestamp();

        // Set initial location
        previousLocation = initialLocation;
        // Set Previous State Matrix
        // xk1 -> [ initial_lat  lat_velocity = 0.0  initial_lon  lon_velocity = 0.0 initial_altitude altitude_velocity = 0.0 ]T
        Matrix matrix = new Matrix(6,1);
        matrix.setValue(0,0,initialLocation.getLatitude());
        matrix.setValue(1,0,0.0);
        matrix.setValue(2,0,initialLocation.getLongitude());
        matrix.setValue(3,0,0.0);
        matrix.setValue(4,0,initialLocation.getAltitude());
        matrix.setValue(5,0,0.0);
        xk1.setMatrix(matrix);
        Matrix matrix2 = new Matrix(6,6,0.0);
        Pk1.setMatrix(matrix2);
        // Set initial Covariance Matrix for Previous State

        Matrix matrix3 = Matrix.getIdentityMatrix(6);

        // Prediction Step Matrix initialization
        A.setMatrix( matrix3);

        Matrix matrix4 = Matrix.getDiagonalMatrix(6,_rValue);
        // Sensor Noise Covariance Matrixinitialization
        R.setMatrix(matrix4);
    }

    /// Restart Kalman Algorithm Function
    /// ===========================================
    /// This restart Kalman filter matrices to the default values
    /// - parameters:
    ///   - newStartLocation: this is CLLocation object which represent location
    ///                       at the moment when algorithm start again
    void resetKalman(CLLocation newStartLocation)
    {
        this.initKalman(newStartLocation);
    }

    /// Process Current Location
    /// ========================
    ///  This function is a main. **processState** will be processed current location of user by Kalman Filter
    ///  based on previous state and other parameters, and it returns corrected location
    /// - parameters:
    ///   - currentLocation: this is CLLocation object which represent current location returned by GPS.
    ///                      **currentLocation** is real position of user, and it will be processed by Kalman Filter.
    /// - returns: CLLocation object with corrected latitude, longitude and altitude values

    public CLLocation processState(Location location){
        return processState(new CLLocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), System.currentTimeMillis(),location.getAccuracy()));
    }

    CLLocation processState(CLLocation currentLocation)
    {
        // Set current timestamp
        long newMeasureTime = currentLocation.getTimestamp();

        // Convert measure times to seconds
        long newMeasureTimeSeconds = newMeasureTime;
        long lastMeasureTimeSeconds = previousMeasureTime;

        // Calculate timeInterval between last and current measure
        long timeInterval = newMeasureTimeSeconds - lastMeasureTimeSeconds;

        // Calculate and set Prediction Step Matrix based on new timeInterval value
        Matrix matrix1 =Matrix.getIdentityMatrix(6);
        matrix1.setValue(0,1,timeInterval);
        matrix1.setValue(2,5,timeInterval);
        matrix1.setValue(4,5,timeInterval);
        A.setMatrix(matrix1);

        // Parts of Acceleration Noise Magnitude Matrix
        double part1 = sigma*((Math.pow(timeInterval, 4.0))/4.0);
        double part2 = sigma*((Math.pow(timeInterval, 3.0))/2.0);
        double part3 = sigma*(Math.pow(timeInterval, 2.0));

        // Calculate and set Acceleration Noise Magnitude Matrix based on new timeInterval and sigma values
        Matrix matrix2 = new Matrix(6,6,0.0);
        matrix2.setValue(0,0,part1);
        matrix2.setValue(0,1,part2);
        matrix2.setValue(1,0,part2);
        matrix2.setValue(1,1,part3);
        matrix2.setValue(2,2,part1);
        matrix2.setValue(2,3,part2);
        matrix2.setValue(3,2,part2);
        matrix2.setValue(3,3,part3);
        matrix2.setValue(4,4,part1);
        matrix2.setValue(4,5,part2);
        matrix2.setValue(5,4,part2);
        matrix2.setValue(5,5,part3);
        Qt.setMatrix(matrix2);

        // Calculate velocity components
        // This is value of velocity between previous and current location.
        // Distance traveled from the previous to the current location divided by timeInterval between two measurement.
        double velocityXComponent = (previousLocation.getLatitude() - currentLocation.getLatitude())/timeInterval;
        double velocityYComponent = (previousLocation.getLongitude() - currentLocation.getLongitude())/timeInterval;
        double velocityZComponent = (previousLocation.getAltitude() - currentLocation.getAltitude())/timeInterval;

        // Set Measured State Vector; current latitude, longitude, altitude and latitude velocity, longitude velocity and altitude velocity

        Matrix matrix = new Matrix(6,1);
        matrix.setValue(0,0,currentLocation.getLatitude());
        matrix.setValue(1,0,velocityXComponent);
        matrix.setValue(2,0,currentLocation.getLongitude());
        matrix.setValue(3,0,velocityYComponent);
        matrix.setValue(4,0,currentLocation.getAltitude());
        matrix.setValue(5,0,velocityZComponent);
        zt.setMatrix(matrix);

        // Set previous Location and Measure Time for next step of processState function.
        previousLocation = currentLocation;
        previousMeasureTime = newMeasureTime;

        // Return value of kalmanFilter
        return kalmanFilter();
    }

    /// Kalman Filter Function
    /// ======================
    /// This is additional function, which helps in the process of correcting location
    /// Here happens the whole mathematics related to Kalman Filter. Here is the essence.
    /// The algorithm consists of two parts - Part of Prediction and Part of Update State
    ///
    /// Prediction part performs the prediction of the next state based on previous state, prediction matrix (A) and takes into consideration
    /// external uncertainty factor (Qt). It returns predicted state and covariance matrix -> xk, Pk
    ///
    /// Next step is Update part. It combines predicted state with sensor measurement. Update part first calculate Kalman gain (Kt).
    /// Kalman gain takes into consideration sensor noice. Next based on this value, value of predicted state and value of measurement,
    /// algorithm can calculate new state, and function return corrected latitude, longitude and altitude values in CLLocation object.
    private CLLocation kalmanFilter()
    {
        HCMatrixObject xk = HCMatrixObject.multiply(A,xk1);
        HCMatrixObject Pk = HCMatrixObject.plus(HCMatrixObject.multiply(HCMatrixObject.multiply(A,Pk1),A.transpose()),Qt);

        HCMatrixObject tmp = HCMatrixObject.multiply(Pk,R);

        // Kalman gain (Kt)
        HCMatrixObject Kt = HCMatrixObject.multiply(Pk,tmp.inverse());

        HCMatrixObject xt = HCMatrixObject.plus( xk , HCMatrixObject.multiply(Kt , HCMatrixObject.minus(zt ,xk)));
        HCMatrixObject Pt = HCMatrixObject.multiply (HCMatrixObject.minus( HCMatrixObject.getIdentityMatrix( stateMDimension) ,Kt), Pk);

        this.xk1 = xt;
        this.Pk1 = Pt;

        Double lat = xk1.getElement(0,0);
        Double lon = xk1.getElement(2,0);
        Double altitude = xk1.getElement(4,0);

        return new CLLocation(lat, lon,altitude,previousMeasureTime,previousLocation.getAccuracy());
    }
}