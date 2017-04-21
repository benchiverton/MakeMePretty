import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import javax.imageio.ImageIO;

public class MakePretty {

	//The dimension of the images you're comparing to
	private int dim = 500;
	//div^2 = how many 'divisions' there will be in a photo
	private int div = 10;
	//The image that you will be 'changing'
	private BufferedImage img;
	//An array containing a numerical value for each pixel
	private int[][][] pxlArr = new int[dim][dim][3];
	//The number of images stored which we are 'comparing'
	private int numComps = 5;
	//An array containing each pixel value for each image you're 'comparing to'
	private int[][][] compArr;
	//An array containing the numeric values of the pixels of the resulting image.
	private int[][] result;
	
	//Changes the image to an array of pixels
	private void imgToPxlArr(){
		for( int i = 0; i < dim; i++ ){
		    for( int j = 0; j < dim; j++ ){
		    	Color c = new Color(img.getRGB(i, j));
		        pxlArr[i][j][0] = c.getRed();
		        pxlArr[i][j][1] = c.getBlue();
		        pxlArr[i][j][2] = c.getGreen();
		    }
		}
	}
	
	//loads an image as a BufferedImage
	private void loadImg(String imgName){
		try {
		    img = ImageIO.read(new File(imgName));
		} catch (IOException e) {
			System.out.println("error loading the image!");
		}
	}
	
	//n is the amount of images you're comparing to
	private void loadDefaults(){
		
		BufferedImage im = null;
		compArr = new int[numComps][dim][dim];
		
		for(int l=1; l<numComps+1; l++){
			try {
			    im = ImageIO.read(new File("C:/Users/benja/workspace/Profile/src/comp" + l + ".jpg"));
			} catch (IOException e) {
				System.out.println("error loading the image!");
				e.printStackTrace();
			}
			
			for( int i = 0; i < dim; i++){
			    for( int j = 0; j < dim; j++ ){
			        compArr[l-1][i][j] = im.getRGB( i, j );
			    }
			}
		}	
	}
	
	//compares the sector at x,y (starting at 0) and returns the value of the most 'suited' default
	private int compareSegment(int x, int y){
		
		BigInteger h = BigInteger.valueOf((long) Math.pow(Integer.MAX_VALUE,10));
		BigInteger q = BigInteger.valueOf(0);
		int choice = 0;
		for(int l=0; l<numComps; l++){
			for(int i=x*(dim/div); i<x*(dim/div) + (dim/div); i++){
				for(int j=y*(dim/div); j<y*(dim/div) + (dim/div); j++){
					Color c = new Color(compArr[l][i][j]);
					q = q.add(BigInteger.valueOf(Math.abs(pxlArr[i][j][0] - c.getRed())));
					q = q.add(BigInteger.valueOf(Math.abs(pxlArr[i][j][1] - c.getBlue())));
					q = q.add(BigInteger.valueOf(Math.abs(pxlArr[i][j][2] - c.getGreen())));
				}	
			}
			if(h == h.max(q)){
				choice = l;
				h = q;
			}
			q = BigInteger.valueOf(0);
		}
		
		return choice;
	}
	
	//updates the 'result' photo, one segment at a time
	private void updateResult(){	
		result = new int[dim][dim];
		for(int i=0; i<div; i++){
			for(int j=0; j<div; j++){
				int h = compareSegment(i, j);
				for(int a=i*(dim/div); a<i*(dim/div) + (dim/div); a++){
					for(int b=j*(dim/div); b<j*(dim/div) + (dim/div); b++){
						result[a][b] = compArr[h][a][b];
					}	
				}
			}			
		}
	}
	
	
	//turns the 'result' into an image and saves it as result.jpeg
	private void generateResult(){
		final int height = result.length;
		final int width = result[0].length;
		final BufferedImage image =
		    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; ++y) {
		    for (int x = 0; x < width; ++x) {
		        image.setRGB(x, y, result[x][y]);
		    }
		}
		File outputfile = new File("saved.png");
	    try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
		
		MakePretty p = new MakePretty();
		p.loadDefaults();
		
		//The path to the "photo.jpg" file on your system
		p.loadImg("C:/Users/benja/workspace/Profile/src/photo.jpg");
		p.imgToPxlArr();
		
		p.updateResult();
		
		p.generateResult();
		
	}
	
	
}
