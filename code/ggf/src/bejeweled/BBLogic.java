package bejeweled;

import game.Move;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.LinkedList;
import java.util.Queue;

import util.Board;
import util.GameLogic;
import bejeweled.BBColor.COLOR_TYPE;
import bejeweled.BBMove.CellIndex;

public class BBLogic extends GameLogic{
	
	public BBScreen screen = null;
	public Robot robot = null;
	public BBLogic(){
		try {
			robot = new Robot();} 
		catch (AWTException e) {}
	}
	
	public void bfs_analyze(Board _board, Move _mv){
		super.bfs_analyze(_board, _mv);
		BBBoard board = (BBBoard) _board;
		BBMove mv = (BBMove) _mv;
		int score =0;
		/// Calculate break score for the move
		if (mv.getMatch() == 1) {
			board.setCell(mv.r2, mv.c2, BBColor.CNULL);
			score = breakAllColor(board.getColor(mv.r1, mv.c1), board, mv);
		} else 
			score = breakMove(board, mv);
		score += dropBlocks(board,1);
		mv.addScore(score);
	}

	public Queue<Move> findMoves(Board _board){
		BBBoard board = (BBBoard) _board;
		BBMove mv =null;
		Queue<Move> moves = new LinkedList<Move>();
		int cell;
		int np = 0;
		for (int r = 7; r >= 0 && !quit; r--){
			for (int c = 7; c >= 0 && !quit; c--){
				cell = board.getBlock(r, c);
				if (cell == BBColor.BLACK || cell == BBColor.CNULL) {
					continue;
				}
				if (cell == BBColor.POWER){
					np++;
					moves.add(getPowerMove(r,c,board));
					continue;
				}
				/// swap lr
				if (c < 7){
					board.swap(r, c, r,c+1);
					mv = findMove(r,c,board,false); /// find left 3
					if (mv != null) {
						mv.setSwapIndexes(r,c+1,r,c);
						moves.add(mv);
					}
					mv = findMove(r,c+1,board,false); /// find right 3
					if (mv != null){
						mv.setSwapIndexes(r,c,r,c+1);
						moves.add(mv);
					}
					board.swap(r, c, r,c+1);
				}
				
				/// swap ud
				if (r < 7){
					board.swap(r,c, r+1,c);
					mv = findMove(r,c,board, false); /// Find top 3
					if (mv != null){
						mv.setSwapIndexes(r+1,c,r,c);
						moves.add(mv);
					}
					mv = findMove(r+1,c,board,false); /// Find Bottom 3
					if (mv != null){
						mv.setSwapIndexes(r,c,r+1,c);
						moves.add(mv);
					}
					board.swap(r,c, r+1,c);
				}
			}
		}
		return moves.size() > 0 ? moves : null;
	}

	private BBMove getPowerMove(final int r, final int c, final BBBoard board) {
		int max_score = 0;
		int color,score;
		BBMove best = null;
		for (int i= -1; i <= 1; i++) for (int j=-1;j<=1;j++){
			if ( !(i == 0 || j== 0) || (i==0 && j==0)) continue;
			if (r+j < 0 || r+j >7 || c+i <0 || c+i > 7){
				continue;}
			BBMove mv = new BBMove();
			mv.setIndexRange(r, r, c, c, board);
			BBBoard scrapboard = new BBBoard(board);
			
			scrapboard.setCell(r, c, BBColor.CNULL);
			color = scrapboard.getColor(r+j,c+i);
			score = breakAllColor(color, scrapboard,mv);
			score += 20;
			score += dropBlocks(scrapboard,1);
			
			if (score > max_score){
				mv.setScore(score);
				mv.setSwapIndexes(r, c, r+j, c+i);
				best = mv;
				max_score = score; 
			}
		}
		return best;
	}


	private static BBMove findMove(final int r,final int c,final BBBoard board, boolean dropped) {
		BBMove mv = null;
		boolean vertical = false;
		do {	
			mv = findMove(r, c, board, mv, vertical);

			vertical = !vertical;
		} while (vertical);
		return mv;
	}

	private static BBMove findMove(final int r, final int c,
			final BBBoard board, BBMove mv, boolean vertical) {
		int bi = (vertical) ? r : c;
		int ei = (vertical) ? r : c;
		int color = board.getColor(r, c);
		if (color == BBColor.CNULL || color == BBColor.BLACK)
			return null;
		if (vertical){
			for (int i = r; --i >= 0 && board.getColor(i, c) == color;){bi = i;}	
			for (int i = r; ++i < 8 && board.getColor(i, c) == color;){ei = i;}
		} else {
			for (int i = c; --i >= 0 && board.getColor(r, i) == color;){bi = i;}	
			for (int i = c; ++i < 8 && board.getColor(r, i) == color;){ei = i;}
		}
		if (ei - bi >= 2){
			if (mv == null)
				mv = new BBMove();
			int rb,re,cb,ce;
			if (vertical){
				rb = bi;
				re = ei;
				cb = ce = c;
			} else {
				cb = bi;
				ce = ei;
				rb = re = r;
			}
			mv.setIndexRange(rb,re,cb,ce, board);
		}
		return mv;
	}

	public static boolean isConsist(final BBBoard board){
		BBMove mv =null;
		for (int r = 7; r >= 0; r--){
			for (int c = 7; c >= 0; c--){
				mv = findMove(r,c,board,false); 
				if (mv != null) {
					return false;}
			}
		}
		return true;
	}
	
