package com.nicolasgarland.pentaingredients.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nicolasgarland.pentaingredients.utils.Ingredient.Famille;

/**
 * Filet de sécurité sur le moteur de règles.
 *
 * <p>Rappel de la topologie encodée par {@link Arbitre} : la ligne 1 relie les
 * emplacements de puissance 0 et 1, et porte les emplacements de contrôle 0 et
 * 1. La ligne 4 relie les emplacements de puissance 3 et 4, et porte les
 * emplacements de contrôle 1 et 2. Les tests s'appuient sur ces deux lignes,
 * les autres restant vides.</p>
 */
class ArbitreTest {

	// Index des énergies dans le tableau energies[]
	private static final int FEU = 0, TERRE = 1, FOUDRE = 2;

	// Catalogue de test : l'indice dans la liste vaut identifiant - 1.
	private static final int DRAGON = 1;          // MAGIQUE  700   3 Feu, 3 Terre
	private static final int SALAMANDRE = 2;      // ANIMALE  700   3 Feu, 1 Foudre, 1 Esprit
	private static final int PONCE = 3;           // MINERALE 200   2 Feu
	private static final int PERSIL = 4;          // VEGETALE 100   1 Feu
	private static final int AIL = 5;             // VEGETALE 100   1 Terre
	private static final int MAGIQUE_A = 6;       // MAGIQUE  300   2 Feu
	private static final int MAGIQUE_B = 7;       // MAGIQUE  300   2 Feu
	private static final int MAGIQUE_C = 8;       // MAGIQUE  300   2 Feu
	private static final int MAGIQUE_D = 9;       // MAGIQUE  300   2 Feu
	private static final int FEU2_ANIMALE = 10;   // ANIMALE  400   2 Feu
	private static final int FEU1_ANIMALE = 11;   // ANIMALE  150   1 Feu
	private static final int FEU1_MINERALE = 12;  // MINERALE 150   1 Feu

	private static final int[] RIEN = {0, 0, 0, 0, 0, 0};

	private static List<Ingredient> catalogue() {
		List<Ingredient> liste = new ArrayList<Ingredient>();
		liste.add(new Ingredient(DRAGON, "Ecaille de dragon", Famille.MAGIQUE, 700, energies(3, 3, 0, 0, 0, 0)));
		liste.add(new Ingredient(SALAMANDRE, "Langue de salamandre", Famille.ANIMALE, 700, energies(3, 0, 1, 0, 0, 1)));
		liste.add(new Ingredient(PONCE, "Pierre ponce", Famille.MINERALE, 200, energies(2, 0, 0, 0, 0, 0)));
		liste.add(new Ingredient(PERSIL, "Persil en branche", Famille.VEGETALE, 100, energies(1, 0, 0, 0, 0, 0)));
		liste.add(new Ingredient(AIL, "Gousse d ail", Famille.VEGETALE, 100, energies(0, 1, 0, 0, 0, 0)));
		liste.add(new Ingredient(MAGIQUE_A, "Magique A", Famille.MAGIQUE, 300, energies(2, 0, 0, 0, 0, 0)));
		liste.add(new Ingredient(MAGIQUE_B, "Magique B", Famille.MAGIQUE, 300, energies(2, 0, 0, 0, 0, 0)));
		liste.add(new Ingredient(MAGIQUE_C, "Magique C", Famille.MAGIQUE, 300, energies(2, 0, 0, 0, 0, 0)));
		liste.add(new Ingredient(MAGIQUE_D, "Magique D", Famille.MAGIQUE, 300, energies(2, 0, 0, 0, 0, 0)));
		liste.add(new Ingredient(FEU2_ANIMALE, "Animale 2 feu", Famille.ANIMALE, 400, energies(2, 0, 0, 0, 0, 0)));
		liste.add(new Ingredient(FEU1_ANIMALE, "Animale 1 feu", Famille.ANIMALE, 150, energies(1, 0, 0, 0, 0, 0)));
		liste.add(new Ingredient(FEU1_MINERALE, "Minerale 1 feu", Famille.MINERALE, 150, energies(1, 0, 0, 0, 0, 0)));
		return liste;
	}

	private static int[] energies(int feu, int terre, int foudre, int eau, int vent, int esprit) {
		return new int[] {feu, terre, foudre, eau, vent, esprit};
	}

	/** Pentagramme vide, sauf aux emplacements précisés. */
	private static Positions plateau(int[] puissance, int[] controle) {
		return new Positions(puissance, controle, new int[10][10]);
	}

	private static Arbitre arbitre(Level niveau, int[] puissance, int[] controle) {
		return new Arbitre(niveau, catalogue(), plateau(puissance, controle));
	}

	private static Level niveau(int[] puissanceRequise, int[] objectifs) {
		return new Level(1, "Niveau de test", puissanceRequise, objectifs);
	}

