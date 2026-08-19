package com.nicolasgarland.pentaingredients.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Où se trouve chaque ingrédient : sur une pointe du pentagramme, sur un de ses
 * emplacements intérieurs, ou en réserve sur l'étagère.
 *
 * <p>L'étagère compte exactement autant de cases que le jeu compte
 * d'ingrédients. Une case n'a jamais besoin de rester libre : déplacer un
 * ingrédient revient toujours à en échanger deux, et poser sur le pentagramme
 * libère la case d'origine.</p>
 */
public class Positions {

	public static enum Emplacement {
		PUISSANCE,
		CONTROLE,
		ETAGERE
	}

	/** Dossier des sauvegardes, relatif au dossier de lancement du jeu. */
	private static final String DOSSIER = "saves/";

	/** Nombre d'emplacements de puissance, et autant de contrôle. */
	public static final int EMPLACEMENTS_PENTAGRAMME = 5;

	public static final int RANGEES_ETAGERE = 6;
	public static final int CASES_PAR_RANGEE = 8;

	public int[] pentaPuissance;
	public int[] pentaControle;
	public int[][] etagere;

	/** Pentagramme vide, tous les ingrédients rangés dans l'ordre. */
	public Positions() {
		this.pentaPuissance = new int[EMPLACEMENTS_PENTAGRAMME];
		this.pentaControle = new int[EMPLACEMENTS_PENTAGRAMME];
		this.etagere = new int[RANGEES_ETAGERE][CASES_PAR_RANGEE];

		int ingredient = 1;
		for (int rangee = 0; rangee < RANGEES_ETAGERE; rangee++) {
			for (int colonne = 0; colonne < CASES_PAR_RANGEE; colonne++) {
				etagere[rangee][colonne] = ingredient++;
			}
		}
	}

	public Positions(int[] puissance, int[] controle, int[][] etagere) {
		this.pentaPuissance = puissance;
		this.pentaControle = controle;
		this.etagere = etagere;
	}

	public int getIngr(int pos, Emplacement empl) {
		switch (empl) {
			case PUISSANCE:
				return pentaPuissance[pos];
			case CONTROLE:
				return pentaControle[pos];
			case ETAGERE:
				return etagere[pos / CASES_PAR_RANGEE][pos % CASES_PAR_RANGEE];
		}
		return 0;
	}

	public void setIngr(int pos, Emplacement empl, int ing) {
		switch (empl) {
			case PUISSANCE:
				pentaPuissance[pos] = ing;
				break;
			case CONTROLE:
				pentaControle[pos] = ing;
				break;
			case ETAGERE:
				etagere[pos / CASES_PAR_RANGEE][pos % CASES_PAR_RANGEE] = ing;
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
		FileHandle fichier = Gdx.files.local(chemin(idLevel));
		fichier.writeString(new Json().prettyPrint(this), false);
	}

	/**
	 * Disposition sauvegardée pour ce niveau, ou une disposition neuve.
	 *
	 * <p>Un fichier écrit par une version antérieure peut décrire une étagère
	 * d'une autre taille. Plutôt que de laisser le jeu s'écrouler sur un indice
	 * hors bornes, on repart d'une disposition propre.</p>
	 */
	public static Positions charger(int idLevel) {
		FileHandle fichier = Gdx.files.local(chemin(idLevel));
		if (!fichier.exists()) {
			return new Positions();
		}

		Positions positions;
		try {
			positions = new Json().fromJson(Positions.class, fichier);
		} catch (RuntimeException illisible) {
			Gdx.app.log("SAUVEGARDE", "fichier illisible, disposition neuve : " + fichier.path());
			return new Positions();
		}

		if (positions == null || !positions.estCoherente()) {
			Gdx.app.log("SAUVEGARDE", "format inattendu, disposition neuve : " + fichier.path());
			return new Positions();
		}
		return positions;
	}

	private boolean estCoherente() {
		if (pentaPuissance == null || pentaPuissance.length != EMPLACEMENTS_PENTAGRAMME) return false;
		if (pentaControle == null || pentaControle.length != EMPLACEMENTS_PENTAGRAMME) return false;
		if (etagere == null || etagere.length != RANGEES_ETAGERE) return false;
		for (int[] rangee : etagere) {
			if (rangee == null || rangee.length != CASES_PAR_RANGEE) return false;
		}
		return true;
	}

	private static String chemin(int idLevel) {
		return DOSSIER + "disposition" + idLevel + ".json";
	}
}
