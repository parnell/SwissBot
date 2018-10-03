package util;

import game.Move;

/**
 * Game Board
 * @author parnell
 *
 */
public interface Board {

	/**
	 * Does the game board make sense?
	 * @return
	 */
	public abstract boolean consistent();
	
	/**
	 * Are two boards the same?
	 * @param board
	 * @return
	 */
	public abstract boolean same(Board board);
	
	/**
	 * Make a move on the gameboard
	 * @param mv
	 */
	public abstract void performMove(Move mv);
	
	/**
	 * Return a copy of this board
	 * @return
	 */
	public abstract Board copy();

	
	public abstract float similar(Board board);

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract int getBlock(int r, int c);
}
