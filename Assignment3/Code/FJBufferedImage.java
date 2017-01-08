package cop5618;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class FJBufferedImage extends BufferedImage {

	private class Setter extends RecursiveAction{

		int xCordinate;
		int yCordinate;
		int width;
		int height;
		int[] arrRGB;
		int displacement;
		int scanSize;
		public volatile ForkJoinPool fjPool = new ForkJoinPool();

		public Setter(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize){
			xCordinate = xStart;
			yCordinate = yStart;
			width = w;
			height = h;
			arrRGB = rgbArray;
			displacement = offset;
			scanSize = scansize;
		}

		protected void compute(){
			if( height < 3 )
				FJBufferedImage.super.setRGB(xCordinate, yCordinate, width, height, arrRGB, displacement, scanSize);
			else{
				ForkJoinPool.commonPool().invoke(new Setter(xCordinate, yCordinate, width, height/3, arrRGB, displacement, scanSize));
				ForkJoinPool.commonPool().invoke(new Setter(xCordinate, yCordinate + (height/3), width, height/3, arrRGB, displacement + ((height/3)*scanSize), scanSize));
				ForkJoinPool.commonPool().invoke(new Setter(xCordinate, yCordinate + (height/3)*2, width, height-(2*(height/3)), arrRGB, displacement + ((height/3)*scanSize*2), scanSize));
			}
		}
	}

	private class Getter extends RecursiveAction {

		int xCordinate;
		int yCordinate;
		int width;
		int height;
		int[] arrRGB;
		int displacement;
		int scanSize;
		public volatile ForkJoinPool fjPool = new ForkJoinPool();

		public Getter(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize) {
			xCordinate = xStart;
			yCordinate = yStart;
			width = w;
			height = h;
			arrRGB = rgbArray;
			displacement = offset;
			scanSize = scansize;
		}

		protected void compute(){
			if( height < 3 )
				FJBufferedImage.super.getRGB(xCordinate, yCordinate, width, height, arrRGB, displacement, scanSize);
			else{
				ForkJoinPool.commonPool().invoke(new Getter(xCordinate, yCordinate, width, height/3, arrRGB, displacement, scanSize));
				ForkJoinPool.commonPool().invoke(new Getter(xCordinate, yCordinate + (height/3), width, height/3, arrRGB, displacement + ((height/3)*scanSize), scanSize));
				ForkJoinPool.commonPool().invoke(new Getter(xCordinate, yCordinate + (height/3)*2, width, height-(2*(height/3)), arrRGB, displacement + ((height/3)*scanSize*2), scanSize));
			}
		}
	}
	
	public FJBufferedImage(int width, int height, int imageType) {
		super(width, height, imageType);
	}

	public FJBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
		super(width, height, imageType, cm);
	}

	public FJBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied,
			Hashtable<?, ?> properties) {
		super(cm, raster, isRasterPremultiplied, properties);
	}

	public static FJBufferedImage BufferedImageToFJBufferedImage(BufferedImage source){
	       Hashtable<String,Object> properties=null; 
	       String[] propertyNames = source.getPropertyNames();
	       if (propertyNames != null) {
	    	   properties = new Hashtable<String,Object>();
	    	   for (String name: propertyNames){properties.put(name, source.getProperty(name));}
	    	   }
	 	   return new FJBufferedImage(source.getColorModel(), source.getRaster(), source.isAlphaPremultiplied(), properties);		
	}
	
	@Override
	public void setRGB(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize){
        new Setter(xStart, yStart, w, h, rgbArray, offset, scansize).compute();

	}

	@Override
	public int[] getRGB(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize){
	       new Getter(xStart, yStart, w, h, rgbArray, offset, scansize).compute();
		return null;
	}
}