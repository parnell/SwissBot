package util;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import javax.media.jai.JAI;


/**
 * Class that is responsible for capturing and interpreting the screen.  
 * Also has various functions to help with color determination
 * @author parnell
 *
 */
public abstract class ScreenInterface {
	protected Robot robot = null;
	protected Rectangle board_rect = null;
	protected BufferedImage bi = null;

	public abstract Board getBoard();

	public ScreenInterface(){
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Capture the screen within the board rectangle
	 * @return
	 */
	public BufferedImage capture(){
		if (board_rect == null) return null;
		bi = robot.createScreenCapture(board_rect);
		return bi;
	}
	
	/**
	 * Find the game board from images of the top-left and bottom-right corners
	 * @param top_left_file
	 * @param bottom_right_file
	 * @return
	 */
	public Rectangle findBoard(String top_left_file, String bottom_right_file){

	    /// Get Screen Pixels
	    BufferedImage bi = robot.createScreenCapture(
		           new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()) );
		
	    /// Get top-left and bottom-right images and pixels
		RenderedImage tl_ri = JAI.create("fileload", top_left_file);
		RenderedImage br_ri = JAI.create("fileload", bottom_right_file);

		return findBoard(bi,tl_ri,br_ri);
	}

	/**
	 * Find the boundary rectangle of the gameboard from the top-left and bottom-right corner images
	 * @param image
	 * @param tl_ri
	 * @param br_ri
	 * @return
	 */
	public Rectangle findBoard(RenderedImage image, RenderedImage tl_ri, RenderedImage br_ri){
	    Rectangle tl = null ,br = null;

	    final int[] pix = new int[image.getWidth()* image.getHeight() * 3];
	    image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), pix);

		final int[] tl_pix = new int[tl_ri.getWidth() * tl_ri.getHeight() * 3];
		tl_ri.getData().getPixels(0, 0,tl_ri.getWidth(), tl_ri.getHeight(), tl_pix);
		
		final int[] br_pix = new int[br_ri.getWidth() * br_ri.getHeight() * 3];
		br_ri.getData().getPixels(0, 0,br_ri.getWidth(), br_ri.getHeight(), br_pix);


		/// Compare pixels and find locations of the game board
		for (int i =0; i < pix.length ;i+=3){
			int x = (i/3) % image.getWidth();
			int y = (i/3) / image.getWidth();
			/// Compare top-left pix
			if (pix[i] == tl_pix[0] && pix[i+1] == tl_pix[1] && pix[i+2] == tl_pix[2] &&
					(x + tl_ri.getWidth() < image.getWidth() && y + tl_ri.getHeight() < image.getHeight())){				 
				Raster tmp = image.getData(new Rectangle(x, y, tl_ri.getWidth(), tl_ri.getHeight()));
				final int[] tmppix = new int[tmp.getWidth()*tmp.getHeight() * 3];
			    tmp.getPixels(x, y, tmp.getWidth(), tmp.getHeight(), tmppix);
				if (compareArray(tmppix, tl_pix)){
					tl = new Rectangle(x,y,tl_ri.getWidth(),tl_ri.getHeight());}
			}
			
			/// Compare bottom-right pix
			if (pix[i] == br_pix[0] && pix[i+1] == br_pix[1] && pix[i+2] == br_pix[2] &&
					(x + br_ri.getWidth() < image.getWidth() && y + br_ri.getHeight() < image.getHeight())){				 
				Raster tmp = image.getData(new Rectangle(x, y, br_ri.getWidth(), br_ri.getHeight()));
				final int[] tmppix = new int[tmp.getWidth()*tmp.getHeight() * 3];
			    tmp.getPixels(x, y, tmp.getWidth(), tmp.getHeight(), tmppix);
				if (compareArray(tmppix, br_pix)){
					br = new Rectangle(x,y,br_ri.getWidth(),br_ri.getHeight());}
			}
		}
		
		if (tl == null || br == null){
			board_rect = null;
		} else {
			int width = (int) (br.getCenterX() - tl.getCenterX());
			int height = (int) (br.getCenterY() - tl.getCenterY());
			if (br.getX() < tl.getX() || br.getY() < tl.getY()) return null;
			board_rect = new Rectangle( (int)tl.getCenterX(), (int)tl.getCenterY(), width,height );
		}
		return board_rect;
	}

	
	/**
	 * Get the Hue, Saturation, and Lightness
	 * @param r
	 * @param g
	 * @param b
	 * @param hsl
	 */
	public static void getHsl( float r,  float g,  float b,float[] hsl) {
		r /= 255; g /=255; b /=255;
		final float max = Math.max(Math.max(r, g), b);
		final float min = Math.min(Math.min(r, g), b); 
//		double hsl[] = new double[3];
		if (max==min){ hsl[0] = 0;}
		else if (max == r){ hsl[0] = (60 * (g-b)/(max-min) + 360) % 360; }
		else if (max == g){ hsl[0] = (60 * (b-r)/(max-min) + 120);}
		else if (max == b){ hsl[0] = (60 * (r-g)/(max-min) + 240);}
		
		hsl[2] = (max + min)/2;

		if (max == min) { hsl[1] = 0;}
		else if (hsl[2] <= 0.5){ hsl[1] = (max-min)/(2*hsl[2]);}
		else if (hsl[2] > 0.5){ hsl[1] = (max-min)/(2-2*hsl[2]);}		
	}
	
	

	/**
	 * Compare arrays
	 * @param ar1
	 * @param ar2
	 * @return
	 */
	private boolean compareArray(int[] ar1, int[] ar2) {
		for (int i=0;i< ar1.length;i++){
			if (ar1[i] != ar2[i]) return false;
		}
		return true;
	}

	/**
	 * 
	 * @param h
	 * @param s
	 * @param l
	 * @return
	 */
	public static Color getBasicColor(final double h,final double s,final double l){
//		System.out.println(h + "," + s + "," + l + "   ");

		if (h < 10 && s < 0.3 && l > 0.45) return Color.white;
		else if (h < 10 && s < 0.3 && l < 0.45) return Color.black;
		else if (s < 0.6 && l < 0.6) return new Color(-1,-1,-1);
		else if (h <= 14) {
			return Color.red; 
		}
		else if (h > 14 && h <= 43 ){
			return Color.orange;
		}
		else if (h > 43 && h <= 60 ){
			return Color.yellow;
		}
		else if (h > 60 && h <= 150) {
			return Color.green;
		}
		else if (h > 150 && h <= 245) {
			return Color.blue; 
		}
		else if (h > 245 && h <= 340) {
			return Color.magenta;
		}
		else if (h > 340 && l >0.3) {
			return Color.red;
}
		return new Color(-1,-1,-1);
	}
}
