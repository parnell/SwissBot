package util;

import game.GamePlayer;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.FileWriter;
import java.io.IOException;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;

import bejeweled.BBConfiguration;
import bejeweled.BBScreen;

public class TestMaker extends BBScreen{
	private static final boolean debug = false;
	private static final int NNORMALSETS = 54;
	private static final int NCONFUSINGSETS = 30;
		
	
	public void makeTests(){

		FileWriter fw = null;
		RenderedImage lq_tl_ri = JAI.create("fileload", BBConfiguration.lq_top_left_file);
		RenderedImage lq_br_ri = JAI.create("fileload", BBConfiguration.lq_bottom_right_file);
		RenderedImage hq_tl_ri = JAI.create("fileload", BBConfiguration.hq_top_left_file);
		RenderedImage hq_br_ri = JAI.create("fileload", BBConfiguration.hq_bottom_right_file);

		for (int i =1; i <= NNORMALSETS;i++ ){
			fw = make(fw, lq_tl_ri, lq_br_ri, hq_tl_ri, hq_br_ri, i, "normal");
		}
		for (int i =1; i <= NCONFUSINGSETS;i++ ){
			fw = make(fw, lq_tl_ri, lq_br_ri, hq_tl_ri, hq_br_ri, i, "confusing");
		}
	}

	private FileWriter make(FileWriter fw, RenderedImage lq_tl_ri,
			RenderedImage lq_br_ri, RenderedImage hq_tl_ri,
			RenderedImage hq_br_ri, int i, String dir) {
		Rectangle boardrect;
		try {fw = new FileWriter("/Users/parnell/workspace/bb/" + dir + "/" + i + "_data1.txt", false);} 
		catch (IOException e) {e.printStackTrace();}
		
		///Open image
		RenderedImage image = JAI.create("fileload", "/Users/parnell/workspace/bb/" + dir + "/" +i + ".png");
		
		/// Find board rect within image
		boardrect = findBoard(image,lq_tl_ri,lq_br_ri);
		if (boardrect == null) boardrect = findBoard(image,hq_tl_ri,hq_br_ri);
		if (boardrect == null){
			System.err.println("Couldn't make test set for " + dir + "  " + i + ".png");
			return null;
		}
		
		/// adjust params
		calculateCellSize(boardrect,0);

		/// Make the test set
		Raster rast = image.getData(boardrect);
//			rast = rast.createCompatibleWritableRaster(0, 0, rast.getWidth(), rast.getHeight());
		makeTestSet(rast,fw);
		
		try { fw.close();} catch (IOException e) {e.printStackTrace();}
		return fw;
	}
	
	public static void makeGuess(ScreenInterface screen) {
		FileWriter fw = null;
		try {fw = new FileWriter("/Users/parnell/workspace/bb/guess.txt", false);} 
		catch (IOException e) {e.printStackTrace();}

		if (screen.findBoard(BBConfiguration.lq_top_left_file, BBConfiguration.lq_bottom_right_file) != null ||
				screen.findBoard(BBConfiguration.hq_top_left_file, BBConfiguration.hq_bottom_right_file) != null ){
			screen.capture();
			Board board = screen.getBoard();
			for (int r =0;r < board.getWidth();r++){
				for (int c=0; c < board.getHeight();c++){
					try { fw.write(board.getBlock(r,c)+"\n");} catch (IOException e) {e.printStackTrace();}
				}
			}
		}		
		try { fw.close();} catch (IOException e) {e.printStackTrace();}

	}

	public void makeTestSet(Raster rast, FileWriter fw) {		
		GamePlayer.startTime();
		for (int r = 0; r < NCELLS; r++){
			for (int c = 0; c < NCELLS; c++){
//				String sb = getBasicCC(rast, r, c);
//				String sb = getCC(rast, r, c);
//				String sb = getCCGrid(rast,r,c);
//				String sb = getCCInsideOutside(rast,r,c);
//				String sb = getCCInsideOutsideandHSL(rast,r,c);
				
//				String sb = getAvgHSL(rast,r,c);
				
//				String sb = getAvgHSLandCC(rast,r,c);
				String sb = getHSLHistogram(rast,r,c);
				try { fw.write(sb);} catch (IOException e) {e.printStackTrace();}
			}
			if (debug) System.out.println("");
		}
		GamePlayer.endTime("made test");
		if (debug) System.out.println("--------------------\n");
	}

