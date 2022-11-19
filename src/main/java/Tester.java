package uk.ac.soton.ecs.cp6g18;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.FConvolution;
import org.openimaj.image.processing.convolution.Gaussian2D;

import java.io.File;
import java.util.Arrays;

/**
 * Tester class for project.
 */
public class Tester {

    /////////////////  
    // MAIN METHOD //
    /////////////////

    /**
     * Main method.
     * 
     * @param args System arguments.
     */
    public static void main( String[] args ) throws Exception {
        //Tester.testMyConvolution();

        Tester.testMyHybridImages();
    }

    ///////////////////
    // MYCONVOLUTION //
    ///////////////////

    /**
     * Tester method for MyConvolution class.
     */
    private static void testMyConvolution() throws Exception{

        ////////////////////
        // SAMPLE KERNELS //
        ////////////////////

        // column shift
        float[][] colShiftKernel = {
                                   {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                                   };

        // column shift
        float[][] rowShiftKernel = {
                                   {0}, {0}, {0}, {0},{0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {1}
                                   };

        // empty kernel
        float[][] emptyKernel = {{}};

        // single kernel
        float[][] singleKernel = {{1}};
    
        // non-square kernel
        float[][] nonSquareKernel = {
                                    {1,2,3}
                                    };

        // sobel
        float[][] sobelKernel = {
                                {2,2,0},
                                {2,0,-2},
                                {0,-2,-2}
                                };

        // mean average 3x3
        float[][] meanAverage3Kernel = {{0.11F,0.11F,0.11F},{0.11F,0.11F,0.11F},{0.11F,0.11F,0.11F}};

        // mean averaging 5x5
        float[][] meanAverage5Kernel = {
                                      {0.04F,0.04F,0.04F,0.04F,0.04F},
                                      {0.04F,0.04F,0.04F,0.04F,0.04F},
                                      {0.04F,0.04F,0.04F,0.04F,0.04F},
                                      {0.04F,0.04F,0.04F,0.04F,0.04F},
                                      {0.04F,0.04F,0.04F,0.04F,0.04F}
                                      };

        // gaussian averaging
        float[][] gaussianAveragingKernel = Gaussian2D.createKernelImage(3, 1.0F).pixels;

        // chosen kernel
        float[][] kernel = gaussianAveragingKernel;

        //////////////////
        // SAMPLE IMAGE //
        //////////////////

        // creating image object
        FImage image;

        // loading image from url
        //image = ImageUtilities.readF(new URL("https://www.aclens.com/u/media/2235/aclens-eyecolors-brown.jpg"));

        //loading image from pixel values
        float[][] pixelValues = {
                                 {0.1F,0.2F,0.3F,0.4F},
                                 {0.1F,0.2F,0.3F,0.4F},
                                 {0.1F,0.2F,0.3F,0.4F},
                                 {0.1F,0.2F,0.3F,0.4F},
                                };
        image = new FImage(pixelValues);

        //////////////////////////////////////
        // PROCESSING IMAGE WITH MY METHODS //
        //////////////////////////////////////

        // copy of sample image
        FImage myProcessedImage = image.clone();

        // my convolution instance
        MyConvolution myConvolution = new MyConvolution(kernel);

        // processing the sample image
        myConvolution.processImage(myProcessedImage);

        // displaying the raw and processed image
        DisplayUtilities.display(image, "Raw Image");
        DisplayUtilities.display(myProcessedImage, "My Processed Image");

        ///////////////////////////////////////////
        // PROCESSING IMAGE WITH LIBRARY METHODS //
        ///////////////////////////////////////////

        // cloning the raw image
        FImage libraryProcessedImage = image.clone();

        // performing convolution with the kernel
        FConvolution fConvolution = new FConvolution(kernel);
        fConvolution.processImage(libraryProcessedImage);

        // displaying the processed image
        DisplayUtilities.display(libraryProcessedImage, "FConvolution Processed Image");

        ///////////////////////
        // COMPARING RESULTS //
        ///////////////////////

        System.out.println("My Processed Values :");

        Tester.printMatrix(myProcessedImage.pixels);

        System.out.println("FConvolution Processed Values : ");

        Tester.printMatrix(libraryProcessedImage.pixels);

        System.out.println("Comparison Matrix : ");

        //Tester.printMatrix(Tester.compareImages(myProcessedImage, libraryProcessedImage));
    }

    /////////////////////
    // MYHYBRID IMAGES //
    /////////////////////

    /**
     * Tester method for MyHybridImages class.
     */
    private static void testMyHybridImages() throws Exception{

        //////////////////////
        // SAMPLE LOW IMAGE //
        //////////////////////

        // creating image object
        MBFImage lowImage;

        // loading image from url
        lowImage = ImageUtilities.readMBF(new File("src/main/resources/img/bj2.png"));

        ///////////////////////
        // SAMPLE HIGH IMAGE //
        ///////////////////////

        // creating image object
        MBFImage highImage;

        // loading image from url
        highImage = ImageUtilities.readMBF(new File("src/main/resources/img/dj2.png"));

        ///////////////////////////
        // CREATING HYBRID IMAGE //
        ///////////////////////////  

        MBFImage hybridImage = MyHybridImages.makeHybrid(lowImage, 8.0F, highImage, 5.0F);

        DisplayUtilities.display(hybridImage, "My Hybrid Image");
    }

    ////////////////////
    // HELPER METHODS //
    ////////////////////

    /**
     * Prints a matrix to the screen.
     * 
     * @param matrix The matrix being printed.
     */
    private static void printMatrix(boolean[][] matrix){
        for(boolean[] row : matrix){
            System.out.println(Arrays.toString(row));
        }
    }

    /**
     * Prints a matrix to the screen.
     * 
     * @param matrix The matrix being printed.
     */
    private static void printMatrix(float[][] matrix){
        for(float[] row : matrix){
            System.out.println(Arrays.toString(row));
        }
    }

    /**
     * Compares two images of the same size to determine if they have the same pixel values.
     * 
     * @param image1 The first image being compared.
     * @param image2 The second image being compared.
     * @return A table of boolean values representing if the pixel values within the two images
     * are the same.
     */
    private static boolean[][] compareImages(MBFImage image1, MBFImage image2){
        boolean[][] comparisonMatrix = new boolean[image1.getHeight()][image1.getWidth()];

        for(int row = 0; row < image1.getHeight(); row++){
            for(int col = 0; col < image1.getWidth(); col++){
                comparisonMatrix[row][col] = (image1.getPixel(col, row) == image2.getPixel(col, row));
            }
        }

        return comparisonMatrix;
    }
}

////////////
// IMAGES //
////////////

/**
 * Woman   - https://www.researchgate.net/profile/Cataldo-Guaragnella/publication/235406965/figure/fig1/AS:393405046771720@1470806480985/Original-image-256x256-pixels.png
 * Discord - https://www.freepnglogos.com/uploads/discord-logo-png/papirus-apps-iconset-papirus-development-team-discord-icon-14.png
 * Eye     - https://www.aclens.com/u/media/2235/aclens-eyecolors-brown.jpg
 * Cat     - http://comp3204.ecs.soton.ac.uk/cw/cat.jpg
 * Dog     - http://comp3204.ecs.soton.ac.uk/cw/dog.jpg
 * */ 