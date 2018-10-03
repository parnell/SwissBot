package bejeweled;

import java.awt.Color;

public class BBColor {
	public static final int CNULL = -1;
	public static final int BLACK = 0;
	public static final int RED = 1;
	public static final int GREEN = 2;
	public static final int BLUE = 3;
	public static final int MAGENTA = 4;
	public static final int YELLOW = 5;
	public static final int ORANGE = 6;
	public static final int WHITE = 7;
	public static final int GL_RED = 8;
	public static final int GL_GREEN = 9;
	public static final int GL_BLUE = 10;
	public static final int GL_MAGENTA = 11;
	public static final int GL_YELLOW = 12;
	public static final int GL_ORANGE = 13;
	public static final int GL_WHITE = 14;
	public static final int RED_MULT = 15;
	public static final int GREEN_MULT = 16;
	public static final int BLUE_MULT = 17;
	public static final int MAGENTA_MULT = 18;
	public static final int YELLOW_MULT = 19;
	public static final int ORANGE_MULT = 20;
	public static final int WHITE_MULT = 21;
	public static final int POWER = 22;

	public enum COLOR_TYPE{ NONE, NORMAL, SPECIAL, MULT, POWER};
	
	public static Color getColor(int i){
		switch(i){
		case BLACK: return Color.black;
		case RED: ;
		case RED_MULT: 
		case GL_RED: return Color.red;
		case GREEN: 
		case GREEN_MULT: 
		case GL_GREEN: return Color.green;
		case BLUE: 
		case BLUE_MULT: 
		case GL_BLUE: return Color.blue;
		case MAGENTA:
		case MAGENTA_MULT: 
		case GL_MAGENTA: return Color.magenta;
		case YELLOW: 
		case YELLOW_MULT:
		case GL_YELLOW: return Color.yellow;
		case ORANGE:
		case ORANGE_MULT:
		case GL_ORANGE: return Color.darkGray;
		case WHITE: 
		case WHITE_MULT: 
		case GL_WHITE: return Color.white;
		case POWER: return Color.pink;
		default: return Color.black;
		}
	}
	
	public static int getIntColor(int i){
		switch(i){
		case BLACK: return BLACK;
		case RED: ;
		case RED_MULT: 
		case GL_RED: return RED;
		case GREEN: 
		case GREEN_MULT: 
		case GL_GREEN: return GREEN;
		case BLUE: 
		case BLUE_MULT: 
		case GL_BLUE: return BLUE;
		case MAGENTA:
		case MAGENTA_MULT: 
		case GL_MAGENTA: return MAGENTA;
		case YELLOW: 
		case YELLOW_MULT:
		case GL_YELLOW: return YELLOW;
		case ORANGE:
		case ORANGE_MULT:
		case GL_ORANGE: return ORANGE;
		case WHITE: 
		case WHITE_MULT: 
		case GL_WHITE: return WHITE;
		case POWER: return POWER;
		default: return BLACK;
		}
	}
	
	public static int getScore(int i){
		switch(i){
		case BLACK: 
		case RED:
		case GREEN: 
		case BLUE: 
		case MAGENTA: 
		case YELLOW: 
		case ORANGE: 
		case WHITE: return 66;
		case POWER: return 1000;
		case GL_RED: 
		case GL_GREEN:
		case GL_BLUE:
		case GL_MAGENTA:
		case GL_YELLOW: 
		case GL_ORANGE: 
		case GL_WHITE: return 600;
		case RED_MULT:
		case GREEN_MULT: 
		case BLUE_MULT: 
		case MAGENTA_MULT: 
		case YELLOW_MULT: 
		case ORANGE_MULT: 
		case WHITE_MULT: return 4000;
		default: return -1;
		}
	}
	public static COLOR_TYPE getType(int i){
		switch(i){
		case BLACK: 
		case RED:
		case GREEN: 
		case BLUE: 
		case MAGENTA: 
		case YELLOW: 
		case ORANGE: 
		case WHITE: return COLOR_TYPE.NORMAL;
		case POWER: return COLOR_TYPE.POWER;
		case GL_RED: 
		case GL_GREEN:
		case GL_BLUE:
		case GL_MAGENTA:
		case GL_YELLOW: 
		case GL_ORANGE: 
		case GL_WHITE: return COLOR_TYPE.SPECIAL;
		case RED_MULT:
		case GREEN_MULT: 
		case BLUE_MULT: 
		case MAGENTA_MULT: 
		case YELLOW_MULT: 
		case ORANGE_MULT: 
		case WHITE_MULT: return COLOR_TYPE.MULT;
		default: return COLOR_TYPE.NONE;
		}
	}

	public static int getSpecialColor(int i) {
		switch(i){
		case BLACK: return BLACK;
		case RED: ;
		case RED_MULT: 
		case GL_RED: return GL_RED;
		case GREEN: 
		case GREEN_MULT: 
		case GL_GREEN: return GL_GREEN;
		case BLUE: 
		case BLUE_MULT: 
		case GL_BLUE: return GL_BLUE;
		case MAGENTA:
		case MAGENTA_MULT: 
		case GL_MAGENTA: return GL_MAGENTA;
		case YELLOW: 
		case YELLOW_MULT:
		case GL_YELLOW: return GL_YELLOW;
		case ORANGE:
		case ORANGE_MULT:
		case GL_ORANGE: return GL_ORANGE;
		case WHITE: 
		case WHITE_MULT: 
		case GL_WHITE: return GL_WHITE;
		case POWER: return POWER;
		default: return BLACK;
		}
	}

}
