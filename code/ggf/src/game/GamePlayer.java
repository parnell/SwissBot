package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import util.Board;
import util.GameLogic;
import util.GameMover;
import util.ScreenInterface;
import util.TestMaker;
import bejeweled.BBBoard;
import bejeweled.BBConfiguration;
import bejeweled.BBLogic;
import bejeweled.BBMover;
import bejeweled.BBScreen;
import bejeweled.GameHistory;

/**
 * 
 * @author parnell
 *
 */
public class GamePlayer implements ActionListener, ItemListener {
	private static final boolean debug = true;

	private int history = 0;
	private int mvhistory = 0;
	
	GameLogic logic = null;

	boolean quit = false;	
	
	ScreenInterface screen = null;
	GameMover player = null;
	ImageFrame frame = null;
	Timer timer = null;
	
	int cur_history = 0;
	int cur_mv_history = 0;
	int cur_mv = 0;
	
	boolean show_move = true;
	boolean show_board = true;
	long timerstarted = 0;
	long tavgtime  = 0 ;
	long loops = 0;
	int mistakes = 0;
	int moves = 0;
	
	int tdepth = 0; 
	
	static long old_time;

	public static void main(String[] args) {
		BBScreen bbs = new BBScreen();
		BBMover player = new BBMover(bbs);
		BBLogic logic = new BBLogic();
		GamePlayer gp = new GamePlayer(bbs, player,logic);
//		gp.idle();
	}
	
	/**
	 * 
	 * @param si
	 * @param gm
	 * @param logic
	 */
	GamePlayer(ScreenInterface si, GameMover gm, GameLogic logic){
		screen = si;
		player = gm;
		this.logic = logic;
		
		frame = new ImageFrame();
		frame.setPreferredSize(new Dimension(400,600));
		frame.more_time.addActionListener(this);

		frame.bt_backward_history.addActionListener(this);
		frame.bt_forward_history.addActionListener(this);
		frame.tf_history_number.addActionListener(this);
		frame.cb_show_board.addItemListener(this);
		frame.cb_show_move.addItemListener(this);
		frame.tf_history_number.setText("0");
		
		frame.tf_mv_history.setText("0");
		frame.bt_mv_history_forward.addActionListener(this);
		frame.bt_mv_history_backward.addActionListener(this);
		
		frame.tf_mv.setText("0");
		frame.bt_mv_next.addActionListener(this);
		frame.bt_mv_previous.addActionListener(this);

		frame.bt_make_guess.addActionListener(this);
		
		frame.setVisible(true);
	}

	public static void startTime(){
		old_time = System.currentTimeMillis();
	}
	
	public static void endTime(String str){
		System.out.println( (System.currentTimeMillis() - old_time) + ": " + str);
	}
	
	
	void playGame(){
		if (player == null || screen == null || logic == null)
			return;

		long oldtime = System.currentTimeMillis();
		Board lastmoveboard = null;
		Board futureboard = null;
Board lastboard = null;
		Move mv = null;
		Move oldmv = null;

		while(!quit){
			Board board = null;
			BufferedImage bi = null, copy = null;
//			Graphics g = null;
				
			/// Capture Screen
			bi = screen.capture();
//			if (bi == null) continue;
//			WritableRaster raster = bi.copyData( null );
//			copy = new BufferedImage( bi.getColorModel(), raster, bi.isAlphaPremultiplied(), null );				
//			g = copy.getGraphics();
			/// Interpret game board from screen
			startTime();
			board = screen.getBoard();
			endTime("got board");
//			if (	board.consistent() && 
//			(board != null && futureboard != null && board.similar(futureboard) < 3)){				
//				logic.merge(board,futureboard);
//			}
//			if (lastboard == null || !board.same(lastboard)){
//				GameHistory.writeHistory(bi,board, history++);
//			}
//			lastboard = board;
			/// Only find and make move if our board is good
			if (board != null && board.consistent()){
				loops++;
				long curtime = System.currentTimeMillis();
				tavgtime +=  curtime - oldtime;
				oldtime = curtime;

				/// Find Move			
				if (lastmoveboard == null || !board.same(lastmoveboard))
					mv = logic.bfs(board, (int) (BBConfiguration.TIME_TO_PLAY - (System.currentTimeMillis() - timerstarted)), 
							BBConfiguration.SEARCH_MS);
					
//				GameHistory.writeMove(mv, history -1,mvhistory++);
//				futureboard = (mv.getNext() == null || mv.getNext().getBoard() == null)
//					? null : mv.getNext().getBoard();

				/// Make the move
				if (mv != null ) {
					player.makeMove(mv);
					if (BBConfiguration.AFTER_MOVE_SLEEP > 0) 
						try {Thread.sleep(BBConfiguration.AFTER_MOVE_SLEEP);} catch (InterruptedException e) {e.printStackTrace();}
					lastmoveboard = board;
					if (mv != oldmv) moves++;
					oldmv = mv;
				}	
			}
						
			if (BBConfiguration.LOOP_SLEEP > 0) 
				try {Thread.sleep(BBConfiguration.LOOP_SLEEP);} catch (InterruptedException e) {e.printStackTrace();}

//			if (cur_history <= 0){
//				if (g == null) continue;
////				if (debug && show_move && mv !=null ) screen.drawMove(g, mv, Color.green);
////				if (debug && show_board && board != null) screen.drawBoard(g, (BBBoard) board);
//				if (debug) frame.ip_1.setImage(copy);
//			} 
		}
	}

