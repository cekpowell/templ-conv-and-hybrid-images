package uk.ac.soton.ecs.cp6g18;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.Gaussian2D;

/**
 * COMP3204: Computer Vision
 * 
 * Coursework 2: Exercise 2
 * 
 * Combining two images together to create a hybrid image.
 * 
 * @author Charles Powell
 */
public class MyHybridImages {

    /////////////////////////
    // MAKING HYBRID IMAGE //
    /////////////////////////

    /**
	 * Computes a hybrid image combining low-pass and high-pass filtered images
	 *
	 * @param lowImage The image to which apply the low pass filter
	 * @param lowSigma The standard deviation of the low-pass filter
	 * @param highImage The image to which apply the high pass filter
	 * @param highSigma The standard deviation of the low-pass component of computing the
	 * high-pass filtered image
	 * @return The computed hybrid image
     * 
     * // TODO this method assumes both input images are the same size
	 */
	public static MBFImage makeHybrid(MBFImage lowImage, float lowSigma, MBFImage highImage, float highSigma) {
        /**
         * Calculate low-pass version of low-image
         */
        MBFImage lowPassImage = MyHybridImages.getLowPassVersion(lowImage, lowSigma);

        /**
         * Calculate high-pass version of high-image
         */
        MBFImage highPassImage = MyHybridImages.getHighPassVersion(highImage, highSigma);

        /**
         * Combine low- and high-pass images together to create hybrid image.
         */
        MBFImage hybridImage = lowPassImage.add(highPassImage);

        // returning the hybrid image
        return hybridImage;
	}

    /**
     * Returns a low-pass version of the image. The low pass version of the image
     * is the image with all of the high-frequencies removed.
     * 
     * @param image The image for which the low pass version is being gathered.
     * @param sigma The sigma value used in the Gaussian filtering.
     * @return A low pass version of the image.
     */
    private static MBFImage getLowPassVersion(MBFImage image, float sigma){

        // creating a copy of the image (to become low pass version)
        MBFImage lowPassImage = image.clone();
        
        /**
         * Creating Gaussian Filter.
         */

        // determining size of filter
        int size = (int) (8.0f * sigma + 1.0f); // (this implies the window is +/- 4 sigmas from the centre of the Gaussian)
        if (size % 2 == 0) size++; // size must be odd

        // creating the gaussian filter
        FImage filter = Gaussian2D.createKernelImage(size, sigma);

        /**
         * Applying Gaussian filter to image.
         */

        // creating image processor
        MyConvolution myConvolution = new MyConvolution(filter.pixels);

        // applying image processor
        lowPassImage.processInplace(myConvolution);

        // returning the low pass version of the image.
        return lowPassImage;
    }

    /**
     * Returns a high-pass version of the image. The high-=pass version of the image
     * is the image with all of the low-frequencies removed.
     * 
     * @param image The image for which the high-pass version is being gathered.
     * @param sigma The sigma value used in the Gaussian filtering.
     * @return A high-pass version of the image.
     */
    private static MBFImage getHighPassVersion(MBFImage image, float sigma){

        // creating a copy of the image (to become low pass version)
        MBFImage highPassImage = image.clone();

        // getting low pass version of image
        MBFImage lowPassImage = MyHybridImages.getLowPassVersion(image, sigma);
        
        /**
         * Calculating difference between normal image and low-pass version.
         */
        highPassImage = image.subtract(lowPassImage);

        /**
         * Note: To display the high-pass version, the pixel values must be normalised by
         * adding 0.5 to all - to account for the fact that some are negative and some are 
         * positive due to the subtraction (remember pixel value are 0 to 1).
         */

        // returning the high-pass version of the image.
        return highPassImage;
    }

    ////////////////////
    // HELPER METHODS //
    ////////////////////

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