	// ---------------------------------------------------------- règle du minimum

	@Test
	@DisplayName("Une paire produit le minimum de chaque énergie, pas la somme")
	void paireProduitLeMinimum() {
		Arbitre arbitre = arbitre(niveau(RIEN, new int[] {0, 0, 0}),
				new int[] {DRAGON, SALAMANDRE, 0, 0, 0}, new int[] {0, 0, 0, 0, 0});

		// Dragon : 3 Feu, 3 Terre. Salamandre : 3 Feu, 1 Foudre, 1 Esprit.
		// Seul le Feu est commun aux deux.
		assertArrayEquals(energies(3, 0, 0, 0, 0, 0), arbitre.lignePuissanceBrut(1));
	}

	@Test
	@DisplayName("Deux ingrédients sans énergie commune ne produisent rien")
	void aucuneEnergieCommune() {
		Arbitre arbitre = arbitre(niveau(RIEN, new int[] {0, 0, 0}),
				new int[] {PERSIL, AIL, 0, 0, 0}, new int[] {0, 0, 0, 0, 0});

		assertArrayEquals(RIEN, arbitre.lignePuissanceBrut(1));
	}

	@Test
	@DisplayName("Une ligne dont un emplacement est vide ne produit rien")
	void ligneIncomplete() {
		Arbitre arbitre = arbitre(niveau(RIEN, new int[] {0, 0, 0}),
				new int[] {DRAGON, 0, 0, 0, 0}, new int[] {0, 0, 0, 0, 0});

		assertArrayEquals(RIEN, arbitre.lignePuissanceBrut(1));
	}

	// ------------------------------------------------------ synergie des familles

	@Test
	@DisplayName("Quatre familles différentes doublent la ligne")
	void synergieDouble() {
		// Magique, Animale aux pointes ; Minérale, Végétale au milieu.
		Arbitre arbitre = arbitre(niveau(RIEN, new int[] {0, 0, 0}),
				new int[] {DRAGON, SALAMANDRE, 0, 0, 0}, new int[] {PONCE, PERSIL, 0, 0, 0});

		assertEquals(2, arbitre.multLigneSynergie(1));

		// puissance 3 Feu - contrôle 1 Feu = 2, doublé = 4
		int[][] ligne = arbitre.lignePuisCtrl(1);
		assertArrayEquals(energies(4, 0, 0, 0, 0, 0), ligne[0], "puissance de la ligne");
		assertArrayEquals(RIEN, ligne[1], "contrôle de la ligne");
	}

	@Test
	@DisplayName("Quatre ingrédients d'une même famille annulent la ligne")
	void synergieNulle() {
		Arbitre arbitre = arbitre(niveau(RIEN, new int[] {0, 0, 0}),
				new int[] {MAGIQUE_A, MAGIQUE_B, 0, 0, 0}, new int[] {MAGIQUE_C, MAGIQUE_D, 0, 0, 0});

		assertEquals(0, arbitre.multLigneSynergie(1));

		int[][] ligne = arbitre.lignePuisCtrl(1);
		assertArrayEquals(RIEN, ligne[0], "puissance de la ligne");
		assertArrayEquals(RIEN, ligne[1], "contrôle de la ligne");
	}

	@Test
	@DisplayName("Un mélange de familles laisse la ligne inchangée")
	void synergieNeutre() {
		// Magique, Magique aux pointes ; Animale, Minérale au milieu :
		// ni toutes identiques, ni toutes différentes.
		Arbitre arbitre = arbitre(niveau(RIEN, new int[] {0, 0, 0}),
				new int[] {MAGIQUE_A, MAGIQUE_B, 0, 0, 0}, new int[] {FEU1_ANIMALE, FEU1_MINERALE, 0, 0, 0});

		assertEquals(1, arbitre.multLigneSynergie(1));

		// puissance 2 Feu - contrôle 1 Feu = 1, inchangé
		assertArrayEquals(energies(1, 0, 0, 0, 0, 0), arbitre.lignePuisCtrl(1)[0]);
	}

	// ------------------------------------------------------ puissance / contrôle

	@Test
	@DisplayName("Sur une même ligne, le contrôle annule la puissance")
	void controleAnnuleLaPuissanceDeSaLigne() {
		// Magique, Magique / Magique, Animale : synergie neutre, 2 Feu contre 2 Feu.
		Arbitre arbitre = arbitre(niveau(RIEN, new int[] {0, 0, 0}),
				new int[] {MAGIQUE_A, MAGIQUE_B, 0, 0, 0}, new int[] {MAGIQUE_C, FEU2_ANIMALE, 0, 0, 0});

		int[][] ligne = arbitre.lignePuisCtrl(1);
		assertArrayEquals(RIEN, ligne[0], "la puissance est entièrement absorbée");
		assertArrayEquals(RIEN, ligne[1], "et il ne reste aucun contrôle");
	}

