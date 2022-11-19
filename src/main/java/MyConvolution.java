package uk.ac.soton.ecs.cp6g18;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

/**
 * COMP3204: Computer Vision
 * 
 * Coursework 2: Exercise 1
 * 
 * Performing template convolution on a given image.
 * 
 * @author Charles Powell
 */
public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {

    // member variables
	private float[][] kernel; /** The template used in the convolution */

    //////////////////
    // INITIALIZING //
    //////////////////

    /**
     * Class constructor.
     * 
     * @param kernel The template to be used in the convolution.
     */
	public MyConvolution(float[][] kernel) {
		//note that like the image pixels kernel is indexed by [row][column]
		this.kernel = kernel;
	}

    ///////////////// 
    // CONVOLUTION //
    /////////////////

    // PROCESSING IMAGE //

    /**
     * Performs template convolution on the provided image.
     * 
     * @param image The image the convolution is being performed on.
     */
	@Override
	public void processImage(FImage image) {
        /**
         * Defining a copy of the image.
         */

        FImage rawImage = image.clone();
        
        /**
         * Inverting the kernel
         */

        float[][] invertedKernel = MyConvolution.invertMatrix(this.kernel);

        /**
         * Processing image points.
         */

        // iterating through image points and processing each one
        for(int row = 0; row < image.getHeight(); row++){
            for(int col = 0; col < image.getWidth(); col++){
                // gathering the processed value
                float processedValue = MyConvolution.processPoint(rawImage, invertedKernel, row, col);

                // putting the processed value into the image
                image.pixels[row][col] = processedValue;
            }
        }
	}

    // PROCESSING IMAGE POINT //

    /**
     * Performs template convolution on a single point wthin the image.
     * 
     * @param image The image being processed.
     * @param invertedKernel The inverted kernel to be applied to the point.
     * @param targetPointRow The row position of the target point.
     * @param targetPointColumn The column position of the target point.
     * @return The processed point value.
     */
    private static float processPoint(FImage image, float[][] invertedKernel, int targetPointRow, int targetPointCol){

        /**
         * Gathering image points within domain of kernel
         */
        float[][] imagePoints = MyConvolution.getPointsInKernelDomain(image, invertedKernel, targetPointRow, targetPointCol);

        /**
         * Calculating weighted sum of points in domain of kernel (i.e., the new processed value)
         */

        float processedValue = MyConvolution.calculateWeightedSum(invertedKernel, imagePoints);

        /**
         * Returning weighed sum of pixel values as new processed pixel value.
         */

        return processedValue;
    }

    ////////////////////
    // HELPER METHODS //
    ////////////////////

    /**
     * Inverts a matrix along both the horizontal and vertical axis.
     * 
     * @param matrix The matrix being inverted.
     * @return The matrix inverted along it's horizontal and vertical axis.
     */
    private static float[][] invertMatrix(float[][] matrix){

        // creating empty matrix to store the inversion
        float[][] invertedMatrix = new float[MyConvolution.getMatrixHeight(matrix)][MyConvolution.getMatrixWidth(matrix)];

        // variables representing the max row and column indexes
        int maxRow = MyConvolution.getMatrixHeight(matrix) - 1;
        int maxCol = MyConvolution.getMatrixWidth(matrix) - 1;

        // iterating through the cells in the matrix and placing them into the inverted matrix
        for(int row = 0; row <= maxRow; row++){
            for(int col = 0; col <= maxCol; col++){
                invertedMatrix[row][col] = matrix[maxRow - row][maxCol - col];
            }
        }

        // returning the inverted matrix
        return invertedMatrix;
    }

    /**
     * Returns a matrix of points centered around the given target point with the same domain as the 
     * kernel.
     * 
     * Zero-padding used in instances where the domain of the kernel extends past the image boundaries.
     * 
     * @param image The image for which the points are being gathered.
     * @param kernel The kernel the image points are being gathered for.
     * @param targetPointRow The row position of the target point.
     * @param targetPointColumn The column position of the target point.
     * 
     * //TODO this method doesnt work for non-odd sized kernels or empty kernels (because of the reach and loop becomes out of bounds).
     */
    private static float[][] getPointsInKernelDomain(FImage image, float[][] kernel, int targetPointRow, int targetPointCol){
        /**
         * Creating object to store the image points in the domain of 
         * the kernel.
         */

        // creating matrix to store image points
        float[][] imagePoints = new float[MyConvolution.getMatrixHeight(kernel)][MyConvolution.getMatrixWidth(kernel)];

        // counters to keep track of position in image point matrix
        int imagePointsRow = 0;
        int imagePointsCol = 0;

        /**
         * Defining start and end points for the image points in the domain
         * of the kernel.
         */

        // how much to reach left/right and up/down from target point (half size of kernel)
        int reachCol = Math.floorDiv(MyConvolution.getMatrixWidth(kernel), 2);
        int reachRow = Math.floorDiv(MyConvolution.getMatrixHeight(kernel), 2);

        // starting row and column positions in image
        int startingRow = targetPointRow - reachRow;
        int startingCol = targetPointCol - reachCol;

        // ending row and column positions in image
        int endingRow = targetPointRow + reachRow;
        int endingCol = targetPointCol + reachCol;

        /**
         * Gathering image points in domain of kernel.
         */

        // iterating through points surrounding target point
        for(int row = startingRow; row <= endingRow; row++){
            for(int col = startingCol; col <= endingCol; col++){
                try{
                    // getting point in image
                    float point = image.pixels[row][col];

                    // adding point to points matrix
                    imagePoints[imagePointsRow][imagePointsCol] = point;
                }
                catch(Exception e){
                    // point outside boundary of image - zero padding.
                    imagePoints[imagePointsRow][imagePointsCol] = 0;
                }

                // incrementing the column counter
                imagePointsCol++;
            }
            // incrementing row counter and resetting column counter
            imagePointsRow++;
            imagePointsCol=0;
        }

        // returninig the image points in the domain of the kernel
        return imagePoints;
    }

    /**
     * Calculates the weighted sum of two matricies that are of the same dimiensions.
     * 
     * That is, the sum of each element (i,j) in matrix A multiplied by the corresponding
     * element (i,j) in matrix B.
     * 
     * @param matrixA The first matrix with dimensions n x m.
     * @param matrixB The second matrix with dimensions n x m.
     * @return The weighted sum of the two matricies.
     * 
     * // TODO this method assumes both input matricies are of the same dimensions.
     */
    private static float calculateWeightedSum(float[][] matrixA, float[][] matrixB){
        // variable to store the total weighted sum
        float weightedSum = 0;

        /**
         * Iterating through points in both matricies and calculating weighted sum for each.
         */
        for(int row = 0; row < MyConvolution.getMatrixHeight(matrixA); row++){
            for(int col = 0; col < MyConvolution.getMatrixWidth(matrixA); col++){
                weightedSum += matrixA[row][col] * matrixB[row][col];
            }
        }

        // returning the weighted sum of the two matricies.
        return weightedSum;
    }

    /**
     * Returns the height of the matrix - i.e., the number of
     * rows in the matrix.
     * 
     * @param matrix The matrix for which the height is being gathered.
     * @return The height of the matrix - i.e., the number of rows in the matrix.
     */
    private static int getMatrixHeight(float[][] matrix){
        try{
            return matrix.length;
        }
        catch(Exception e){
            return 0;
        }
    }

    /**
     * Returns the width of the provided matrix - i.e., the number of
     * columns in the matrix.
     * 
     * @param matrix The matrix for which the width is being gathered.
     * @return The width of the matrix - the number of rows in the matrix.
     * 
     * // TODO this method assumes the matrix is uniform.
     */
    private static int getMatrixWidth(float[][] matrix){
        try{
            return matrix[0].length;
        }
        catch(Exception e){
            return 0;
        }
    }
}