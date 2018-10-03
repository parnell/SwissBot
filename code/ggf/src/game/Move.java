package game;

import java.io.Serializable;

import util.Board;

public abstract class Move implements Comparable<Move>, Serializable{
	private static final long serialVersionUID = 1L;

	protected float score = 0;
	
	protected Move previous = null;
	protected Move next = null;
	protected Board board = null;

	
	public abstract float getScore();

	public int compareTo(Move mv) {
		float f = this.getScore() - mv.getScore();
		if (f == 0) return 0;
		else return (f > 0) ? 1 : -1;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setNext(Move mv) {
		this.next = mv;
	}
	
	public Move getNext(){
		return this.next;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	public Board getBoard(){
		return this.board;
	}

	public Move getPrevious() {
		return previous;
	}

	public void setPrevious(Move previous) {
		this.previous = previous;
	}	
}