	@Test
	@DisplayName("Le contrôle excédentaire d'une ligne compte comme contrôle")
	void controleExcedentaire() {
		// Ligne 4 : emplacements de puissance 3 et 4 vides, contrôle en 1 et 2.
		Arbitre arbitre = arbitre(niveau(RIEN, new int[] {0, 0, 0}),
				new int[] {0, 0, 0, 0, 0}, new int[] {0, DRAGON, SALAMANDRE, 0, 0});

		int[][] ligne = arbitre.lignePuisCtrl(4);
		assertArrayEquals(RIEN, ligne[0], "aucune puissance");
		assertArrayEquals(energies(3, 0, 0, 0, 0, 0), ligne[1], "3 Feu de contrôle");
	}

	// -------------------------------------------------------------- verdict final

	@Test
	@DisplayName("Rituel réussi : assez puissant et sous contrôle")
	void rituelReussi() {
		// Ligne 1 : 2 Feu de puissance. Ligne 4 : 3 Feu de contrôle.
		Level niveau = niveau(energies(2, 0, 0, 0, 0, 0), new int[] {3000, 2500, 2000});
		Arbitre arbitre = arbitre(niveau,
				new int[] {PONCE, FEU2_ANIMALE, 0, 0, 0}, new int[] {0, DRAGON, SALAMANDRE, 0, 0});

		Pentacle resultat = arbitre.validerPentacle();

		assertArrayEquals(energies(2, 0, 0, 0, 0, 0), resultat.puissance, "puissance totale");
		assertArrayEquals(energies(3, 0, 0, 0, 0, 0), resultat.controle, "contrôle total");
		// TODO étape 4 : le verdict devrait être structuré, pas rédigé en français
		// par le moteur. Ce test portera alors sur un rang, pas sur du texte.
		assertTrue(resultat.description.contains("Bravo"),
				"coût de 2000, sous le meilleur seuil, mais message : " + resultat.description);
	}

	@Test
	@DisplayName("Rituel trop faible : la puissance requise n'est pas atteinte")
	void rituelTropFaible() {
		Level niveau = niveau(energies(5, 0, 0, 0, 0, 0), new int[] {3000, 2500, 2000});
		Arbitre arbitre = arbitre(niveau,
				new int[] {PONCE, FEU2_ANIMALE, 0, 0, 0}, new int[] {0, DRAGON, SALAMANDRE, 0, 0});

		assertTrue(arbitre.validerPentacle().description.contains("pas assez puissant"));
	}

	@Test
	@DisplayName("Rituel incontrôlé : la puissance dépasse le contrôle")
	void rituelIncontrole() {
		Level niveau = niveau(energies(2, 0, 0, 0, 0, 0), new int[] {3000, 2500, 2000});
		// 2 Feu de puissance, aucun contrôle nulle part.
		Arbitre arbitre = arbitre(niveau,
				new int[] {PONCE, FEU2_ANIMALE, 0, 0, 0}, new int[] {0, 0, 0, 0, 0});

		assertTrue(arbitre.validerPentacle().description.contains("sous contrôle"));
	}

	// ------------------------------------------------------------------- coût

	@Test
	@DisplayName("Le coût ne compte que les ingrédients posés sur le pentagramme")
	void coutTotal() {
		int[][] etagere = new int[10][10];
		etagere[0][0] = DRAGON; // resté sur l'étagère : gratuit
		Positions plateau = new Positions(
				new int[] {PONCE, FEU2_ANIMALE, 0, 0, 0}, new int[] {0, DRAGON, SALAMANDRE, 0, 0}, etagere);
		Arbitre arbitre = new Arbitre(niveau(RIEN, new int[] {0, 0, 0}), catalogue(), plateau);

		// 200 + 400 + 700 + 700
		assertEquals(2000, arbitre.coutTotal());
	}

	@Test
	@DisplayName("Les énergies produites sans être demandées doivent aussi être maîtrisées")
	void energieParasiteDoitEtreControlee() {
		Level niveau = niveau(energies(3, 0, 0, 0, 0, 0), new int[] {9000, 8000, 7000});
		Arbitre arbitre = arbitre(niveau,
				new int[] {DRAGON, SALAMANDRE, 0, 0, 0}, new int[] {0, 0, 0, 0, 0});

		Pentacle resultat = arbitre.validerPentacle();

		assertEquals(3, resultat.puissance[FEU]);
		assertEquals(0, resultat.puissance[TERRE], "la Terre n'est pas commune aux deux ingrédients");
		assertEquals(0, resultat.puissance[FOUDRE], "la Foudre non plus");
		assertTrue(resultat.description.contains("sous contrôle"), "aucun contrôle n'a été posé");
	}
}
