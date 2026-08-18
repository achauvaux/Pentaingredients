package com.nicolasgarland.pentaingredients.screens;

import com.nicolasgarland.pentaingredients.actors.InventorySlot;

/**
 * Averti qu'un emplacement a été cliqué, qu'il soit sur l'étagère ou sur le
 * pentagramme.
 *
 * <p>Les deux vues fabriquent leurs emplacements mais ne décident pas de ce
 * qu'un clic déclenche : sélectionner, permuter, rafraîchir l'autre vue. Cet
 * arbitrage revient à l'écran de jeu, seul à voir les deux moitiés.</p>
 */
public interface EcouteurDeSlot {

	void slotClique(InventorySlot slot);
}
