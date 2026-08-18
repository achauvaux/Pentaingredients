package com.nicolasgarland.pentaingredients.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.nicolasgarland.pentaingredients.actors.InventorySlot;
import com.badlogic.gdx.graphics.Color;
import com.nicolasgarland.pentaingredients.graphics.IngredientIcons;
import com.nicolasgarland.pentaingredients.graphics.Palette;
import com.nicolasgarland.pentaingredients.graphics.RessourcesJeu;
import com.nicolasgarland.pentaingredients.utils.Arbitre;
import com.nicolasgarland.pentaingredients.utils.Partie;
import com.nicolasgarland.pentaingredients.utils.Positions.Emplacement;

/**
 * La moitié centrale de l'écran de jeu : ce que le rituel réclame, le
 * pentagramme et ses dix emplacements, le coût courant et le bouton
 * d'incantation.
 *
 * <p>Les coordonnées ci-dessous sont exprimées en fractions du côté du
 * pentagramme et ont été réglées à l'œil sur l'image de fond. Elles n'ont pas
 * d'autre justification que le rendu obtenu, et ne se recalculent pas.</p>
 */
public class VuePentagramme {

	private static final int COTE = 650;

	/** Inclinaison de chaque trait, en degrés. */
	private static final int[] ANGLE_DES_TRAITS = {0, 37, 72, 108, 144};

	private static final float[][] CENTRE_DES_TRAITS = {
			{-0.01f, 0.29f},
			{0.47f, -0.77f},
			{0.535f, -0.66f},
			{1.68f, -0.545f},
			{1.60f, -0.43f},
	};

	private static final int[] ANGLE_DES_ENERGIES = {0, 37, 72, -72, -36};

	/** Par ligne : où poser la puissance, puis où poser le contrôle. */
	private static final float[][][] CENTRE_DES_ENERGIES = {
			{{0f, 0.34f},     {0f, 0.24f}},
			{{0.2f, -0.3f},   {0.14f, -0.20f}},
			{{-0.35f, 0.09f}, {-0.23f, 0.08f}},
			{{0.34f, 0.09f},  {0.22f, 0.08f}},
			{{-0.2f, -0.3f},  {-0.14f, -0.2f}}
	};

	private static final float[][] POSITION_DES_POINTES = {
			{-0.915f, 0.29f},
			{0.915f, 0.29f},
			{-0.57f, -0.80f},
			{0f, 0.97f},
			{0.57f, -0.80f}
	};

	private static final float[][] POSITION_DES_MILIEUX = {
			{-0.215f, 0.29f},
			{0.215f, 0.29f},
			{0.35f, -0.125f},
			{0f, -0.382f},
			{-0.35f, -0.125f}
	};

	private final Partie partie;
	private final Skin skin;
	private final RessourcesJeu ressources;
	private final IngredientIcons icones;
	private final EcouteurDeSlot ecouteur;
	private final Runnable surIncantation;

	private final Image[] traits = new Image[Arbitre.NB_LIGNES];
	private final Table[][] energiesParLigne = new Table[Arbitre.NB_LIGNES][2];
	private Label coutTotal;

	public VuePentagramme(Partie partie, Skin skin, RessourcesJeu ressources,
			IngredientIcons icones, EcouteurDeSlot ecouteur, Runnable surIncantation) {
		this.partie = partie;
		this.skin = skin;
		this.ressources = ressources;
		this.icones = icones;
		this.ecouteur = ecouteur;
		this.surIncantation = surIncantation;

		for (int ligne = 0; ligne < Arbitre.NB_LIGNES; ligne++) {
			traits[ligne] = new Image(ressources.trait);
			energiesParLigne[ligne][0] = new Table();
			energiesParLigne[ligne][1] = new Table();
		}
	}

