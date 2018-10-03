package bejeweled;

import java.io.Serializable;

import game.Move;
import util.Board;
import bejeweled.BBColor.COLOR_TYPE;

public class BBBoard implements Board, Serializable{
	private static final long serialVersionUID = 1L;
	
	int cells[][] = new int[8][8];
	
	public BBBoard(BBBoard copy){
		for (int r = 0;r<8;r++){
			for (int c=0;c<8;c++){
				cells[r][c] = copy.cells[r][c];
			}
		}
	}
	public Board copy(){
		BBBoard board = new BBBoard(this);
		return board;
	}
	
	public BBBoard(){
		for (int r = 0;r<8;r++){
			for (int c=0;c<8;c++){
				cells[r][c] = 1;
			}
		}	
	}
	
	public void setCell(int r, int c, int v){
		cells[r][c] = v;
	}
	
	public int getBlock(int r, int c){
		return cells[r][c];
	}
	
	int getColor(int r,int c){
		return BBColor.getIntColor(cells[r][c]);
	}
		
	int[][] getColors(){
		int[][] colors = new int[8][8];
		for (int i =0;i<8;i++) for(int j=0;j<8;j++) colors[i][j] = BBColor.getIntColor(cells[i][j]);
		return colors;
	}

	int[][] getCells(){
		return cells;
	}
	
	int getBreakScore(int r, int c){
		return (cells[r][c] == BBColor.POWER) ? -10 : BBColor.getScore(cells[r][c]);
	}
	
	int getScore(int r, int c){
		return BBColor.getScore(cells[r][c]);
	}
	
	COLOR_TYPE getType(int r, int c){
		return BBColor.getType(cells[r][c]);
	}	
	
	public boolean same(Board board) {
		BBBoard b = (BBBoard) board;
		for (int i =0;i<8;i++)for(int j=0;j<8;j++) 
			if (BBColor.getIntColor(this.cells[i][j]) != BBColor.getIntColor(b.cells[i][j])) return false; 
		return true;
	}

	public float similar(Board board) {
		BBBoard b = (BBBoard) board;
		int difs = 0;
		for (int i =0;i<8;i++)for(int j=0;j<8;j++){
			int c = BBColor.getIntColor(this.cells[i][j]);
			int c2 = BBColor.getIntColor(b.cells[i][j]);
			if (	c == BBColor.BLACK || c == BBColor.CNULL || 
					c2 == BBColor.BLACK || c2 == BBColor.CNULL) continue;
			if (BBColor.getIntColor(this.cells[i][j]) != BBColor.getIntColor(b.cells[i][j])) difs++;
		}
		return ((float)difs);
	}

	public void swap(int r, int c, int r2, int c2) {
		int t = cells[r][c];
		cells[r][c] = cells[r2][c2];
		cells[r2][c2] = t;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (int i =0;i<8;i++){ 
			for(int j=0;j<8;j++){
				sb.append(cells[i][j] + "\t");}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public boolean consistent() {
		int bc = 0; //black count
		int tbc = 0; // top levelblack count
		int grid[][] = getColors();
		for (int r = 7; r >= 0; r--){
			for (int c = 7; c >= 0; c--){
				if (r == 0 && grid[r][c] == BBColor.BLACK) tbc++;
				else 
					if (grid[r][c] == BBColor.BLACK) bc++;
			}		
		}
		return (bc <= 0 && tbc <= 8 && BBLogic.isConsist(this) );
	}

	public void performMove(Move move) {
		BBMove mv = (BBMove) move;
		swap(mv.r1, mv.c1, mv.r2, mv.c2);		
	}
	public int getHeight() {
		return 8;
	}
	public int getWidth() {
		return 8;
	}
}