	// ~20 ms, 97.6 % accurate, 95.1% confusing
	// small box ~10ms, 95.37% confusing 
	// small box ~10ms, 96.6%, 95.86% confusing, no black
	// big box, ~18ms, 96.2% confusing, no black
	// small box colors, big box HSL, ~20ms, 96.28% confusing, no black, I think best so far
	//big box colors, small box HSL, ~15ms, 97.12% , 96.00% confusing, no black
	//strangely big box colors does poorly at indentifying blues and purple but good at discovering white special
//	@SuppressWarnings("unused")
//	private String getAvgHSLandCC(Raster rast, int r, int c) {
//		int nattrs = 1;
//		StringBuffer sb = new StringBuffer();
//		Rectangle rect = new Rectangle(cells[r][c]);
//		rect.grow(-5, -5);
//		rect.x += rast.getMinX();
//		rect.y += rast.getMinY();
//
//		int[] center_cc = new int[22];
//		getColorCounts(
//				rect.x, rect.y,(int) rect.getWidth(),(int) rect.getHeight(),
//				rast, center_cc);
//
//		for (int k=1;k<center_cc.length;k++){ 
//			sb.append(nattrs++ + ":" + center_cc[k] + " ");}
//		
//		rect = new Rectangle(cells[r][c]);
//		rect.x += rast.getMinX();
//		rect.y += rast.getMinY();
//		
//		Color cr = getAvgColor(rect.x, rect.y, (int) rect.getWidth(), (int) rect.getHeight(), rast);
//
//		float[] hsl = new float[3];
//		getHsl(cr.getRed(), cr.getGreen(), cr.getBlue(), hsl);
//		for (int i = 0; i < hsl.length;i++){
//			sb.append(nattrs++ + ":" + hsl[i] + " ");}
//
//		sb.append("\n");
//		return sb.toString();
//	}

	// <5 ms, 93.3% accurate
	@SuppressWarnings("unused")
	private String getAvgHSL(Raster rast, int r, int c){
		Rectangle rect = new Rectangle(cells[r][c]);
		rect.x += rast.getMinX();
		rect.y += rast.getMinY();
		Color cr = getAvgColor(rect.x, rect.y, (int) rect.getWidth(), (int) rect.getHeight(), rast);
		float[] hsl = new float[3];
		getHsl(cr.getRed(), cr.getGreen(), cr.getBlue(), hsl);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hsl.length;i++){
			sb.append(i+1 + ":" + hsl[i] + " ");}
		sb.append("\n");
		return sb.toString();
	}
	
	// large box, 96.7% accurate, 94.3% confusing
//	@SuppressWarnings("unused")
//	private String getCC(Raster rast, int r, int c) {
//		Rectangle rect = new Rectangle(cells[r][c]);
//		rect.x += rast.getMinX();
//		rect.y += rast.getMinY();
//		
//		int[] center_cc = new int[22];
//		getColorCounts(
//				rect.x, rect.y,(int) rect.getWidth(),(int) rect.getHeight(),
//				rast, center_cc);
//
//		StringBuffer sb = new StringBuffer();
//		for (int k=0;k<center_cc.length;k++){ 
//			sb.append(k+1 + ":" + center_cc[k] + " ");}
//		sb.append("\n");
//		return sb.toString();
//	}
//
//	// nr=nc=4.  96.8% accurate
//	// nr=nc=8. ~96.6%
//	// nr=nc=4.  basic_colors , 93.8% 
//	// nr=nc=8.  basic_colors , 
//	@SuppressWarnings("unused")
//	private String getCCGrid(Raster rast, int r, int c) {
//		int nattr = 1;
//		StringBuffer sb = new StringBuffer();
//		final int nr = 8;
//		final int nc = 8;
//		Rectangle rect = cells[r][c];
//		Rectangle[][] rects = divideRect(rect,nr,nc);
//
//		for (int rr = 0; rr < nr;rr++){
//			for (int cc = 0;cc<nc;cc++){
//				rect = rects[rr][cc];
//				rect.x += rast.getMinX();
//				rect.y += rast.getMinY();
//
//				int[] center_cc = new int[22];
//				getColorCounts(
//						rect.x, rect.y,(int) rect.getWidth(),(int) rect.getHeight(),
//						rast, center_cc);
//
//				for (int k=0;k<center_cc.length;k++){ 
//					sb.append(nattr++ + ":" + center_cc[k] + " ");}				
//			}
//		}
//
//		sb.append("\n");
//		return sb.toString();
//	}
	
	/// confusing 95.91%
	/// confusing 98.8935%
	private String getHSLHistogram(Raster rast, int r, int c){
		Rectangle rect = new Rectangle(cells[r][c]);
		rect.x += rast.getMinX();
		rect.y += rast.getMinY();
		Histogram hist = BBScreen.getHistogram(rect.x, rect.y, (int) rect.getWidth(), (int) rect.getHeight(), rast);
		int[][] bins = hist.getBins();
		StringBuffer sb = new StringBuffer();
		int loc=1;
		for (int i=0; i < bins.length; i++) {
			for (int j=0;j < bins[i].length;j++){
				sb.append(loc++ + ":" + bins[i][j] +" ");
//				System.out.println("bins[][]=" + bins[i][j]);
			}
	     }

		sb.append("\n");
		return sb.toString();
		
	}
	
	// 97.14 % accurate
