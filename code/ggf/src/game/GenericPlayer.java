package game;

import util.GameLogic;
import util.GameMover;
import util.ScreenInterface;
import util.TestMaker;

public class GenericPlayer extends GamePlayer {
	
	GenericPlayer(ScreenInterface si, GameMover gm, GameLogic gl) {
		super(si, gm,gl);
	}

	public static void main(String[] args) {
//		BBScreen bbs = new BBScreen();
//		BBMover player = new BBMover(bbs);
//		BBLogic logic = new BBLogic();
//
//		GenericPlayer gp = new GenericPlayer(bbs, player, logic);
//		gp.idle();
		
		TestMaker bbs = new TestMaker();
		bbs.makeTests();

	}
	
}
