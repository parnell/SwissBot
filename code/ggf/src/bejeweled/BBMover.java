package bejeweled;

import game.Move;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import util.GameMover;

public class BBMover implements GameMover{
	Robot robot = null;
	
	BBScreen screen = null;

	public BBMover(BBScreen screen){
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		this.screen = screen;
	}

	/**
	 * Make a move.  left click a block, move the mouse and click on the block to swap.
	 */
	public void makeMove(Move move) {
		BBMove mv = (BBMove) move;
					
		/// If we are only 1 cell away from our last position try to start 
		/// the move kitty corner so we don't accidentally move
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		Point p1,p2;
		p1 = screen.getCellCenter(mv.r1, mv.c1);
		p2 = screen.getCellCenter(mv.r2, mv.c2);			
		
		/// Do the moving
		robot.mouseMove(p1.x, p1.y);
		try {Thread.sleep(BBConfiguration.AFTER_MV1_WAIT);} catch (InterruptedException e) {e.printStackTrace();}
		robot.mousePress(InputEvent.BUTTON1_MASK);
		try {Thread.sleep(BBConfiguration.AFTER_PRESS1_WAIT);} catch (InterruptedException e) {e.printStackTrace();}
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		robot.mouseMove(p2.x, p2.y);
		try {Thread.sleep(BBConfiguration.AFTER_MV2_WAIT);} catch (InterruptedException e) {e.printStackTrace();}
		robot.mousePress(InputEvent.BUTTON1_MASK);
		try {Thread.sleep(BBConfiguration.AFTER_PRESS2_WAIT);} catch (InterruptedException e) {e.printStackTrace();}
		robot.mouseRelease(InputEvent.BUTTON1_MASK);			
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			
		p2 = screen.getCellCenter(0, 0);
		robot.mouseMove(p2.x - 30, p2.y -30);
	}

	public void touchBoard() {
		Point p = screen.getCellCenter(0, 0);
		robot.mouseMove(p.x - 30, p.y -30);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		try {Thread.sleep(BBConfiguration.AFTER_PRESS1_WAIT);} catch (InterruptedException e) {e.printStackTrace();}
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		
	}

}
