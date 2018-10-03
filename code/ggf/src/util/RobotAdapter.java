package util;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class RobotAdapter extends Robot{
	Point last_loc; // the last known location of the mouse
	public RobotAdapter() throws AWTException {
		super();
		PointerInfo pi = MouseInfo.getPointerInfo();
		last_loc = pi.getLocation();
	}
	
	public void move(Point topoint){
		move(topoint,1,0);
	}

	public void move(Point topoint, int steps, int time){
		move(topoint,steps,time,false);
	}
	
	public void move(Point frompoint, Point topoint, int steps, int time, boolean press_button){
		PointerInfo pi = MouseInfo.getPointerInfo();
		Point mouseloc = pi.getLocation();
		/// Mouse might have been moved by user... perhaps important so don't do anything
		if (!last_loc.equals(mouseloc)){
			last_loc = mouseloc;
			try {Thread.sleep( 700);} catch (InterruptedException e) {e.printStackTrace();}
			return;
		}
		mouseMove(frompoint.x, frompoint.y);
		/// Press mouse
		if (press_button){
			try {Thread.sleep( 120);} catch (InterruptedException e) {e.printStackTrace();}
			mousePress(InputEvent.BUTTON1_MASK);
		}
		double ix = (topoint.x - mouseloc.x)/ steps;
		double iy = (topoint.y - mouseloc.y)/ steps;
		double t = time / steps;
		/// Move to point in steps
		for (int i = 1; i <= steps; i++){
			mouseMove( (int) (mouseloc.x + ix*i), (int) (mouseloc.y + iy*i) );
			if (time > 0)
				try {Thread.sleep( (int)t);} catch (InterruptedException e) {e.printStackTrace();}
		}
		/// Release Mouse
		if (press_button) mouseRelease(InputEvent.BUTTON1_MASK);
//		last_loc = topoint;
		pi = MouseInfo.getPointerInfo();
		last_loc = pi.getLocation();

	}
	
	public void move(Point topoint, int steps, int time, boolean press_button){
		PointerInfo pi = MouseInfo.getPointerInfo();
		Point mouseloc = pi.getLocation();
		/// Mouse might have been moved by user... perhaps important so don't do anything
		if (!last_loc.equals(mouseloc)){
			last_loc = mouseloc;
			try {Thread.sleep( 700);} catch (InterruptedException e) {e.printStackTrace();}
			return;
		}
		/// Press mouse
		if (press_button){
			mousePress(InputEvent.BUTTON1_MASK);
		}
		double ix = (topoint.x - mouseloc.x)/ steps;
		double iy = (topoint.y - mouseloc.y)/ steps;
		double t = time / steps;
		/// Move to point in steps
		for (int i = 1; i <= steps; i++){
			mouseMove( (int) (mouseloc.x + ix*i), (int) (mouseloc.y + iy*i) );
			if (time > 0)
				try {Thread.sleep( (int)t);} catch (InterruptedException e) {e.printStackTrace();}
		}
		/// Release Mouse
		if (press_button) mouseRelease(InputEvent.BUTTON1_MASK);
		pi = MouseInfo.getPointerInfo();
		last_loc = pi.getLocation();
//		last_loc = topoint;	
	}
	
	public void drag(Point topoint){
		drag(topoint, 1,0);
	}

	public void drag(Point topoint, int steps, int time){
		move(topoint,steps,time,true);
	}
}
