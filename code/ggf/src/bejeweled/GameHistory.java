package bejeweled;

import game.ImageFrame;
import game.Move;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;

import util.Board;
import util.ScreenInterface;


public class GameHistory {
	
	public static void writeMove(Move mv, int curhistory, int curmove) {
		try {
			FileOutputStream fos = new FileOutputStream( "/Users/parnell/workspace/bb/history_mv_" + curmove + ".bin");
			ObjectOutputStream outStream = new ObjectOutputStream( fos );
			outStream.writeObject( new Integer(curhistory) );
			outStream.writeObject( mv );
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeHistory(BufferedImage bi, Board board, int curhistory) {
		File of = new File("/Users/parnell/workspace/bb/history_" + curhistory + ".png");
		try {
			 ImageIO.write(bi, "PNG", of);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileOutputStream fos = new FileOutputStream( "/Users/parnell/workspace/bb/history_" + curhistory + ".bin");
			ObjectOutputStream outStream = new ObjectOutputStream( fos );
			outStream.writeObject( board );
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void showHistory(int cur_history, int cur_mv_history, int cur_mv, 
			ScreenInterface si, boolean show_board, ImageFrame frame) {
		BBScreen screen = (BBScreen) si;
		final AffineTransform ident = new AffineTransform ();
		RenderedImage lq_tl_ri = JAI.create("fileload", BBConfiguration.lq_top_left_file);
		RenderedImage lq_br_ri = JAI.create("fileload", BBConfiguration.lq_bottom_right_file);
		RenderedImage hq_tl_ri = JAI.create("fileload", BBConfiguration.hq_top_left_file);
		RenderedImage hq_br_ri = JAI.create("fileload", BBConfiguration.hq_bottom_right_file);


		if (cur_history > 0){
			RenderedImage ri = null;
			try {
				ri = JAI.create("fileload", "/Users/parnell/workspace/bb/history_" + cur_history + ".png" );
			} catch (IllegalArgumentException e){
				return;
			}
			BBBoard board = null;
			try {
				String filename = "/Users/parnell/workspace/bb/history_"+ cur_history + ".bin"; 
				ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(filename));
				board = (BBBoard) objstream.readObject();
				objstream.close();

			} catch (Exception e){}
			
			BufferedImage bi= new BufferedImage(ri.getWidth(), ri.getHeight(), BufferedImage.TYPE_INT_ARGB);			
			Graphics2D g2 = (Graphics2D) bi.getGraphics();
			Rectangle boardrect = screen.findBoard(ri,lq_tl_ri,lq_br_ri);
			if (boardrect == null) boardrect = screen.findBoard(ri,hq_tl_ri,hq_br_ri);
			if (boardrect == null) {
				System.err.println("Couldn't find game board on " + cur_history);
				return;
			}
			/// adjust params
			((BBScreen)screen).calculateCellSize(boardrect,20);

			g2.drawRenderedImage(ri, ident );
			if (show_board)
				((BBScreen) screen).drawBoard(g2, board);

			frame.ip_1.setImage(bi);
		}
		if (cur_mv_history > 0){
			RenderedImage ri = null;
			Move mv  =null;
			try {
				String filename = "/Users/parnell/workspace/bb/history_mv_"+ cur_mv_history + ".bin"; 
				ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(filename));
				Integer thehistory = (Integer) objstream.readObject();
				frame.tf_mvtohistory.setText(thehistory + "");
				mv = (Move) objstream.readObject();
				ri = JAI.create("fileload", "/Users/parnell/workspace/bb/history_" + thehistory + ".png" );
				objstream.close();

			} catch (IllegalArgumentException e){
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Rectangle boardrect = screen.findBoard(ri,lq_tl_ri,lq_br_ri);
			if (boardrect == null) boardrect = screen.findBoard(ri,hq_tl_ri,hq_br_ri);
			if (boardrect == null) {
				System.err.println("Couldn't find game board on " + cur_history);
				return;
			}

			/// adjust params
			((BBScreen)screen).calculateCellSize(boardrect,20);

			BufferedImage bi= new BufferedImage(ri.getWidth(), ri.getHeight(), BufferedImage.TYPE_INT_ARGB);			
			Graphics2D g2 = (Graphics2D) bi.getGraphics();
			g2.drawRenderedImage(ri, ident );
			if (cur_mv == 0){
				if (mv.getBoard() != null)
					((BBScreen) screen).drawBoard(g2, (BBBoard) mv.getBoard());
				((BBScreen) screen).drawMove(g2, (BBMove) mv, Color.cyan);
				System.out.println("cur move = " + mv);

			} else{
				int i = 0;
				
				while (i++ < cur_mv && mv.getNext() != null){
					mv = mv.getNext();
				}
				if (mv != null && mv.getBoard() != null)
					((BBScreen) screen).drawBoard(g2, (BBBoard) mv.getBoard());
				((BBScreen) screen).drawMove(g2, (BBMove) mv, Color.cyan);
				System.out.println(mv);
			}

			frame.ip_2.setImage(bi);
		}
	}


}
