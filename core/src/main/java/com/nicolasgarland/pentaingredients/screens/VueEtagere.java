package com.nicolasgarland.pentaingredients.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.nicolasgarland.pentaingredients.actors.InventorySlot;
import com.nicolasgarland.pentaingredients.graphics.IngredientIcons;
import com.nicolasgarland.pentaingredients.graphics.RessourcesJeu;
import com.nicolasgarland.pentaingredients.utils.Ingredient;
import com.nicolasgarland.pentaingredients.utils.Partie;
import com.nicolasgarland.pentaingredients.utils.Positions.Emplacement;

/**
 * La moitié droite de l'écran de jeu : les rangées d'ingrédients en réserve, la
 * fiche de l'ingrédient sélectionné, et le bouton de remise à zéro.
 */
public class VueEtagere {

	private static final int RANGEES = 10;
	private static final int CASES_PAR_RANGEE = 10;

	private final Partie partie;
	private final Skin skin;
	private final RessourcesJeu ressources;
	private final IngredientIcons icones;
	private final EcouteurDeSlot ecouteur;
	private final Runnable surReinitialisation;

	private Image ficheIcone;
	private Label ficheNom;
	private Label ficheFamille;
	private Label ficheCout;
	private Table ficheEnergies;

	public VueEtagere(Partie partie, Skin skin, RessourcesJeu ressources,
			IngredientIcons icones, EcouteurDeSlot ecouteur, Runnable surReinitialisation) {
		this.partie = partie;
		this.skin = skin;
		this.ressources = ressources;
		this.icones = icones;
		this.ecouteur = ecouteur;
		this.surReinitialisation = surReinitialisation;
	}

	public Actor construire() {
		Table principale = new Table();

		principale.add(new Label("Etagères", skin, "title")).center();
		principale.row();

		principale.add(rangees()).center();
		principale.row();

		principale.add(fiche()).center();
		principale.row();

		TextButton reinitialisation = new TextButton("Réinitialisation", skin);
		reinitialisation.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				surReinitialisation.run();
			}
		});
		principale.add(reinitialisation).width(240).height(60).pad(10).align(Align.right);

		return principale;
	}

	private Table rangees() {
		Table grille = new Table();

		for (int rangee = 0; rangee < RANGEES; rangee++) {
			for (int colonne = 0; colonne < CASES_PAR_RANGEE; colonne++) {
				int position = rangee * CASES_PAR_RANGEE + colonne;
				final InventorySlot slot =
						new InventorySlot(ressources.caseVide, Emplacement.ETAGERE, position, icones);
				slot.setItem(partie.ingredient(partie.idSur(position, Emplacement.ETAGERE)));
				slot.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						ecouteur.slotClique(slot);
					}
				});
				grille.add(slot).size(InventorySlot.SLOT_SIZE);
			}
			grille.row();
		}

		return grille;
	}

	private Window fiche() {
		Window fenetre = new Window("Ingrédient sélectionné :", skin);
		Table contenu = new Table();

		ficheIcone = new Image(ressources.caseVide);
		contenu.add(ficheIcone).size(64).align(Align.left);
		ficheNom = new Label("Aucun ingrédient sélectionné", skin, "default");
		contenu.add(ficheNom).align(Align.left);
		contenu.row();

		ficheEnergies = new Table();
		ficheEnergies.add(new Image()).size(64).center();
		contenu.add(ficheEnergies).colspan(2).center();
		contenu.row();

		ficheCout = new Label("", skin, "default");
		contenu.add(ficheCout).align(Align.left);
		ficheFamille = new Label("", skin, "default");
		contenu.add(ficheFamille).align(Align.right);

		fenetre.add(contenu).size(640, 250);
		return fenetre;
	}

	/** Affiche la fiche d'un ingrédient, ou la vide si l'argument est {@code null}. */
	public void afficher(Ingredient ingredient) {
		if (ingredient != null) {
			Texture icone = icones.get(ingredient.id);
			if (icone != null) ficheIcone.setDrawable(new TextureRegionDrawable(icone));
			ficheNom.setText(ingredient.name);
			ficheFamille.setText(ingredient.famille.toString());
			ficheCout.setText("Coût : " + ingredient.cout);
			ficheEnergies.clear();
			ressources.remplirEnergies(ficheEnergies, ingredient.energies, 64);
		} else {
			ficheIcone.setDrawable(new TextureRegionDrawable(ressources.caseVide));
			ficheNom.setText("Aucun ingrédient sélectionné");
			ficheFamille.setText("");
			ficheCout.setText("");
			ficheEnergies.clear();
			ficheEnergies.add(new Image()).size(64).center();
		}
	}
}
