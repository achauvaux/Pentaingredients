package com.nicolasgarland.pentaingredients.graphics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
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

	/**
	 * Le trait d'une ligne. Un seul masque blanc : c'est la teinte appliquée à
	 * l'exécution qui dit la synergie, plutôt que trois fichiers distincts.
	 */
	public final TextureRegion trait;

	public final Texture etoile;
	public final Texture pentagramme;

	/** Chargé pour le fond d'écran encore à faire ; l'écran ne le dessine pas. */
	public final Texture fond;

	/** Dégradé chaud et vignettage, étiré derrière toute la scène. */
	public final Texture ambiance;

	/** Lueur radiale, blanche donc teintable, posée derrière le pentagramme. */
	public final Texture lueur;

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
		this.trait = new TextureRegion(texture("assets/skin/line.png"));
		this.emplacement = new TextureRegion(texture("assets/skin/circle.png"));
		this.etoile = texture("assets/skin/star.png");
		this.pentagramme = texture("assets/skin/Pentagramme.PNG");
		this.fond = texture("assets/menu_background.png");
		this.ambiance = retenir(ambiance());
		this.lueur = retenir(lueur());
	}

	/**
	 * Fond de scène : un noir chaud qui s'éclaircit vers le centre, assombri sur
	 * les bords. Généré plutôt que dessiné — un dégradé lisse s'étire sans
	 * artefact, une image de 256 pixels suffit donc pour couvrir l'écran.
	 */
	private static Texture ambiance() {
		int taille = 256;
		Pixmap pixmap = new Pixmap(taille, taille, Pixmap.Format.RGBA8888);
		float centre = (taille - 1) / 2f;

		for (int y = 0; y < taille; y++) {
			for (int x = 0; x < taille; x++) {
				float dx = (x - centre) / centre;
				float dy = (y - centre) / centre;
				float distance = Math.min(1f, (float) Math.sqrt(dx * dx + dy * dy) / 1.41421f);
				float chute = (float) Math.pow(distance, 1.9);

				// du brun de chandelle au centre vers un noir presque pur aux bords
				float r = 0.105f + (0.014f - 0.105f) * chute;
				float v = 0.068f + (0.011f - 0.068f) * chute;
				float b = 0.042f + (0.009f - 0.042f) * chute;

				pixmap.setColor(r, v, b, 1f);
				pixmap.drawPixel(x, y);
			}
		}

		Texture texture = new Texture(pixmap);
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		pixmap.dispose();
		return texture;
	}

	/** Halo blanc à décroissance douce, à teinter au moment de l'utiliser. */
	private static Texture lueur() {
		int taille = 256;
		Pixmap pixmap = new Pixmap(taille, taille, Pixmap.Format.RGBA8888);
		float centre = (taille - 1) / 2f;

		for (int y = 0; y < taille; y++) {
			for (int x = 0; x < taille; x++) {
				float dx = (x - centre) / centre;
				float dy = (y - centre) / centre;
				float distance = (float) Math.sqrt(dx * dx + dy * dy);
				float intensite = distance >= 1f ? 0f : (float) Math.pow(1f - distance, 3);

				pixmap.setColor(1f, 1f, 1f, intensite);
				pixmap.drawPixel(x, y);
			}
		}

		Texture texture = new Texture(pixmap);
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		pixmap.dispose();
		return texture;
	}

	private Texture retenir(Texture texture) {
		chargees.add(texture);
		return texture;
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
