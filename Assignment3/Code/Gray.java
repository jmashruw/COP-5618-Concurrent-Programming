package cop5618;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Arrays;

public class Gray {

	public static String[] labels = { "getRGB", "stream processing", "setRGB" };

	public static Timer gray_SS(BufferedImage image, BufferedImage newImage) {
		Timer time = new Timer(labels);
		ColorModel colorModel = ColorModel.getRGBdefault();
		int w = image.getWidth();
		int h = image.getHeight();
		time.now();
		int[] sourcePixelArray = image.getRGB(0, 0, w, h, new int[w * h], 0, w);
		time.now();
		int[] grayPixelArray =
				Arrays.stream(sourcePixelArray)
						.map(pixel -> (int) ((colorModel.getRed(pixel) * .299) + (colorModel.getGreen(pixel) * .587)
								+ (colorModel.getBlue(pixel) * .114)))
						.map(grayVal -> HW3Utils.makeRGBPixel(grayVal, grayVal, grayVal))
						.toArray();
		time.now();

		newImage.setRGB(0, 0, w, h, grayPixelArray, 0, w);
		time.now();
		return time;
	}
	public static Timer gray_SS_FJ(FJBufferedImage image, FJBufferedImage newImage) {

		Timer t = new Timer(labels);

		ColorModel colorModel = ColorModel.getRGBdefault();
		int w = image.getWidth();
		int h = image.getHeight();

		t.now();
		int[] sourcePixelArray = new int[w*h];
		image.getRGB(0, 0, w, h, sourcePixelArray, 0, w);

		t.now();

		int[] grayPixelArray =
 				Arrays.stream(sourcePixelArray)
						.map(pixel -> (int) ((colorModel.getRed(pixel) * .299) + (colorModel.getGreen(pixel) * .587)
								+ (colorModel.getBlue(pixel) * .114)))
						.map(grayVal -> HW3Utils.makeRGBPixel(grayVal, grayVal, grayVal))
						.toArray();

		t.now();

		newImage.setRGB(0, 0, w, h, grayPixelArray, 0, w);

		t.now();
		return t;
	}

	public static Timer gray_PS_FJ(FJBufferedImage image, FJBufferedImage newImage) {

		Timer time = new Timer(labels);

		ColorModel colorModel = ColorModel.getRGBdefault();
		int w = image.getWidth();
		int h = image.getHeight();

		time.now();

		int[] sourcePixelArray = new int[w * h];
		image.getRGB(0, 0, w, h, sourcePixelArray, 0, w);

		time.now();

		int[] grayPixelArray =
				Arrays.stream(sourcePixelArray).parallel()
						.map(pixel -> (int) ((colorModel.getRed(pixel) * .299) + (colorModel.getGreen(pixel) * .587)
								+ (colorModel.getBlue(pixel) * .114)))
						.map(grayVal -> HW3Utils.makeRGBPixel(grayVal, grayVal, grayVal))
						.toArray();

		time.now();

		newImage.setRGB(0, 0, w, h, grayPixelArray, 0, w);

		time.now();
		return time;
	}

	public static Timer gray_PS(BufferedImage image, BufferedImage newImage) {

		Timer time = new Timer(labels);

		ColorModel colorModel = ColorModel.getRGBdefault();
		int w = image.getWidth();
		int h = image.getHeight();

		time.now();

		int[] sourcePixelArray = image.getRGB(0, 0, w, h, new int[w * h], 0, w);

		time.now();

		int[] grayPixelArray =
				Arrays.stream(sourcePixelArray).parallel()
						.map(pixel -> (int) ((colorModel.getRed(pixel) * .299) + (colorModel.getGreen(pixel) * .587)
								+ (colorModel.getBlue(pixel) * .114)))
						.map(grayVal -> HW3Utils.makeRGBPixel(grayVal, grayVal, grayVal))
						.toArray();

		time.now();

		newImage.setRGB(0, 0, w, h, grayPixelArray, 0, w);

		time.now();
		return time;
	}

}