	public Actor construire() {
		Table principale = new Table();

		principale.add(commandeDuRituel()).colspan(2).center();
		principale.row();
		// TODO : mettre dans une window + superposer étoile et valeur

		principale.add(pentagramme()).pad(50).colspan(2).size(COTE, COTE);
		principale.row();

		coutTotal = new Label("Coût total : " + partie.coutTotal(), skin, "default");
		coutTotal.setColor(Palette.PARCHEMIN);
		principale.add(coutTotal).align(Align.left);

		TextButton incantation = new TextButton("Lancer l'incantation", skin);
		incantation.setColor(Palette.HABILLAGE);
		incantation.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				surIncantation.run();
			}
		});
		principale.add(incantation).align(Align.right);

		return principale;
	}

	/** Ce que le niveau réclame : son nom, ses énergies, ses trois seuils de coût. */
	private Table commandeDuRituel() {
		Table commande = new Table();

		commande.add(new Label(partie.niveau.name, Palette.titre(skin))).colspan(3).center();
		commande.row();

		Table requises = new Table();
		ressources.remplirEnergies(requises, partie.niveau.puissance, 64);
		commande.add(requises).colspan(3).center();
		commande.row();

		for (int seuil = 0; seuil < 3; seuil++) {
			// Éteintes : ce sont des seuils à atteindre, pas des récompenses acquises.
			Image etoile = new Image(ressources.etoile);
			etoile.setColor(Palette.ETOILE_ETEINTE);
			commande.add(etoile).center();
		}
		commande.row();

		for (int seuil = 0; seuil < 3; seuil++) {
			Label seuilLabel = new Label("" + partie.niveau.objectifs[seuil], skin, "default");
			seuilLabel.setColor(Palette.PARCHEMIN);
			commande.add(seuilLabel).center();
		}
		commande.row();

		return commande;
	}

	private Group pentagramme() {
		Group groupe = new Group();

		// La lueur d'abord : tout le reste se pose dessus.
		Image lueur = new Image(ressources.lueur);
		lueur.setColor(Palette.BRAISE.r, Palette.BRAISE.g, Palette.BRAISE.b, 0.22f);
		lueur.setSize(COTE * 1.7f, COTE * 1.7f);
		lueur.setPosition((COTE - lueur.getWidth()) / 2f, (COTE - lueur.getHeight()) / 2f);
		groupe.addActor(lueur);

		Image trace = new Image(ressources.pentagramme);
		trace.setColor(Palette.OR);
		trace.setWidth(COTE);
		trace.setHeight(COTE);
		groupe.addActor(trace);

		for (int ligne = 0; ligne < Arbitre.NB_LIGNES; ligne++) {
			Image trait = traits[ligne];
			trait.setColor(couleurSelonSynergie(partie.synergie(ligne + 1)));
			trait.setWidth(COTE - 50);
			trait.setHeight(128);
			trait.setRotation(ANGLE_DES_TRAITS[ligne]);
			trait.setPosition(place(CENTRE_DES_TRAITS[ligne][0], trait.getWidth()),
					place(CENTRE_DES_TRAITS[ligne][1], trait.getHeight()));
			groupe.addActor(trait);
		}

		for (int ligne = 0; ligne < Arbitre.NB_LIGNES; ligne++) {
			int[][] energies = partie.energiesDeLaLigne(ligne + 1);
			for (int role = 0; role < 2; role++) {
				Table table = energiesParLigne[ligne][role];
				// Le remplissage précède le placement : la position est calculée à
				// partir de la largeur de la table, donc de son contenu.
				table.clear();
				ressources.remplirEnergies(table, energies[role], 18);
				table.setTransform(true);
				table.setRotation(ANGLE_DES_ENERGIES[ligne]);
				table.setPosition(place(CENTRE_DES_ENERGIES[ligne][role][0], table.getWidth()),
						place(CENTRE_DES_ENERGIES[ligne][role][1], table.getHeight()));
				groupe.addActor(table);
			}
		}

		poserEmplacements(groupe, Emplacement.PUISSANCE, POSITION_DES_POINTES, Palette.BRAISE);
		poserEmplacements(groupe, Emplacement.CONTROLE, POSITION_DES_MILIEUX, Palette.GIVRE);

		return groupe;
	}

	private void poserEmplacements(Group groupe, Emplacement role, float[][] positions, Color couleur) {
		for (int numero = 0; numero < positions.length; numero++) {
			final InventorySlot slot = new InventorySlot(ressources.emplacement, role, numero, icones);
			slot.setColor(couleur);
			slot.setItem(partie.ingredient(partie.idSur(numero, role)));
			slot.setPosition(place(positions[numero][0], slot.getWidth()),
					place(positions[numero][1], slot.getHeight()));
			slot.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					ecouteur.slotClique(slot);
				}
			});
			groupe.addActor(slot);
		}
	}

	/** Convertit une fraction du côté en coordonnée, centrée sur l'acteur. */
	private float place(float fraction, float tailleActeur) {
		return COTE / 2 * (1 + fraction) - tailleActeur / 2;
	}

	/**
	 * Recalcule ce que le pentagramme montre après un déplacement : le coût, la
	 * couleur de chaque trait selon sa synergie, et les énergies posées de part
	 * et d'autre. Les positions, elles, ne bougent plus.
	 */
	public void rafraichir() {
		coutTotal.setText("Coût total : " + partie.coutTotal());

		for (int ligne = 0; ligne < Arbitre.NB_LIGNES; ligne++) {
			traits[ligne].setColor(couleurSelonSynergie(partie.synergie(ligne + 1)));

			int[][] energies = partie.energiesDeLaLigne(ligne + 1);
			for (int role = 0; role < 2; role++) {
				energiesParLigne[ligne][role].clear();
				ressources.remplirEnergies(energiesParLigne[ligne][role], energies[role], 18);
			}
		}
	}

	/**
	 * La couleur du trait dit ce que vaut la ligne : cendre quand elle est
	 * éteinte, braise quand elle compte double, parchemin le reste du temps.
	 */
	private Color couleurSelonSynergie(int synergie) {
		switch (synergie) {
			case Arbitre.SYNERGIE_NULLE:
				return Palette.CENDRE;
			case Arbitre.SYNERGIE_DOUBLE:
				return Palette.BRAISE;
			default:
				return Palette.PARCHEMIN;
		}
	}
}
