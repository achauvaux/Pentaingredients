package com.nicolasgarland.pentaingredients.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * La direction artistique du jeu, en un seul endroit : « grimoire à la
 * chandelle ». Un noir chaud pour le fond, de l'or pour le tracé, et deux
 * couleurs opposées pour les deux forces du rituel.
 *
 * <p>La braise et le givre ne sont pas décoratifs : ils portent la mécanique.
 * Tout ce qui produit de la puissance tire vers le chaud, tout ce qui la
 * maîtrise vers le froid.</p>
 */
public final class Palette {

	/** Tracé du pentagramme et accents. */
	public static final Color OR = new Color(0xD8A33Aff);

	/** Puissance : les pointes de l'étoile, la lueur du rituel. */
	public static final Color BRAISE = new Color(0xD2563Bff);

	/** Contrôle : les emplacements intérieurs. */
	public static final Color GIVRE = new Color(0x6FA8B4ff);

	/** Trait d'une ligne ordinaire, ni annulée ni doublée. */
	public static final Color PARCHEMIN = new Color(0xEDE3CEff);

	/** Trait d'une ligne éteinte par quatre familles identiques. */
	public static final Color CENDRE = new Color(0x584F45ff);

	/** Cadres et bordures discrètes. */
	public static final Color LAITON = new Color(0x8C6A22ff);

	/**
	 * Teinte des boutons et des cadres de la skin. Elle se multiplie au gris
	 * d'origine, ce qui suffit à faire virer l'habillage au laiton sans avoir à
	 * redessiner quoi que ce soit.
	 */
	public static final Color HABILLAGE = new Color(0xE0B878ff);

	/** Fond des fenêtres d'information. */
	public static final Color PANNEAU = new Color(0xA8814Cff);

	/** Une étoile non encore décrochée : un objectif, pas un trophée. */
	public static final Color ETOILE_ETEINTE = new Color(0x7A6234ff);

	/** Une case d'étagère qui attend un ingrédient. */
	public static final Color CASE_VIDE = new Color(0x4A3826ff);

	/** Une case d'étagère occupée. */
	public static final Color CASE_PLEINE = new Color(0xB07A2Aff);

	/**
	 * Style de titre, en or plutôt que dans le rouge de la skin.
	 *
	 * <p>Passer par un style copié est nécessaire : {@code setColor} sur un Label
	 * <em>multiplie</em> la couleur de son style, et aucune multiplication ne
	 * transforme du rouge pur en or.</p>
	 */
	public static Label.LabelStyle titre(Skin skin) {
		Label.LabelStyle style = new Label.LabelStyle(skin.get("title", Label.LabelStyle.class));
		style.fontColor = OR;
		return style;
	}

	private Palette() {
	}
}
