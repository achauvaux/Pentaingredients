package com.nicolasgarland.pentaingredients.utils;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import com.nicolasgarland.pentaingredients.utils.Positions.Emplacement;

/**
 * Une partie en cours sur un niveau : le niveau visé, le catalogue
 * d'ingrédients, la disposition du joueur, et l'arbitre qui les juge.
 *
 * <p>Réunit ce qui était éparpillé dans l'écran de jeu : le chargement des
 * données, le déplacement d'un ingrédient et la sauvegarde qui l'accompagne.
 * L'écran n'a plus qu'à afficher et à transmettre les clics.</p>
 *
 * <p>Le constructeur ne touche pas au disque : seule {@link #charger(int)} lit
 * les assets, ce qui laisse la partie constructible dans un test.</p>
 */
public class Partie {

	public final int numeroNiveau;
	public final Level niveau;
	public final List<Ingredient> ingredients;

	private Positions positions;
	private final Arbitre arbitre;

	public Partie(int numeroNiveau, Level niveau, List<Ingredient> ingredients, Positions positions) {
		this.numeroNiveau = numeroNiveau;
		this.niveau = niveau;
		this.ingredients = ingredients;
		this.positions = positions;
		this.arbitre = new Arbitre(niveau, ingredients, positions);
	}

	/** Charge le niveau, le catalogue d'ingrédients et la disposition sauvegardée. */
	@SuppressWarnings("unchecked")
	public static Partie charger(int numeroNiveau) {
		Json json = new Json();

		Level niveau = null;
		String chemin = "assets/levels/level" + numeroNiveau + ".json";
		if (Gdx.files.internal(chemin).exists()) {
			niveau = json.fromJson(Level.class, Gdx.files.internal(chemin));
		} else {
			Gdx.app.log("ERROR", "in loading level : " + Gdx.files.internal(chemin).file().getAbsolutePath());
		}

		List<Ingredient> ingredients = null;
		chemin = "assets/ingredients.lst";
		if (Gdx.files.internal(chemin).exists()) {
			ingredients = json.fromJson(List.class, Ingredient.class, Gdx.files.internal(chemin));
		} else {
			Gdx.app.log("ERROR", "in loading ingredients list : " + Gdx.files.internal(chemin).file().getAbsolutePath());
		}

		return new Partie(numeroNiveau, niveau, ingredients, Positions.charger(numeroNiveau));
	}

	/** Existe-t-il un niveau portant ce numéro ? */
	public static boolean niveauExiste(int numeroNiveau) {
		return Gdx.files.internal("assets/levels/level" + numeroNiveau + ".json").exists();
	}

	// ------------------------------------------------------------- disposition

	/** Identifiant de l'ingrédient occupant cet emplacement, ou 0 si vide. */
	public int idSur(int position, Emplacement emplacement) {
		return positions.getIngr(position, emplacement);
	}

	/** Ingrédient portant cet identifiant, ou {@code null} pour l'identifiant 0. */
	public Ingredient ingredient(int idIngredient) {
		if (idIngredient == 0) return null;
		return ingredients.get(idIngredient - 1);
	}

	/** Échange deux emplacements, puis sauvegarde aussitôt. */
	public void deplacer(int position1, Emplacement emplacement1, int position2, Emplacement emplacement2) {
		positions.swapIngr(position1, emplacement1, position2, emplacement2);
		sauvegarder();
		arbitre.setPos(positions);
	}

	/** Renvoie tous les ingrédients sur l'étagère, et sauvegarde. */
	public void reinitialiser() {
		positions = new Positions();
		sauvegarder();
		arbitre.setPos(positions);
	}

	public void sauvegarder() {
		positions.savePosition(numeroNiveau);
	}

	// ------------------------------------------------------------------ calculs

	public int coutTotal() {
		return arbitre.coutTotal();
	}

	/** Multiplicateur de la ligne : 0, 1 ou 2. Voir {@link Arbitre}. */
	public int synergie(int ligne) {
		return arbitre.multLigneSynergie(ligne);
	}

	/** Énergies nettes de la ligne : indice 0 la puissance, indice 1 le contrôle. */
	public int[][] energiesDeLaLigne(int ligne) {
		return arbitre.lignePuisCtrl(ligne);
	}

	public Pentacle valider() {
		return arbitre.validerPentacle();
	}
}
