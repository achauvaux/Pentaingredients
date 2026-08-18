package com.nicolasgarland.pentaingredients.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
		return partie(new int[5], new int[5], new int[10][10]);
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
	@DisplayName("Une case d'étagère se désigne par rangée × 10 + colonne")
	void adressageDeLEtagere() {
		int[][] etagere = new int[10][10];
		etagere[0][0] = PERSIL;
		etagere[3][7] = AIL;
		etagere[9][9] = PONCE;

		Partie partie = partie(new int[5], new int[5], etagere);

		assertEquals(PERSIL, partie.idSur(0, Emplacement.ETAGERE));
		assertEquals(AIL, partie.idSur(3 * 10 + 7, Emplacement.ETAGERE));
		assertEquals(PONCE, partie.idSur(9 * 10 + 9, Emplacement.ETAGERE));
		assertEquals(0, partie.idSur(42, Emplacement.ETAGERE), "case restée vide");
	}

	@Test
	@DisplayName("Les emplacements du pentagramme se lisent par leur numéro")
	void adressageDuPentagramme() {
		Partie partie = partie(
				new int[] {0, PONCE, 0, 0, 0},
				new int[] {AIL, 0, 0, 0, 0},
				new int[10][10]);

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
				new int[] {PONCE, PERSIL, 0, 0, 0}, new int[5], new int[10][10]);

		assertEquals(300, partie.coutTotal(), "200 + 100, l'étagère ne compte pas");
		assertEquals(1, partie.synergie(1), "deux familles sur quatre emplacements, dont deux vides");
		assertEquals(1, partie.energiesDeLaLigne(1)[0][0], "le minimum de 2 Feu et 1 Feu");

		Pentacle resultat = partie.valider();
		assertEquals(1, resultat.puissance[0]);
		assertTrue(resultat.assezPuissant, "le niveau de test n'exige aucune énergie");
	}
}
