package com.nicolasgarland.pentaingredients.graphics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;

/**
 * Les images de l'écran de jeu, chargées d'un bloc et libérées d'un bloc.
 *
 * <p>Toute texture passe par {@link #texture(String)}, qui la retient pour
 * {@link #dispose()} : une image ajoutée plus tard ne peut donc pas être
 * oubliée au moment de libérer. L'écran de jeu est reconstruit à chaque
 * niveau, chaque réinitialisation et chaque retour depuis la sélection, ce qui
 * rend l'oubli coûteux.</p>
 */
public class RessourcesJeu implements Disposable {

	/** Une icône par énergie, dans l'ordre feu, terre, foudre, eau, vent, esprit. */
	public final TextureRegion[] energies;

	/** Case vide de l'étagère. */
	public final TextureRegion caseVide;

	/** Emplacement du pentagramme. */
	public final TextureRegion emplacement;

	public final TextureRegion traitNul;
	public final TextureRegion traitNeutre;
	public final TextureRegion traitDouble;

	public final Texture etoile;
	public final Texture pentagramme;

	/** Chargé pour le fond d'écran encore à faire ; l'écran ne le dessine pas. */
	public final Texture fond;

	private final List<Texture> chargees = new ArrayList<Texture>();

	public RessourcesJeu() {
		this.energies = new TextureRegion[] {
				new TextureRegion(texture("assets/skin/fire.png")),
				new TextureRegion(texture("assets/skin/earth.png")),
				new TextureRegion(texture("assets/skin/lightning.png")),
				new TextureRegion(texture("assets/skin/water.png")),
				new TextureRegion(texture("assets/skin/wind.png")),
				new TextureRegion(texture("assets/skin/spirit.png"))
		};
		this.caseVide = new TextureRegion(texture("assets/skin/slot.png"));
		this.traitNul = new TextureRegion(texture("assets/skin/line-noir.png"));
		this.traitNeutre = new TextureRegion(texture("assets/skin/line.png"));
		this.traitDouble = new TextureRegion(texture("assets/skin/line-rouge.png"));
		this.emplacement = new TextureRegion(texture("assets/skin/circle.png"));
		this.etoile = texture("assets/skin/star.png");
		this.pentagramme = texture("assets/skin/Pentagramme.PNG");
		this.fond = texture("assets/menu_background.png");
	}

	private Texture texture(String chemin) {
		Texture texture = new Texture(Gdx.files.internal(chemin));
		chargees.add(texture);
		return texture;
	}

	/**
	 * Remplit une table d'une icône par point d'énergie : trois points de feu
	 * donnent trois flammes côte à côte.
	 */
	public void remplirEnergies(Table table, int[] quantites, int taille) {
		for (int energie = 0; energie < quantites.length; energie++) {
			for (int point = 0; point < quantites[energie]; point++) {
				table.add(new Image(energies[energie])).size(taille);
			}
		}
	}

	@Override
	public void dispose() {
		for (Texture texture : chargees) {
			texture.dispose();
		}
		chargees.clear();
	}
}