	private void showHistory(){
		GameHistory.showHistory(cur_history, cur_mv_history, cur_mv, screen, show_board, frame);
	}
	
	
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source == frame.cb_show_board){
			show_board = frame.cb_show_board.getState();
		} else if (source == frame.cb_show_move){
			show_move = frame.cb_show_move.getState();
		}
		showHistory();
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
	    if (source == frame.tf_history_number){
	    	cur_history = Integer.valueOf(frame.tf_history_number.getText());
	    	showHistory();
	    } else if (source == frame.bt_mv_history_backward){
    		cur_mv_history--;
    		frame.tf_mv_history.setText(cur_mv_history +"");
    		showHistory();
	    }else if (source == frame.bt_mv_history_forward){
    		cur_mv_history++;
    		frame.tf_mv_history.setText(cur_mv_history +"");
    		showHistory();
	    } else if (source == frame.bt_mv_next){
    		cur_mv++;
    		frame.tf_mv.setText(cur_mv +"");
    		showHistory();
	    } else if (source == frame.bt_mv_previous){
    		cur_mv--;
    		frame.tf_mv.setText(cur_mv +"");
    		showHistory();
	    } else if (source == frame.bt_backward_history){
    		cur_history--;
    		frame.tf_history_number.setText(cur_history +"");
    		showHistory();
	    } else if (source == frame.bt_forward_history){
	    	cur_history++;
    		frame.tf_history_number.setText(cur_history +"");
	    	showHistory();
	    } else if (source == frame.bt_make_guess){
	    	TestMaker.makeGuess(screen);
	    } else if (source == frame.more_time){
			run();
	    }
	    
	}

	protected boolean run(){
    	Rectangle rect = screen.findBoard(BBConfiguration.lq_top_left_file, BBConfiguration.lq_bottom_right_file);
		if (rect == null)
			rect = screen.findBoard(BBConfiguration.hq_top_left_file, BBConfiguration.hq_bottom_right_file);
    	if (rect == null){
    		player = null;
    		System.err.println("Couldn't find game board");
    		return false;
    	} 

    	if (timer != null){
    		timer.cancel();
    	}
    	if (debug) System.out.println("Starting timer for " + BBConfiguration.TIME_TO_PLAY);
//    	logic.resetStats();
    	loops = 0;
    	mistakes = 0;
    	tavgtime = 0;
    	moves = 0;
//    	history.clear();
    	history = 0;
    	mvhistory = 0;
		timer = new Timer();
		timer.schedule(new StopMoving(), BBConfiguration.TIME_TO_PLAY);
		timerstarted= System.currentTimeMillis();
		logic.resetStats();
		quit = false;
		player.touchBoard();
		playGame();
		return true;
	}
		
	public void idle(){
		while (!quit){
			if (screen.findBoard(BBConfiguration.lq_top_left_file, BBConfiguration.lq_bottom_right_file) != null ||
					screen.findBoard(BBConfiguration.hq_top_left_file, BBConfiguration.hq_bottom_right_file) != null ){
				BufferedImage bi = screen.capture();
				Graphics g = bi.getGraphics();
				Board board = screen.getBoard();
				
				((BBScreen)screen).drawBoard(g, (BBBoard) board);
				frame.ip_1.setImage(bi);
			}		

			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	private class StopMoving extends TimerTask{
		public void run() {
	        if (debug) System.out.println("Timer expired!%n");
	        timer.cancel(); //kill timer
	        quit = true;
	        System.out.println("Timer Expired!\n " +
	        		logic +
	        		"\navgtime=" + ((loops > 0) ? tavgtime / loops : 0)+
	        		"\navgdepth=" + ((loops > 0) ? tdepth / loops : 0)+
	        		",searchms="+ BBConfiguration.SEARCH_MS + 
	        		",mistakes=" + mistakes + 
	        		",nmoves=" + moves 
	        		);
		}
		
	}



}
