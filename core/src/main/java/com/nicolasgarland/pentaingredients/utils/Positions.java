package com.nicolasgarland.pentaingredients.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class Positions {
	public static enum Emplacement {
		PUISSANCE,
		CONTROLE,
		ETAGERE
	}
	
	/** Dossier des sauvegardes, relatif au dossier de lancement du jeu. */
	private static final String SAVE_DIR = "saves/";
	
	public int[] pentaPuissance;
	public int[] pentaControle;
	public int[][] etagere;
	
	public Positions() {
		this.pentaPuissance = new int[]{0,0,0,0,0};
		this.pentaControle = new int[]{0,0,0,0,0};
		
		int[][] etagereDeBase = {
			{ 1,  2,  3,  4,  5,  6,  0,  0,  0,  0},
			{ 7,  8,  9, 10, 11, 12, 13, 14,  0,  0},
			{15, 16, 17, 18, 19, 20, 21, 22, 23, 24},
			{25, 26, 27, 28, 29, 30, 31, 32, 33, 34},
			{35, 36, 37, 38, 39, 40, 41, 42,  0,  0},
			{43, 44, 45, 46, 47, 48,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0}
		};
		this.etagere = etagereDeBase;
	}
	
	public Positions(int[] puissance, int[] controle, int[][] etagere) {
		this.pentaPuissance = puissance;
		this.pentaControle = controle;
		this.etagere = etagere;
	}
	
	public int getIngr(int pos, Emplacement empl) {
		switch(empl) {
			case PUISSANCE: 
				return pentaPuissance[pos]; 
			case CONTROLE:
				return pentaControle[pos];
			case ETAGERE:
				return etagere[pos/10][pos%10]; 
		}
		return 0;
	}
	
	public void setIngr(int pos, Emplacement empl, int ing) {
		switch(empl) {
			case PUISSANCE: 
				pentaPuissance[pos] = ing; 
				break;
			case CONTROLE:
				pentaControle[pos] = ing;
				break;
			case ETAGERE:
				etagere[pos/10][pos%10] = ing; 
				break;
		}
	}
	
	public void swapIngr(int pos1, Emplacement empl1, int pos2, Emplacement empl2) {
		int a = getIngr(pos1, empl1);
		int b = getIngr(pos2, empl2);
		
		setIngr(pos1, empl1, b);
		setIngr(pos2, empl2, a);
	}
	
	public void savePosition(int idLevel) {
		String fileName = SAVE_DIR + "posision" + idLevel + ".json";
		FileHandle file = Gdx.files.local(fileName);
       	file.writeString((new Json()).prettyPrint(this), false);
	}
	
	public Positions loadPositions(int idLevel) {
    	Positions pos = this;
        String fileName = SAVE_DIR + "posision" + idLevel + ".json";
        if (Gdx.files.local(fileName).exists()) {
            pos = (new Json()).fromJson(Positions.class, Gdx.files.local(fileName));
        } else {
        	Gdx.app.log("ERROR", "in loaging position : " + Gdx.files.local(fileName).file().getAbsolutePath());
        }
		return pos;
	}

}
