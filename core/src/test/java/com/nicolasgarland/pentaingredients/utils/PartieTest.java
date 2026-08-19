package com.nicolasgarland.pentaingredients.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nicolasgarland.pentaingredients.utils.Ingredient.Famille;
import com.nicolasgarland.pentaingredients.utils.Positions.Emplacement;

/**
 * Une partie se construit désormais sans toucher au disque ni au contexte
 * graphique : seule {@link Partie#charger(int)} lit les assets. C'est ce qui
 * rend ces vérifications possibles.
 *
 * <p>Les méthodes qui sauvegardent ({@code deplacer}, {@code reinitialiser})
 * restent hors de portée d'un test unitaire : elles écrivent via
 * {@code Gdx.files}.</p>
 */
class PartieTest {

	private static final int PERSIL = 1, AIL = 2, PONCE = 3;

	private static Partie partie(int[] puissance, int[] controle, int[][] etagere) {
		List<Ingredient> catalogue = new ArrayList<Ingredient>();
		catalogue.add(new Ingredient(PERSIL, "Persil", Famille.VEGETALE, 100, new int[] {1, 0, 0, 0, 0, 0}));
		catalogue.add(new Ingredient(AIL, "Ail", Famille.VEGETALE, 100, new int[] {0, 1, 0, 0, 0, 0}));
		catalogue.add(new Ingredient(PONCE, "Ponce", Famille.MINERALE, 200, new int[] {2, 0, 0, 0, 0, 0}));

		Level niveau = new Level(1, "Niveau de test",
				new int[] {0, 0, 0, 0, 0, 0}, new int[] {1000, 800, 500});

		return new Partie(1, niveau, catalogue, new Positions(puissance, controle, etagere));
	}

	private static Partie partieVide() {
		return partie(new int[5], new int[5], etagereVide());
	}

	private static int[][] etagereVide() {
		return new int[Positions.RANGEES_ETAGERE][Positions.CASES_PAR_RANGEE];
	}

	@Test
	@DisplayName("L'identifiant 0 désigne un emplacement vide, pas un ingrédient")
	void identifiantZeroVautVide() {
		Partie partie = partieVide();

		assertNull(partie.ingredient(0));
		assertSame(partie.ingredients.get(0), partie.ingredient(PERSIL));
		assertSame(partie.ingredients.get(2), partie.ingredient(PONCE));
	}

	@Test
	@DisplayName("Une case d'étagère se désigne par rangée × largeur + colonne")
	void adressageDeLEtagere() {
		int largeur = Positions.CASES_PAR_RANGEE;
		int derniereRangee = Positions.RANGEES_ETAGERE - 1;

		int[][] etagere = etagereVide();
		etagere[0][0] = PERSIL;
		etagere[3][largeur - 1] = AIL;
		etagere[derniereRangee][largeur - 1] = PONCE;

		Partie partie = partie(new int[5], new int[5], etagere);

		assertEquals(PERSIL, partie.idSur(0, Emplacement.ETAGERE));
		assertEquals(AIL, partie.idSur(3 * largeur + largeur - 1, Emplacement.ETAGERE));
		assertEquals(PONCE, partie.idSur(derniereRangee * largeur + largeur - 1, Emplacement.ETAGERE),
				"la toute dernière case");
		assertEquals(0, partie.idSur(2 * largeur + 4, Emplacement.ETAGERE), "case restée vide");
	}

	@Test
	@DisplayName("L'étagère offre exactement une case par ingrédient du jeu")
	void etagereAjusteeAuCatalogue() {
		assertEquals(48, Positions.RANGEES_ETAGERE * Positions.CASES_PAR_RANGEE,
				"48 ingrédients, donc 48 cases : aucune n'a besoin de rester libre");

		Positions neuves = new Positions();
		boolean[] vus = new boolean[49];
		for (int position = 0; position < 48; position++) {
			int id = neuves.getIngr(position, Emplacement.ETAGERE);
			assertFalse(vus[id], "l'ingrédient " + id + " apparaît deux fois");
			vus[id] = true;
		}
		for (int id = 1; id <= 48; id++) {
			assertTrue(vus[id], "l'ingrédient " + id + " manque à l'étagère");
		}
	}

	@Test
	@DisplayName("Les emplacements du pentagramme se lisent par leur numéro")
	void adressageDuPentagramme() {
		Partie partie = partie(
				new int[] {0, PONCE, 0, 0, 0},
				new int[] {AIL, 0, 0, 0, 0},
				etagereVide());

		assertEquals(PONCE, partie.idSur(1, Emplacement.PUISSANCE));
		assertEquals(0, partie.idSur(0, Emplacement.PUISSANCE));
		assertEquals(AIL, partie.idSur(0, Emplacement.CONTROLE));
		assertEquals(0, partie.idSur(1, Emplacement.CONTROLE));
	}

	@Test
	@DisplayName("La partie délègue les calculs à son arbitre")
	void calculsDelegues() {
		// Ponce et Persil aux deux pointes de la ligne 1 : 1 Feu en commun.
		Partie partie = partie(
				new int[] {PONCE, PERSIL, 0, 0, 0}, new int[5], etagereVide());

		assertEquals(300, partie.coutTotal(), "200 + 100, l'étagère ne compte pas");
		assertEquals(1, partie.synergie(1), "deux familles sur quatre emplacements, dont deux vides");
		assertEquals(1, partie.energiesDeLaLigne(1)[0][0], "le minimum de 2 Feu et 1 Feu");

		Pentacle resultat = partie.valider();
		assertEquals(1, resultat.puissance[0]);
		assertTrue(resultat.assezPuissant, "le niveau de test n'exige aucune énergie");
	}
}
