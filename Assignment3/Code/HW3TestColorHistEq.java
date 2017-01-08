package cop5618;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

public class HW3TestColorHistEq {

	static BufferedImage sourceImage;
	static String sourceImageFilename;
	static int WARMUPREPS = 3; // default value may be changed with command line
								// vm argument
	static int REPS = 3; // default value may be changed with command line vm
							// argument
	static double meanSerialDuration;
	static BufferedImage serialSolution;

	/**
	 * Reads source image and values from System properties
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// read source image
		// The system property is set by command line vm argument
		// Example:
		sourceImageFilename = System.getProperty("sourceImageFilename");
		if (sourceImageFilename == null)
			System.out.println(
					"Provide image filename as system property on command line:  -DsourceImageFilename=\"C:\\Path\\to\\Pictures\\hw3photo.jpg\"");
		System.out.println("Opening image " + sourceImageFilename);
		File sourceFile = new File(sourceImageFilename);
		sourceImage = ImageIO.read(sourceFile);
		String warmupReps = System.getProperty("warmupReps");
		WARMUPREPS = warmupReps != null ? Integer.parseInt(System.getProperty("warmupReps")) : WARMUPREPS;
		String reps = System.getProperty("reps");
		REPS = reps != null ? Integer.parseInt(System.getProperty("reps")) : REPS;		
		BufferedImage source = sourceImage;
		BufferedImage newImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		ColorHistEq.colorHistEq_serial(source, newImage);
		serialSolution = newImage;
	}

	
	/**
	 * Adds the given prefix to the given filename.
	 * Example:  
	 * old is "C:\Path\to\Pictures\hw3photo.jpg"
	 * prefix is "new_"
	 * returned String is "C:\Path\to\Pictures\new_hw3photo.jpg"
	 * 
	 * @param old   original filename
	 * @param prefix  prefix
	 * @return  string with modified filename
	 */
	String addPrefixToFileName(String old, String prefix) {
		int index = old.lastIndexOf(System.getProperty("file.separator")) + 1;
		return old.substring(0, index) + prefix + old.substring(index);
	}
	
	
	
	/**
	 * Test method for
	 * {@link cop5618.ColorHistEq#clorHistEq_SS(java.awt.image.BufferedImage, java.awt.image.BufferedImage)}
	 * 
	 * This is the serial test case.  
	 * 
	 * @throws IOException
	 */
	@Test
	public void testColorHistEq_serial() throws IOException {
		System.out.println("****Running test case colorHistEq_SS****");
		String filename = addPrefixToFileName(sourceImageFilename, "colorHistEq_ss_");

		File output = new File(filename);
		BufferedImage source = sourceImage;
		BufferedImage newImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		for (int rep = 0; rep < WARMUPREPS; rep++) {
			Timer timerData = ColorHistEq.colorHistEq_serial(source, newImage);
//			System.out.println(timerData.toStringNanos());
		}
		Timer[] timers = new Timer[REPS];
		//System.out.println("**starting runs included in statistics**");
		for (int rep = 0; rep < REPS; rep++) {
			timers[rep] = ColorHistEq.colorHistEq_serial(source, newImage);
//			System.out.println(timers[rep].toString());
		}
		//write the last one
		System.out.println("writing file " + filename);
		ImageIO.write(newImage, "jpg", output);


		// print stats
		System.out.println("printing stats for colorHistEq_SS");
		double[] meanDuration = new double[1]; //passed as one-element array so it can be set in statsToString.
		System.out.println(Timer.statsToString(timers, meanDuration));
		meanSerialDuration= meanDuration[0];
		serialSolution = newImage;
	}
	




	/**
	 * Test method for
	 * {@link cop5618.ColorHistEq#ColorHistEq_PS_FJ(cop5618.FJBufferedImage, cop5618.FJBufferedImage)}
	 * .
	 * This test invokes Color Histogram  with parallel getRGB and setRGB and parallel stream processing.
	 * The speedup is compared to the duration from testColorHistEq_SS, or 0 if that test case has not run.
	 * @throws IOException 
	 */
	@Test
	public void testColorHistEq_parallel() throws IOException {
		System.out.println("****Running test case ColorHistEq_PS_FJ****");
		String filename = addPrefixToFileName(sourceImageFilename, "ColorHistEq_parallel_");
		FJBufferedImage source = FJBufferedImage.BufferedImageToFJBufferedImage(sourceImage);
		FJBufferedImage newImage = new FJBufferedImage(source.getWidth(), source.getHeight(), source.getType());
		for (int rep = 0; rep < WARMUPREPS; rep++) {
			Timer timerData = ColorHistEq.colorHistEq_parallel(source, newImage);
//			System.out.println(timerData.toString());
		}
		Timer[] timers = new Timer[REPS];
		//System.out.println("**starting runs included in statistics**");
		for (int rep = 0; rep < REPS; rep++) {
			timers[rep] = ColorHistEq.colorHistEq_parallel(source, newImage);
//			System.out.println(timers[rep].toString());
		}
		//write the last one
		System.out.println("writing file " + filename);
		File output = new File(filename);
		ImageIO.write(newImage, "jpg", output);
		// print stats
		System.out.println("printing stats for ColorHistEq_parallel");
		System.out.println(Timer.statsToString(timers,meanSerialDuration));
		assertTrue(HW3Utils.equals(serialSolution, newImage));
	}

	
	/**
	 * Allows the test cases to be invoked as an application in a controlled order.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		setUpBeforeClass();
		HW3TestColorHistEq test = new HW3TestColorHistEq();
		test.testColorHistEq_serial();
		test.testColorHistEq_parallel();
	}


}
