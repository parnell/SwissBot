package bejeweled;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.media.jai.Histogram;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import util.Board;
import util.ScreenInterface;
import bejeweled.BBMove.CellIndex;

public class BBScreen extends ScreenInterface{

	private static final boolean debug = false;

	int count = 45;
	protected static final int NCELLS = 8;
	static final int CELL_OFFSET = 10;
	
	protected double lower = -1.0;
	protected double upper = 1.0;
	protected static final int MAX_FEATURES = 78;
	protected double feature_min[] = new double[MAX_FEATURES];
	protected double feature_max[] = new double[MAX_FEATURES];

	protected Rectangle cells[][] = new Rectangle[NCELLS][NCELLS];
	int tmpimage[] = null;
	protected Rectangle cell = null; //cell size
	protected Rectangle playrect = null;
	Rectangle absrect = null;
	protected svm_model model = null;
	

	public BBScreen() {
		super();
		initSVM();
	}

	private void initSVM() {
		int predict_probability = 0;
		String restore_filename = "/downloads/libsvm-2.89/tools/train.txt.range";
		try {
			model = svm.svm_load_model("/downloads/libsvm-2.89/tools/train.txt.model");
			if(predict_probability == 1){
				if(svm.svm_check_probability_model(model)==0){
					System.err.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			}
			else{
				if(svm.svm_check_probability_model(model)!=0){
					System.out.print("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			BufferedReader fp_restore =  new BufferedReader(new FileReader(restore_filename));;
			int idx;
			double fmin, fmax;
			if(fp_restore.read() == 'x') {
				fp_restore.readLine();		// pass the '\n' after 'x'
				StringTokenizer st = new StringTokenizer(fp_restore.readLine());
				lower = Double.parseDouble(st.nextToken());
				upper = Double.parseDouble(st.nextToken());
				String restore_line = null;
				while((restore_line = fp_restore.readLine())!=null)
				{
					StringTokenizer st2 = new StringTokenizer(restore_line);
					idx = Integer.parseInt(st2.nextToken());
					fmin = Double.parseDouble(st2.nextToken());
					fmax = Double.parseDouble(st2.nextToken());
					if (idx <= MAX_FEATURES){
						feature_min[idx-1] = fmin;
						feature_max[idx-1] = fmax;
					}
				}
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(ArrayIndexOutOfBoundsException e)  {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static Histogram getHistogram(int x, int y, int w,int h, Raster rast) {
		int[] bins = {26, 26, 26};
		double[] low = {0.0D, 0.0D, 0.0D};     
		double[] high = {256.0D, 256.0D, 256.0D}; 
		/// define a rectangular region of interest
		/// I tried a circle but it was Significantly slower
		Shape s = new Rectangle(x,y,w,h);
		ROI roi = new ROIShape(s);
	
		/// Make the histogram
		Histogram hist = new Histogram(bins, low, high);
		hist.countPixels(rast, roi, 0, 0, 1, 1);
		return hist;
	}

	public void drawBoard(Graphics g, BBBoard board){
		int x,y,w,h;
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				Rectangle rect = new Rectangle(cells[r][c]);
				rect.grow(-5, -5);
				x = rect.x;
				y = rect.y;
				w = (int) rect.getWidth();
				h = (int) rect.getHeight();
				g.setColor(BBColor.getColor(board.getColor(r,c)));
				g.fillRect(x,y,w,h);
				if (board.getType(r,c) == BBColor.COLOR_TYPE.MULT){
					g.setColor(Color.black);
					g.drawOval(x,y,w,h);
					g.drawOval(x-1,y-1,w+2,h+2);
				} else if (board.getType(r,c) == BBColor.COLOR_TYPE.SPECIAL){
					g.setColor(Color.cyan);
					g.drawOval(x,y,w,h);
					g.drawOval(x-1,y-1,w+2,h+2);
				}
			}
		}
	}

	public void drawMove(Graphics g, BBMove mv, Color color) {
		if (mv == null) return;
		for (CellIndex index : mv.getCellIndices()){
			g.setColor(color);
			int r = index.r;
			int c = index.c;
			for (int i =1;i<=2;i++)
				g.drawRect(cells[r][c].x -i, cells[r][c].y -i,
					(int) cells[r][c].getWidth() +i*2,(int) cells[r][c].getHeight()+i*2);
		}
	}	

	public static Color getAvgColor(int x, int y, int w,
            int h, Raster rast) {
	    int[] c = new int[3];
	
	    final int npix = w * h;
	
	    final int[] tmpimage = new int[npix * 3];
	    rast.getPixels(x, y, w, h, tmpimage);
	
	    int rSum = 0, gSum = 0, bSum = 0;
	
	    for (int i = 0; i < tmpimage.length; i += 3) {
	            rSum += tmpimage[i];
	            gSum += tmpimage[i + 1];
	            bSum += tmpimage[i + 2];
	    }
	
	    c[0] = rSum / npix;
	    c[1] = gSum / npix;
	    c[2] = bSum / npix;
		if (debug) System.out.print(c[0]+":"+c[1]+":" + c[2] + " ");
		
	    return new Color(c[0],c[1],c[2]);
	}
		
	
	public Board getBoard() {
		BBBoard board = new BBBoard();
		Raster rast = bi.getData();

		for (int r = 0; r < NCELLS; r++){
			for (int c = 0; c < NCELLS; c++){
				int v = getCellColor(rast, r, c);
				board.setCell(r, c, v);
			}
			if (debug) System.out.println("");
		}
		if (debug) System.out.println("--------------------\n");
		return board;
	}

	private int getCellColor(Raster rast, int r,int c) {
		int nattr = 1;
		Rectangle rect = new Rectangle(cells[r][c]);
		Histogram hist = BBScreen.getHistogram(rect.x, rect.y, (int) rect.getWidth(), (int) rect.getHeight(), rast);
		int[][] bins = hist.getBins();
		svm_node[] x = new svm_node[MAX_FEATURES];
		for (int i=0; i < bins.length; i++) {
			for (int j=0;j < bins[i].length;j++){
				x[nattr-1] = new svm_node();
				x[nattr-1].index = nattr;
				x[nattr-1].value = bins[i][j];
				nattr++;
			}
	     }
		
		for (int k=0;k<x.length;k++){
			if (feature_min[k] == feature_max[k]) continue;
			if (x[k].value == feature_min[k]){
				x[k].value = lower;
			} else if (x[k].value == feature_max[k]){
				x[k].value = upper;
			} else {
				x[k].value = lower + (upper-lower) *
					(x[k].value - feature_min[k])/
					(feature_max[k]-feature_min[k]);
			}
		}

		int v = (int) svm.svm_predict(model,x);
		return v;
	}
		
	public Rectangle findBoard(String top_left_file, String bottom_right_file){
		super.findBoard(top_left_file, bottom_right_file);
		if (board_rect != null  && 
				board_rect.getWidth() > 0 && board_rect.getHeight() > 0){
			calculateCellSize(board_rect,20);

			board_rect.x = board_rect.x - 20;
			board_rect.y = board_rect.y - 20;
			board_rect.width += 20*2;
			board_rect.height += 20*2;
		}
		return board_rect;
	}

	
	public void calculateCellSize(Rectangle board_rect, int border) {
		playrect = new Rectangle(border,border, (int) board_rect.getWidth(), (int) board_rect.getHeight());

		cell = new Rectangle(0,0, 
				playrect.width / NCELLS, playrect.height / NCELLS);
		int offset = cell.width / 8;
		for (int r = 0; r < NCELLS; r++){
			for (int c = 0; c < NCELLS; c++){
				cells[r][c] = new Rectangle(
						playrect.x + offset + cell.width*c +(c/2), 
						playrect.y + offset +  cell.height*r,
						cell.width - offset*2, 
						cell.height - offset*2);
			}
		}
		tmpimage = new int[(int) (cells[0][0].getWidth()*cells[0][0].getHeight()*3)];
	}
	
	public Point getCellCenter(int r, int c) {
		return new Point(
				(int) (board_rect.x + cells[r][c].getCenterX()),
				(int) (board_rect.y + cells[r][c].getCenterY()) );
	}


}
