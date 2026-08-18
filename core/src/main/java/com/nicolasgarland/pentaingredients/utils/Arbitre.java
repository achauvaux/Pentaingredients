package com.nicolasgarland.pentaingredients.utils;

import java.util.List;

import com.nicolasgarland.pentaingredients.utils.Ingredient.Famille;

public class Arbitre {

	/**
	 * Topologie du pentagramme. L'étoile se trace d'un seul trait : elle est
	 * faite de cinq lignes, et chaque ligne porte quatre emplacements.
	 *
	 * <p>Pour la ligne n (1 à 5), {@code POINTES[n-1]} donne les deux
	 * emplacements de puissance situés à ses extrémités, et
	 * {@code MILIEUX[n-1]} les deux emplacements de contrôle qu'elle traverse.
	 * Chaque emplacement apparaît exactement deux fois : c'est ce partage qui
	 * fait le casse-tête.</p>
	 */
	private static final int[][] POINTES = {
		{0, 1},   // ligne 1
		{1, 2},   // ligne 2
		{2, 3},   // ligne 3
		{3, 4},   // ligne 4
		{4, 0},   // ligne 5
	};

	private static final int[][] MILIEUX = {
		{0, 1},   // ligne 1
		{2, 3},   // ligne 2
		{4, 0},   // ligne 3
		{1, 2},   // ligne 4
		{3, 4},   // ligne 5
	};

	/** Multiplicateurs rendus par {@link #multLigneSynergie(int)}. */
	public static final int SYNERGIE_NULLE = 0;
	public static final int SYNERGIE_NEUTRE = 1;
	public static final int SYNERGIE_DOUBLE = 2;

	public static final int NB_LIGNES = 5;
	public static final int NB_ENERGIES = 6;
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
		
		int[][][] lignes = new int[NB_LIGNES][2][NB_ENERGIES];
		
		for(int a=0; a<NB_LIGNES; a++) lignes[a] = lignePuisCtrl(a+1);
		
		int[] puisTot = new int[] {0,0,0,0,0,0};
		int[] ctrlTot = new int[] {0,0,0,0,0,0};
		
		for(int i=0; i<puisTot.length; i++) {
			for(int a=0; a<NB_LIGNES; a++) {
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
	
	/**
	 * Énergies produites par une paire d'ingrédients : pour chaque énergie, la
	 * plus petite des deux valeurs. Une paire incomplète ne produit rien.
	 */
	private int[] combinaison(int idPremier, int idSecond) {
		int[] resultat = new int[NB_ENERGIES];
		if(idPremier == 0 || idSecond == 0) return resultat;

		int[] premier = listOfIngredients.get(idPremier - 1).energies;
		int[] second = listOfIngredients.get(idSecond - 1).energies;
		for(int e = 0; e < resultat.length; e++) {
			resultat[e] = Math.min(premier[e], second[e]);
		}
		return resultat;
	}

	private Famille familleDe(int idIngredient) {
		return listOfIngredients.get(idIngredient - 1).famille;
	}

	public int[] lignePuissanceBrut(int num) {
		int[] pointes = POINTES[num - 1];
		return combinaison(pos.pentaPuissance[pointes[0]], pos.pentaPuissance[pointes[1]]);
	}

	public int[] ligneControleBrut(int num) {
		int[] milieux = MILIEUX[num - 1];
		return combinaison(pos.pentaControle[milieux[0]], pos.pentaControle[milieux[1]]);
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
	
	/**
	 * Multiplicateur de la ligne, lu sur les familles de ses quatre
	 * ingrédients : nul si les quatre appartiennent à la même famille, double
	 * si les quatre familles sont toutes différentes, neutre sinon. Une ligne
	 * incomplète reste neutre.
	 */
	public int multLigneSynergie(int num) {
		int[] pointes = POINTES[num - 1];
		int idPointeA = pos.pentaPuissance[pointes[0]];
		int idPointeB = pos.pentaPuissance[pointes[1]];
		if(idPointeA == 0 || idPointeB == 0) return SYNERGIE_NEUTRE;

		int[] milieux = MILIEUX[num - 1];
		int idMilieuA = pos.pentaControle[milieux[0]];
		int idMilieuB = pos.pentaControle[milieux[1]];
		if(idMilieuA == 0 || idMilieuB == 0) return SYNERGIE_NEUTRE;

		Famille f1 = familleDe(idPointeA);
		Famille f2 = familleDe(idPointeB);
		Famille f3 = familleDe(idMilieuA);
		Famille f4 = familleDe(idMilieuB);

		if(f1 == f2 && f2 == f3 && f3 == f4) return SYNERGIE_NULLE;
		if(f1 != f2 && f1 != f3 && f1 != f4 && f2 != f3 && f2 != f4 && f3 != f4) return SYNERGIE_DOUBLE;

		return SYNERGIE_NEUTRE;
	}

	public void setPos(Positions pos) {
		this.pos = pos;
	}
}
