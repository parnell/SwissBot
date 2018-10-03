package util;

import game.Move;

/**
 * Derived classes are responsible for making the move
 * @author parnell
 *
 */
public interface GameMover {
	
	/**
	 * Imlement the details of how to make the move.  Usually mouse clicks, drags, or keypresses
	 * @param move
	 */
	public abstract void makeMove(Move move);

	public abstract void touchBoard();
}
