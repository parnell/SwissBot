package util;

import game.Move;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * 
 * @author parnell
 *
 */
public abstract class GameLogic {
	static final boolean debug = false;
	
	protected boolean quit = false; // Set to true when timer has run out
	Timer timer = null;

	static final int NPROCS = Runtime.getRuntime().availableProcessors();

	/// Stats variables
	long texamined = 0;
	int nsearches = 0;
	int tmoves = 0;
	int texhausted = 0;
	int nspecials = 0;
	int tdepth;

	public abstract Queue<Move> findMoves(Board board);
	
	/**
	 * Timer to stop searching
	 * @author parnell
	 *
	 */
	private class StopSearch extends TimerTask{
		public void run() {
			if (debug) System.out.println("search timer expired");
			timer.cancel(); //kill timer
			quit = true;
		}
	}

	public class BFS extends Thread{
		Move best = null;
		Queue<Move> moves = null;
		public int texamined = 0;
		
		BFS(Queue<Move> moves){
			this.moves = moves;
		}
		public Move getBest(){
			return best;
		}
		
		public void run() {
			while (!moves.isEmpty() && !quit){
				Move mv = moves.remove();
				Board scrapboard = mv.getBoard().copy();
				
				/// Perform the move
				scrapboard.performMove(mv);
				bfs_analyze(scrapboard, mv);
				
				/// is our new move better than our best?
				if (best == null || best.getScore() < mv.getScore()){
					best = mv;}
				
				/// Add all the children moves
				Queue<Move> mvs = findMoves(scrapboard);
				if (mvs != null){
					for (Move candidate : mvs){
						candidate.setPrevious(mv);
						candidate.setBoard(scrapboard);
						bfs_visit(mv,candidate);
					}
					moves.addAll(mvs);
				}
				bfs_postvisit(mv);
				texamined++;
			}
		}
	}
	 
	private Queue<Queue<Move>> split(Collection<Move> moves){
		Queue< Queue<Move> > list = new LinkedList<Queue<Move>>();
		int nperproc = (int) Math.ceil( ((double)moves.size()) / (NPROCS));
		int whichthread = 1;
		Queue<Move> mvs = new LinkedList<Move>();
		list.add(mvs);
		int i = 0;
		// nprocs =2;  size=5, 0,1   2,3   4
		for (Move mv : moves){
			if (i++ == nperproc * whichthread ){
				mvs = new LinkedList<Move>();
				list.add(mvs);
				whichthread++;
			}
			mvs.add(mv);
		}

		return list;
	}
	/**
	 * 
	 * @param board
	 * @param max_depth
	 * @param time
	 * @return
	 */
	public Move bfs(final Board board,final int max_depth,  final long time){
		if (board == null || time <= 0) return null;
		nsearches++;
		
		quit = false;
		timer = new Timer();
		timer.schedule(new StopSearch(), time);

		Move best = null;
		Queue<Move> moves = findMoves(board);
		if (moves == null) return null;
		for (Move mv : moves){
			mv.setBoard(board);}
		
		bfs_init();
		Queue<Queue<Move>> list = split(moves);
		Vector<BFS> bfss = new Vector<BFS>();
		
		for (Queue<Move> mvs : list){
			BFS bfs = new BFS(mvs);
			bfss.add(bfs);
			bfs.start();
		}
		
		for (BFS bfs : bfss){
			try {
				bfs.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (	best == null ||  
					(bfs.getBest() != null && bfs.getBest().getScore() > best.getScore()) ){
				best = bfs.getBest();
			}
			this.texamined += bfs.texamined;
		}
		
		Move swap = best;		
		while ( (swap != null && swap.getPrevious()!= null)){
			swap.getPrevious().setNext(swap);
			swap = swap.getPrevious();	
			tdepth++;
		}
//		System.out.println("texamined = " + texamined / nsearches );
 		best = swap;
		bfs_finish();
		return best;
	}
	
//	private void enQueueAll(Queue<Move> mvs) {
//		synchronized(this) {
//			moves.addAll(mvs);
//		}	
//	}
//
//
//	public void enQueue(Move move){
//		synchronized(this) {
//			moves.add(move);
//		}
//	}
//	
//	public Move deQueue(){
//		Move mv = null;
//		synchronized(this) {
//			mv = moves.remove();
//		}
//		return mv;
//	}
//	

	public void bfs_init(){
		
	}
		
	public void bfs_visit(Move u, Move v){
		
	}

	public void bfs_postvisit(Move u){
		
	}
	
	public void bfs_analyze(Board board, Move mv){
		
	}
	
	public void bfs_finish(){
		
	}
	
	public void resetStats() {
	  	nsearches = 0;
    	texamined = 0;
    	texhausted = 0;
    	tdepth = 0;
	}

	public String toString(){
		if (nsearches==0){
			return new String("nsearches=0,avgmoves=0,avgexamined=0,exhausted=0,avgdepth=0");
		} else {
			return   new String(
				"nsearches=" + nsearches +
				",avgmoves=" + tmoves / nsearches +
				",avgexamined=" + texamined/nsearches + 
				",exhausted=" + texhausted +
				",avgdepth=" + ((double)tdepth /nsearches));
		}
	}
	public abstract void merge(Board board, Board futureboard);

}
