package com.nicolasgarland.pentaingredients.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * La règle du meilleur résultat, éprouvée sans toucher au disque :
 * {@link Progression#retenir} applique la décision, {@code enregistrer} se
 * contente d'y ajouter l'écriture.
 */
class ProgressionTest {

	@Test
	@DisplayName("Un niveau jamais réussi ne rapporte aucune étoile")
	void niveauJamaisReussi() {
		Progression progression = new Progression();

		assertFalse(progression.estReussi(3));
		assertEquals(0, progression.etoiles(3));
		assertEquals(0, progression.meilleurCout(3));
	}

	@Test
	@DisplayName("Un premier succès est toujours retenu")
	void premierSucces() {
		Progression progression = new Progression();

		assertTrue(progression.retenir(3, 2, 4200));

		assertTrue(progression.estReussi(3));
		assertEquals(2, progression.etoiles(3));
		assertEquals(4200, progression.meilleurCout(3));
	}

	@Test
	@DisplayName("Un rituel moins cher remplace le précédent")
	void moinsCherLEmporte() {
		Progression progression = new Progression();
		progression.retenir(3, 2, 4200);

		assertTrue(progression.retenir(3, 3, 3800));

		assertEquals(3, progression.etoiles(3));
		assertEquals(3800, progression.meilleurCout(3));
	}

	@Test
	@DisplayName("Une partie moins bonne ne fait pas perdre les étoiles acquises")
	void moinsBonNeRegressePas() {
		Progression progression = new Progression();
		progression.retenir(3, 3, 3800);

		assertFalse(progression.retenir(3, 1, 4900), "rien de mieux, donc rien à écrire");

		assertEquals(3, progression.etoiles(3));
		assertEquals(3800, progression.meilleurCout(3));
	}

	@Test
	@DisplayName("À coût égal, le rituel le plus étoilé l'emporte")
	void coutEgalPlusEtoile() {
		Progression progression = new Progression();
		progression.retenir(3, 1, 4000);

		assertTrue(progression.retenir(3, 3, 4000));

		assertEquals(3, progression.etoiles(3));
	}

	@Test
	@DisplayName("Les niveaux sont indépendants les uns des autres")
	void niveauxIndependants() {
		Progression progression = new Progression();
		progression.retenir(1, 3, 3000);
		progression.retenir(2, 1, 8000);

		assertEquals(3, progression.etoiles(1));
		assertEquals(1, progression.etoiles(2));
		assertEquals(0, progression.etoiles(3));
	}
}