	public static int dropBlocks(BBBoard board, int multiplier){
		int total_score = 0;
		int break_loc[] = new int[8]; //lowest broken blocks
		for (int i =0;i<8;i++) break_loc[i] =-1;
		
		for (int r = 7;r>=0;r--) for (int c = 0; c < 8;c++ ){
			if (board.getBlock(r, c) == -1){
				/// recalc lowest broken block.. everything above must be checked when finding more breaks
//				if (break_loc[c] < r) {
//					if (c >0 && break_loc[c-1] < r) break_loc[c-1] = r;
//					break_loc[c] = r;
//					if (c < 7 && break_loc[c+1] < r) break_loc[c+1] = r;
//				}

				///Move everything up
				int y2 = r;
				while (--y2 >= 0 && board.getBlock(y2,c) == -1){}
//				System.out.println("swapping " + (i)  + ", " + (j) + " <-> " + (y2));
				int t = (y2 < 0) ? 0 : board.getBlock(y2,c);
				if (y2 >= 0){
					board.setCell(y2,c, board.getBlock(r, c));}
				board.setCell(r,c,t);
			}
		}
		
		BBMove mv = null;
		/// BB seems to create "special" blocks starting from the lower right
//		for (int c=7;c>=0;c--) for (int r=break_loc[c]; r >=0; r--){
		for (int r = 7;r>=0;r--) for (int c = 0; c < 8;c++ ){
			mv = findMove(r,c,board,true); /// find lr
			if (mv != null) {
				mv.setSwapIndexes(r+1, c, r, c);
				total_score += breakMove(board,mv);
			}
		}
		if (total_score > 0) total_score += multiplier++ * dropBlocks(board, multiplier) ;
		return total_score;
	}
	
	private static int breakAllColor(int color, BBBoard board, BBMove mv) {
		int score = 0;
		for (int r = 7; r>=0; r--) for (int c = 0; c < 8;c++ ){
			if (board.getColor(r, c) == color){
				score += breakBlock(board,r,c,1,mv);}
		}
		return score;
	}

	public static int breakMove(BBBoard board, BBMove move){
		int score = 0;
		int special = move.getMatch() == 4 ? 2 : 1;
		boolean promoted = false;
		/// In the act of breaking a move greater than 3 a special or power block is created
		if (move.getMatch() == 4){
			board.setCell(move.r2, move.c2, BBColor.getSpecialColor(board.getBlock(move.r2, move.c2)));
			promoted = true;
		} else if (move.getMatch() == 5 ){
			board.setCell(move.r2, move.c2, BBColor.POWER);
			promoted = true;
		}
		
		/// Go through and break the blocks
		for (CellIndex index : move.getCellIndices()){
			/// Don't break the newly promoted piece
			/// if it gets destroyed by a blast from another piece that's fine
			if (promoted && index.r == move.r2 && index.c == move.c2) continue;
			/// break the individual block
			score += breakBlock(board,index.r, index.c,special,move);
		}
		return score;
	}
	
	public static int breakBlock(BBBoard board, int r, int c,int special, BBMove mv){
		int score = 0;
		COLOR_TYPE type = board.getType(r, c);
		/// Add the score for breaking this block
		score += board.getBreakScore(r, c);
		board.setCell(r, c, BBColor.CNULL);
		
		/// Break all surrounding blocks if this was a special
		if (type == COLOR_TYPE.SPECIAL){
			mv.addBreakSpecial();
			if (c > 0) score+=breakBlock(board,r,c-1,special+1, mv) * special;
			if (c < 7) score+=breakBlock(board,r,c+1,special+1, mv) * special;
			if (r > 0) score+=breakBlock(board,r-1,c,special+1, mv) * special;
			if (r < 7) score+=breakBlock(board,r+1,c,special+1, mv) * special;

			if (c > 0 && r > 0) score+=breakBlock(board,r-1,c-1,special+1, mv) * special;
			if (c < 7 && r < 7) score+=breakBlock(board,r+1,c+1,special+1, mv) * special;
			if (c > 0 && r < 7) score+=breakBlock(board,r+1,c-1,special+1, mv) * special;
			if (c < 7 && r > 0) score+=breakBlock(board,r-1,c+1,special+1, mv) * special;
		}
		return score;
	}
	
	public static boolean consistent(BBBoard board){
		int bc = 0; //black count
		int tbc = 0; // top levelblack count
		int grid[][] = board.getColors();
		for (int r = 7; r >= 0; r--){
			for (int c = 7; c >= 0; c--){
				if (r == 0 && grid[r][c] == BBColor.BLACK) tbc++;
				else 
					if (grid[r][c] == BBColor.BLACK) bc++;
			}		
		}
		if (BBConfiguration.TBLACK_LOGIC > 0)
			return (bc <= BBConfiguration.BLACK_LOGIC && tbc <= BBConfiguration.TBLACK_LOGIC && isConsist(board) );		
		return (bc <= BBConfiguration.BLACK_LOGIC && isConsist(board) );
	}


	@Override
	public void merge(Board _board, Board _futureboard) {
		BBBoard board = (BBBoard) _board;
		BBBoard futureboard = (BBBoard) _futureboard;
		for (int c = 0; c < 8 ;c++){
			for (int r = 0; r < 8 ; r++){
				if (futureboard.cells[r][c] == BBColor.GL_WHITE)
					board.setCell(r, c, BBColor.GL_WHITE);
			}
		}
	}

}
