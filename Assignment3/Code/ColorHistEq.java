package cop5618;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;

class MyIntOperator implements BinaryOperator<Object> {
	@Override
	public Object apply(Object left, Object right) {
		return ((long) (left) + (long) right);
	}
}

public class ColorHistEq {


	static String[] labels = { "getRGB", "convert to HSB", "create brightness map", "probability array",
			"parallel prefix", "equalize pixels", "setRGB" };

	static Timer colorHistEq_serial(BufferedImage image, BufferedImage newImage) {
		Timer times = new Timer(labels);

		ColorModel colorModel = ColorModel.getRGBdefault();

		int w = image.getWidth();
		int h = image.getHeight();
		times.now();
		int[] sourcePixelArray =  new int[w*h];
		image.getRGB(0, 0, w, h, sourcePixelArray, 0, w);
		times.now();

		int numOfBins = 256;

		Object[] hsbObjectStream = Arrays.stream(sourcePixelArray).mapToObj(
				pixel -> Color.RGBtoHSB(colorModel.getRed(pixel), colorModel.getGreen(pixel), colorModel.getBlue(pixel),
						new float[3]))
				.collect(groupingBy(brightness -> (int) (numOfBins * (float) Array.get(brightness, 2)),
						Collectors.counting()))
				.values()
				.toArray();

		Arrays.parallelPrefix(hsbObjectStream, new MyIntOperator());

		double [] cumulativeProbability = Arrays.stream(hsbObjectStream).mapToDouble(
				e ->  ((long) e / (double) (w * h)))
				.toArray();

		int[] newRGBArray = Arrays.stream(sourcePixelArray).mapToObj(
				pixel -> Color.RGBtoHSB(colorModel.getRed(pixel), colorModel.getGreen(pixel), colorModel.getBlue(pixel),
						new float[3]))
				.mapToInt( conv_pixel -> Color.HSBtoRGB((float) Array.get(conv_pixel,0),
						(float) Array.get(conv_pixel,1),
						(float) cumulativeProbability[(int)(numOfBins * (float) Array.get(conv_pixel,2))])).toArray();

		newImage.setRGB(0, 0, w, h, newRGBArray, 0, w);
		times.now();

		return times;
	}

	static Timer colorHistEq_parallel(FJBufferedImage image, FJBufferedImage newImage) {
		Timer times = new Timer(labels);

		ColorModel colorModel = ColorModel.getRGBdefault();

		int w = image.getWidth();
		int h = image.getHeight();
		times.now();
		int[] sourcePixelArray =  new int[w*h];
		image.getRGB(0, 0, w, h, sourcePixelArray, 0, w);
		times.now();

		int numOfBins = 256;

		Object[] hsbObjectStream = Arrays.stream(sourcePixelArray)
				.parallel()
				.mapToObj(
						pixel -> Color.RGBtoHSB(colorModel.getRed(pixel), colorModel.getGreen(pixel), colorModel.getBlue(pixel),
								new float[3]))
				.collect(groupingBy(brightness -> (int) (numOfBins * (float) Array.get(brightness, 2)),
						Collectors.counting()))
				.values()
				.toArray();

		Arrays.parallelPrefix(hsbObjectStream, new MyIntOperator());

		double [] cumulativeProbability = Arrays.stream(hsbObjectStream)
				.parallel()
				.mapToDouble(
						e ->  ((long) e / (double) (w * h)))
				.toArray();

		int[] newRGBArray = Arrays.stream(sourcePixelArray)
				.parallel()
				.mapToObj(
						pixel -> Color.RGBtoHSB(colorModel.getRed(pixel), colorModel.getGreen(pixel), colorModel.getBlue(pixel),
								new float[3]))
				.mapToInt( conv_pixel -> Color.HSBtoRGB((float) Array.get(conv_pixel,0),
						(float) Array.get(conv_pixel,1),
						(float) cumulativeProbability[(int)(numOfBins * (float) Array.get(conv_pixel,2))])).toArray();

		newImage.setRGB(0, 0, w, h, newRGBArray, 0, w);
		times.now();

		return times;
	}
}