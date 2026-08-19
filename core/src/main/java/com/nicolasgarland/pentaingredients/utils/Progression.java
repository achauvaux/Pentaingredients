package com.nicolasgarland.pentaingredients.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Ce que le joueur a déjà accompli : pour chaque niveau réussi, le meilleur
 * résultat obtenu.
 *
 * <p>Distincte de {@link Positions}, qui n'est qu'un brouillon en cours sur un
 * niveau. La progression, elle, ne recule jamais : rejouer un niveau moins bien
 * ne fait pas perdre les étoiles déjà décrochées.</p>
 */
public class Progression {

	/** Un niveau réussi au moins une fois. */
	public static class Resultat {
		public int niveau;
		public int etoiles;
		public int cout;

		public Resultat() {
		}

		public Resultat(int niveau, int etoiles, int cout) {
			this.niveau = niveau;
			this.etoiles = etoiles;
			this.cout = cout;
		}
	}

	private static final String FICHIER = "saves/progression.json";

	private final List<Resultat> resultats;

	public Progression() {
		this(new ArrayList<Resultat>());
	}

	public Progression(List<Resultat> resultats) {
		this.resultats = resultats;
	}

	@SuppressWarnings("unchecked")
	public static Progression charger() {
		FileHandle fichier = Gdx.files.local(FICHIER);
		if (!fichier.exists()) {
			return new Progression();
		}

		try {
			List<Resultat> lus = new Json().fromJson(List.class, Resultat.class, fichier);
			return new Progression(lus == null ? new ArrayList<Resultat>() : lus);
		} catch (RuntimeException illisible) {
			Gdx.app.log("SAUVEGARDE", "progression illisible, on repart de zéro : " + fichier.path());
			return new Progression();
		}
	}

	/**
	 * Retient un rituel réussi. Le meilleur résultat l'emporte : moins cher
	 * d'abord, et à coût égal le plus étoilé.
	 */
	public void enregistrer(int niveau, int etoiles, int cout) {
		if (retenir(niveau, etoiles, cout)) {
			sauvegarder();
		}
	}

	/**
	 * Applique la règle du meilleur résultat, sans rien écrire sur le disque.
	 * Rend vrai si la progression a effectivement changé.
	 */
	public boolean retenir(int niveau, int etoiles, int cout) {
		Resultat connu = resultatDe(niveau);

		if (connu == null) {
			resultats.add(new Resultat(niveau, etoiles, cout));
			return true;
		}
		if (cout < connu.cout || (cout == connu.cout && etoiles > connu.etoiles)) {
			connu.etoiles = etoiles;
			connu.cout = cout;
			return true;
		}
		return false;
	}

	/** Étoiles décrochées sur ce niveau, ou 0 s'il n'a jamais été réussi. */
	public int etoiles(int niveau) {
		Resultat resultat = resultatDe(niveau);
		return resultat == null ? 0 : resultat.etoiles;
	}

	/** Meilleur coût obtenu, ou 0 si le niveau n'a jamais été réussi. */
	public int meilleurCout(int niveau) {
		Resultat resultat = resultatDe(niveau);
		return resultat == null ? 0 : resultat.cout;
	}

	public boolean estReussi(int niveau) {
		return resultatDe(niveau) != null;
	}

	private Resultat resultatDe(int niveau) {
		for (Resultat resultat : resultats) {
			if (resultat.niveau == niveau) return resultat;
		}
		return null;
	}

	private void sauvegarder() {
		Json json = new Json();
		Gdx.files.local(FICHIER).writeString(
				json.prettyPrint(json.toJson(resultats, List.class, Resultat.class)), false);
	}
}
