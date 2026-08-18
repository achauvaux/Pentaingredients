package com.nicolasgarland.pentaingredients.utils;

import java.util.List;

import com.nicolasgarland.pentaingredients.utils.Ingredient.Famille;

public class Arbitre {
	private final Level objectif;
    private final List<Ingredient> listOfIngredients;
    private Positions pos;

	public Arbitre(Level thisLevel, List<Ingredient> listOfIngredients, Positions pos) {
		this.objectif = thisLevel;
		this.listOfIngredients = listOfIngredients;
		this.pos = pos;
	}

	public Pentacle validerPentacle() {
		String res = "";
		Boolean isCtrlOk = true;
		Boolean isPuisOk = true;
		
		int[][][] lignes = new int[5][2][6];
		
		for(int a=0; a<5; a++) lignes[a] = lignePuisCtrl(a+1);
		
		int[] puisTot = new int[] {0,0,0,0,0,0};
		int[] ctrlTot = new int[] {0,0,0,0,0,0};
		
		for(int i=0; i<puisTot.length; i++) {
			for(int a=0; a<5; a++) {
				puisTot[i] += lignes[a][0][i];
				ctrlTot[i] += lignes[a][1][i];
			}
			if(ctrlTot[i] < puisTot[i]) isCtrlOk = false;
			if(puisTot[i] < objectif.puissance[i]) isPuisOk = false;
		}
		
		if(!isCtrlOk) res += "Aïe ! Aïe ! Aïe ! Le sort n'est pas sous contrôle !\n";
		if(!isPuisOk) res += "Humpf ! Le sort n'est pas assez puissant !\n";
		
		if(isCtrlOk && isPuisOk) {
			int cout = coutTotal();
			if(cout <= objectif.objectifs[2]) {
				res += "Bravo !";
			} else if(cout <= objectif.objectifs[1]) {
				res += "Excellent !";
			} else if(cout <= objectif.objectifs[0]) {
				res += "Bien joué !";
			} else {
				res += "Peu mieux faire";
			}
		}
		// TODO Auto-generated method stub
		return new Pentacle(puisTot, ctrlTot, res);
	}

	public int coutTotal() {
		int result = 0;
		for(int i : pos.pentaPuissance)  {
			if(i>0) result += listOfIngredients.get(i-1).cout;
		}
		for(int i : pos.pentaControle)  {
			if(i>0) result += listOfIngredients.get(i-1).cout;
		}
		return result;
	}
	
	public int[] lignePuissanceBrut(int num) {
		int i=0; // id ingr 1 
		int j=0; // id ingr 2
		switch(num) {
			case 1:
				i=pos.pentaPuissance[0];
				j=pos.pentaPuissance[1];
				break;
			case 2:
				i=pos.pentaPuissance[1];
				j=pos.pentaPuissance[2];
				break;
			case 3:
				i=pos.pentaPuissance[2];
				j=pos.pentaPuissance[3];
				break;
			case 4:
				i=pos.pentaPuissance[3];
				j=pos.pentaPuissance[4];
				break;
			case 5:
				i=pos.pentaPuissance[4];
				j=pos.pentaPuissance[0];
				break;
		}
		
		if(i==0 || j==0) return new int[] {0,0,0,0,0,0};
		
		int[] ener1 = listOfIngredients.get(i-1).energies;
		int[] ener2 = listOfIngredients.get(j-1).energies;
		
		int[] resul = new int[] {0,0,0,0,0,0};
		for(int a=0; a<ener1.length; a++) {
			resul[a] = Math.min(ener1[a], ener2[a]);
		}
		
		return resul;
	}
	
	public int[] ligneControleBrut(int num) {
		int i=0;
		int j=0;
		switch(num) {
			case 1:
				i=pos.pentaControle[0];
				j=pos.pentaControle[1];
				break;
			case 2:
				i=pos.pentaControle[2];
				j=pos.pentaControle[3];
				break;
			case 3:
				i=pos.pentaControle[4];
				j=pos.pentaControle[0];
				break;
			case 4:
				i=pos.pentaControle[1];
				j=pos.pentaControle[2];
				break;
			case 5:
				i=pos.pentaControle[3];
				j=pos.pentaControle[4];
				break;
		}
		
		if(i==0 || j==0) return new int[] {0,0,0,0,0,0};

		int[] ener1 = listOfIngredients.get(i-1).energies;
		int[] ener2 = listOfIngredients.get(j-1).energies;

		int[] resul = new int[] {0,0,0,0,0,0};
		for(int a=0; a<ener1.length; a++) {
			resul[a] = Math.min(ener1[a], ener2[a]);
		}
		
		return resul;
	}
	
	public  int[][] lignePuisCtrl(int num) {
		int synergie = multLigneSynergie(num);
		int[] puis = lignePuissanceBrut(num);
		int[] ctrl = ligneControleBrut(num);
		for(int a=0; a<puis.length; a++) {
			int diff = (puis[a]-ctrl[a])*synergie;
			puis[a] = Math.max(diff, 0);
			ctrl[a] = Math.max(0, -diff);
		}
		return new int[][] {puis,ctrl};
	}
	
	public int multLigneSynergie(int num) { // return 0, 1 ou 2
		int i=0; // id ingr 1 
		int j=0; // id ingr 2
		switch(num) {
			case 1:
				i=pos.pentaPuissance[0];
				j=pos.pentaPuissance[1];
				break;
			case 2:
				i=pos.pentaPuissance[1];
				j=pos.pentaPuissance[2];
				break;
			case 3:
				i=pos.pentaPuissance[2];
				j=pos.pentaPuissance[3];
				break;
			case 4:
				i=pos.pentaPuissance[3];
				j=pos.pentaPuissance[4];
				break;
			case 5:
				i=pos.pentaPuissance[4];
				j=pos.pentaPuissance[0];
				break;
		}
		if(i==0 || j==0) return 1;
		
		Famille f1 = listOfIngredients.get(i-1).famille;
		Famille f2 = listOfIngredients.get(j-1).famille;
		
		switch(num) {
			case 1:
				i=pos.pentaControle[0];
				j=pos.pentaControle[1];
				break;
			case 2:
				i=pos.pentaControle[2];
				j=pos.pentaControle[3];
				break;
			case 3:
				i=pos.pentaControle[4];
				j=pos.pentaControle[0];
				break;
			case 4:
				i=pos.pentaControle[1];
				j=pos.pentaControle[2];
				break;
			case 5:
				i=pos.pentaControle[3];
				j=pos.pentaControle[4];
				break;
		}
		if(i==0 || j==0) return 1;

		Famille f3 = listOfIngredients.get(i-1).famille;
		Famille f4 = listOfIngredients.get(j-1).famille;

		if(f1==f2 && f2==f3 && f3==f4) return 0;
		if(f1!=f2 && f1!=f3 && f1!=f4 && f2!=f3 && f2!=f4 && f3!=f4) return 2;

		return 1;
	}

	public void setPos(Positions pos) {
		this.pos = pos;
	}
}
