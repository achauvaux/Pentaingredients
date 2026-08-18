package com.nicolasgarland.pentaingredients.utils;

/**
 * Résultat d'une incantation : ce que le pentagramme a produit, et le verdict
 * qu'en tire le moteur de règles.
 *
 * <p>Volontairement dépourvu de texte. La formulation destinée au joueur
 * appartient à l'écran, pas au moteur : on peut ainsi vérifier les règles sur
 * des valeurs plutôt que sur des phrases, et changer un message sans toucher au
 * calcul.</p>
 */
public class Pentacle {

	/** Nombre d'étoiles maximal, soit le nombre de seuils de coût d'un niveau. */
	public static final int ETOILES_MAX = 3;

	/** Puissance totale produite, énergie par énergie. */
	public final int[] puissance;

	/** Contrôle total produit, énergie par énergie. */
	public final int[] controle;

	/** La puissance atteint celle qu'exige le niveau, sur chacune des énergies. */
	public final boolean assezPuissant;

	/** Le contrôle égale ou dépasse la puissance, sur chacune des énergies. */
	public final boolean sousControle;

	/** Coût des seuls ingrédients posés sur le pentagramme. */
	public final int cout;

	/** De 0 à {@link #ETOILES_MAX} selon le coût, et toujours 0 si le rituel échoue. */
	public final int etoiles;

	public Pentacle(int[] puissance, int[] controle,
			boolean assezPuissant, boolean sousControle, int cout, int etoiles) {
		this.puissance = puissance;
		this.controle = controle;
		this.assezPuissant = assezPuissant;
		this.sousControle = sousControle;
		this.cout = cout;
		this.etoiles = etoiles;
	}

	/** Le rituel est réussi s'il est à la fois assez puissant et sous contrôle. */
	public boolean estReussi() {
		return assezPuissant && sousControle;
	}
}
