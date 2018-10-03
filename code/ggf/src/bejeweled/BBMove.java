package bejeweled;

import game.Move;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public class BBMove extends Move {
	private static final long serialVersionUID = 1L;
	protected int additional_score;
	protected int r1,c1;
	protected int r2,c2;
	protected int nspecialbreaks =0;
	protected int match = 0;
	protected Vector<CellIndex> cells = new Vector<CellIndex>();
	protected int depth = 0;

	
	class CellIndex implements Serializable{
		private static final long serialVersionUID = 1L;
		int r,c;
		CellIndex(int r, int c){
			this.r = r;
			this.c = c;
		}
		public String toString(){
			return r + ":" + c;
		}
	}
	
	public BBMove(){
		super();
		score = additional_score = 0;
	}
	
	public float getScore() {
		return ((score + additional_score)) / (depth+1) + 
			((previous != null) ? previous.getScore() : 0);
	}
	
	public Collection<CellIndex> getCellIndices(){
		return cells;
	}
	
	public void setIndexRange(int rb, int re, int cb, int ce, BBBoard board) {
		int m = 0;
		for (int i = cb; i<=ce;i++){
			for (int j = rb;j<=re;j++){
				m++;
				cells.add(new CellIndex(j,i));
				score += board.getScore(j, i);}}
		if (match == 3 && m == 3) m = 4;
		
		if (m > 3) score += m * 200;
		if (m > match) match = m;
		if (match == 4) score += 600;
		if (match == 5) score += 1000;
	}

	public void setSwapIndexes(int r1, int c1, int r2, int c2) {
		this.r1 = r1; this.c1 = c1;
		this.r2 = r2; this.c2 = c2;
	}

	public void addScore(int val) {
		additional_score += val;
	}

	public void setPrevious(Move previous) {
		super.setPrevious(previous);
		depth++;
	}
	
	public int getDepth(){
		return depth;
	}
	

	public int getMatch() {
		return match;
	}

	public int getCell() {
		return ((BBBoard) board).getBlock(r1, c1);
	}
	public int getCell2() {
		return ((BBBoard) board).getBlock(r2, c2);
	}


	public int getSpecialBreaks() {
		return nspecialbreaks;
	}

	public void addBreakSpecial() {
		nspecialbreaks++;
	}
		
	public String toString(){
		String str = "";
		for (CellIndex index : cells){
			str += index + ",";
		}
		return r1+":"+c1  + "    " + r2 + ":" + c2 + " =" + score + "=" + getScore() + 
		"   match=" + match + "  indices=" + str;
	}

	public CellIndex getMovedIndex() {
		// TODO Auto-generated method stub
		return null;
	}

}