//	private String getCCInsideOutside(Raster rast, int r, int c) {
//		int nattr = 1;
//		StringBuffer sb = new StringBuffer();
//		final int nr = 4;
//		final int nc = 4;
//		Rectangle rect = cells[r][c];
//		Rectangle[][] rects = divideRect(rect,nr,nc);
//		final int length = 22;
//		int[] inside = new int[length];
//		int[] outside = new int[length];
//		for (int i=0;i< inside.length;i++){
//			inside[i] = outside[i] = 0;}
//		
//		for (int rr = 0; rr < nr;rr++){
//			for (int cc = 0;cc<nc;cc++){
//				rect = rects[rr][cc];
//				rect.x += rast.getMinX();
//				rect.y += rast.getMinY();
//
//				int[] colorcounts = new int[length];
//				getColorCounts(
//						rect.x, rect.y,(int) rect.getWidth(),(int) rect.getHeight(),
//						rast, colorcounts);
//				if (rr >= 1 && rr <= 2 && cc >= 1 && cc <= 2){
//					for (int i = 0; i < inside.length;i++) inside[i] += colorcounts[i];}
//
//				else if (
//						(rr > 0 && rr < nr && cc == 0) ||
//						(rr > 0 && rr < nr && cc == nc -1) ||
//						(cc > 0 && cc < nc && rr == 0) ||
//						(cc > 0 && cc < nc && rr == nr -1)
//				){
//					for (int i = 0; i < outside.length;i++) outside[i] += colorcounts[i];}
//
//			}
//		}
//
//		for (int k=0;k<inside.length;k++){ 
//			sb.append(nattr++ + ":" + inside[k] + " ");}				
//		for (int k=0;k<outside.length;k++){ 
//			sb.append(nattr++ + ":" + outside[k] + " ");}				
//
//		sb.append("\n");
//		return sb.toString();
//	}

	//300ms, 97.5 %, 96.7% confusing
//	private String getCCInsideOutsideandHSL(Raster rast, int r, int c) {
//		int nattr = 1;
//		StringBuffer sb = new StringBuffer();
//		final int nr = 4;
//		final int nc = 4;
//		Rectangle rect = cells[r][c];
//		Rectangle[][] rects = divideRect(rect,nr,nc);
//		rect.x += rast.getMinX();
//		rect.y += rast.getMinY();
//		Color cr = getAvgColor(rect.x, rect.y, (int) rect.getWidth(), (int) rect.getHeight(), rast);
//
//		final int length = 22;
//		int[] inside = new int[length];
//		int[] outside = new int[length];
//		for (int i=0;i< inside.length;i++){
//			inside[i] = outside[i] = 0;}
//		
//		for (int rr = 0; rr < nr;rr++){
//			for (int cc = 0;cc<nc;cc++){
//				rect = rects[rr][cc];
//				rect.x += rast.getMinX();
//				rect.y += rast.getMinY();
//
//				int[] colorcounts = new int[length];
//				getColorCounts(
//						rect.x, rect.y,(int) rect.getWidth(),(int) rect.getHeight(),
//						rast, colorcounts);
//				if (rr >= 1 && rr <= 2 && cc >= 1 && cc <= 2){
//					for (int i = 0; i < inside.length;i++) inside[i] += colorcounts[i];}
//
//				else if (
//						(rr > 0 && rr < nr && cc == 0) ||
//						(rr > 0 && rr < nr && cc == nc -1) ||
//						(cc > 0 && cc < nc && rr == 0) ||
//						(cc > 0 && cc < nc && rr == nr -1)
//				){
//					for (int i = 0; i < outside.length;i++) outside[i] += colorcounts[i];}
//
//			}
//		}
//
//
//		float[] hsl = new float[3];
//		getHsl(cr.getRed(), cr.getGreen(), cr.getBlue(), hsl);
//		for (int i = 0; i < hsl.length;i++){
//			sb.append(nattr++ + ":" + hsl[i] + " ");}
//
//		for (int k=0;k<inside.length;k++){ 
//			sb.append(nattr++ + ":" + inside[k] + " ");}				
//		for (int k=0;k<outside.length;k++){ 
//			sb.append(nattr++ + ":" + outside[k] + " ");}				
//
//		sb.append("\n");
//		return sb.toString();
//	}

	
	
	// 92.7% accurate, 
//	private String getBasicCC(Raster rast, int r, int c) {
//		Rectangle rect = new Rectangle(cells[r][c]);
//		rect.x += rast.getMinX();
//		rect.y += rast.getMinY();
//		
//		int[] center_cc = new int[8];
//		getBasicColorCounts(
//				rect.x, rect.y,(int) rect.getWidth(),(int) rect.getHeight(),
//				rast, center_cc);
//
//		StringBuffer sb = new StringBuffer();
//		for (int k=0;k<center_cc.length;k++){ 
//			sb.append(k+1 + ":" + center_cc[k] + " ");}
//		sb.append("\n");
//		return sb.toString();
//	}

//	private Rectangle[][] divideRect(Rectangle rect, int nr, int nc){
//		Rectangle rects[][] = new Rectangle[nr][nc];
//		double w = rect.getWidth() / nc;
//		double h = rect.getHeight() / nr;
//		
//		for (int r = 0; r < nr;r++){
//			for (int c = 0;c<nc;c++){
//				rects[r][c] = new Rectangle((int) (c*w) + rect.x, (int) (r*h) + rect.y,(int) w, (int) h);
//			}
//		}
//		return rects;
//	}
}
