package com.nicolasgarland.pentaingredients.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nicolasgarland.pentaingredients.Main;
import com.nicolasgarland.pentaingredients.actors.InventorySlot;
import com.nicolasgarland.pentaingredients.graphics.Palette;
import com.nicolasgarland.pentaingredients.graphics.RessourcesJeu;
import com.nicolasgarland.pentaingredients.utils.Ingredient;
import com.nicolasgarland.pentaingredients.utils.Partie;
import com.nicolasgarland.pentaingredients.utils.Pentacle;
import com.nicolasgarland.pentaingredients.utils.Positions.Emplacement;

/**
 * L'écran de jeu. Il n'assemble plus lui-même le pentagramme et l'étagère :
 * {@link VuePentagramme} et {@link VueEtagere} s'en chargent, {@link Partie}
 * tient l'état et les règles.
 *
 * <p>Ce qui reste ici est ce que ni l'une ni l'autre des deux vues ne peut
 * décider seule : ce qu'un clic sur un emplacement déclenche, puisqu'il peut
 * permuter un ingrédient d'une moitié de l'écran à l'autre, et les dialogues.</p>
 */
public class GameScreen implements Screen, EcouteurDeSlot {

	private final Main game;
	private final Partie partie;

	private Stage metaStage;
	private Skin skin;
	private RessourcesJeu ressources;
	private VuePentagramme vuePentagramme;
	private VueEtagere vueEtagere;

	/** Premier emplacement cliqué, en attente du second avec qui permuter. */
	private InventorySlot slotSelectionne;

	public GameScreen(Main game, int levelNb) {
		this.game = game;
		this.partie = Partie.charger(levelNb);
		this.slotSelectionne = null;
	}

	@Override
	public void show() {
		ressources = new RessourcesJeu();
		metaStage = new Stage(new FitViewport(1920, 1080));
		skin = new Skin(Gdx.files.internal("assets/skin/uiskin.json"));

		vuePentagramme = new VuePentagramme(partie, skin, ressources, game.ingredientIcons, this,
				new Runnable() {
					@Override
					public void run() {
						partie.sauvegarder();
						// Le pentagramme s'embrase, puis le verdict tombe.
						vuePentagramme.flamboyer();
						metaStage.addAction(Actions.delay(0.45f, Actions.run(new Runnable() {
							@Override
							public void run() {
								afficherResultat();
							}
						})));
					}
				});

		vueEtagere = new VueEtagere(partie, skin, ressources, game.ingredientIcons, this,
				new Runnable() {
					@Override
					public void run() {
						partie.reinitialiser();
						game.changerEcran(new GameScreen(game, partie.numeroNiveau));
					}
				});

		// Posé avant tout le reste : il occupe le fond de la scène.
		Image ambiance = new Image(ressources.ambiance);
		ambiance.setSize(metaStage.getWidth(), metaStage.getHeight());
		metaStage.addActor(ambiance);

		Table metaTable = new Table();
		metaTable.setFillParent(true);
		metaTable.add(panneauGauche());
		metaTable.add(vuePentagramme.construire()).expand();
		metaTable.add(vueEtagere.construire()).expand();

		// Le plateau se révèle plutôt qu'il n'apparaît d'un coup.
		metaTable.getColor().a = 0f;
		metaTable.addAction(Actions.fadeIn(0.45f));

		metaStage.addActor(metaTable);
		Gdx.input.setInputProcessor(metaStage);
	}

	/** Colonne de gauche : accès aux règles en haut, retour en bas. */
	private Actor panneauGauche() {
		Table panneau = new Table();

		TextButton regles = new TextButton("Règles", skin, "default");
		regles.setColor(Palette.HABILLAGE);
		regles.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				afficherRegles();
			}
		});
		panneau.add(regles).width(160).height(60).pad(10).align(Align.top);
		panneau.row();

		// TODO : bouton d'options ?
		panneau.add().center().height(500);
		panneau.row();

		TextButton retour = new TextButton("Retour", skin);
		retour.setColor(Palette.HABILLAGE);
		retour.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				partie.sauvegarder();
				game.changerEcran(new LevelSelectScreen(game));
			}
		});
		panneau.add(retour).width(160).height(60).pad(10).align(Align.bottom);

		return panneau;
	}

	/**
	 * Un premier clic sélectionne un emplacement occupé, un second permute les
	 * deux contenus. Recliquer sur l'emplacement sélectionné annule.
	 */
	@Override
	public void slotClique(InventorySlot slot) {
		if (slot.isSelected()) {
			slot.setSelected(false);
			slotSelectionne = null;
		} else if (slotSelectionne == null) {
			if (slot.getItem() != null) {
				slot.setSelected(true);
				slotSelectionne = slot;
			}
		} else {
			permuter(slot);
		}
		vueEtagere.afficher(slotSelectionne != null ? slotSelectionne.getItem() : null);
	}

	private void permuter(InventorySlot slot) {
		Ingredient echange = slot.getItem();
		slot.setItem(slotSelectionne.getItem());
		slotSelectionne.setItem(echange);

		partie.deplacer(slot.getPosInt(), slot.getPosEmpl(),
				slotSelectionne.getPosInt(), slotSelectionne.getPosEmpl());

		slot.animerPose();
		slotSelectionne.animerPose();

		// Rien n'a changé sur le pentagramme si les deux cases sont sur l'étagère.
		if (surLePentagramme(slot) || surLePentagramme(slotSelectionne)) {
			vuePentagramme.rafraichir();
		}

		slot.setSelected(false);
		slotSelectionne.setSelected(false);
		slotSelectionne = null;
	}

	private boolean surLePentagramme(InventorySlot slot) {
		return slot.getPosEmpl() == Emplacement.PUISSANCE || slot.getPosEmpl() == Emplacement.CONTROLE;
	}

	/**
	 * Rédige le verdict destiné au joueur. Le moteur de règles ne rend que des
	 * valeurs : la formulation, elle, appartient à l'écran.
	 */
	private String messageDuResultat(Pentacle resultat) {
		String message = "";

		if (!resultat.sousControle) message += "Aïe ! Aïe ! Aïe ! Le sort n'est pas sous contrôle !\n";
		if (!resultat.assezPuissant) message += "Humpf ! Le sort n'est pas assez puissant !\n";

		if (resultat.estReussi()) {
			switch (resultat.etoiles) {
				case 3:  message += "Bravo !"; break;
				case 2:  message += "Excellent !"; break;
				case 1:  message += "Bien joué !"; break;
				default: message += "Peu mieux faire"; break;
			}
		}

		return message;
	}

	private void afficherResultat() {
		Dialog dialogue = new Dialog("Résultat de l'incantation", skin) {
			@Override
			protected void result(Object object) {
				if (object.equals("next")) {
					int suivant = partie.numeroNiveau + 1;
					if (Partie.niveauExiste(suivant)) {
						game.changerEcran(new GameScreen(game, suivant));
					} else {
						// plus de niveau : renvoyer vers la sélection
						game.changerEcran(new LevelSelectScreen(game));
					}
				}
			}
		};

		Pentacle resultat = partie.valider();
		if (resultat.estReussi()) {
			game.progression.enregistrer(partie.numeroNiveau, resultat.etoiles, resultat.cout);
		}

		Table contenu = new Table();
		contenu.add(new Label(" Puissance requise : ", skin, "default")).align(Align.left);
		contenu.add(tableEnergies(partie.niveau.puissance)).center();
		contenu.row();

		contenu.add(new Label(" Puissance du rituel : ", skin, "default")).align(Align.left);
		contenu.add(tableEnergies(resultat.puissance)).center();
		contenu.row();

		contenu.add(new Label(" Contrôle du rituel : ", skin, "default")).align(Align.left);
		contenu.add(tableEnergies(resultat.controle)).center();
		contenu.row();

		contenu.add(new Label(messageDuResultat(resultat), skin, "default")).colspan(2).center();

		dialogue.getContentTable().add(contenu).size(1000, 700);
		dialogue.button("Continuer d'essayer", "return").align(Align.left);
		dialogue.button("Niveau suivant", "next").align(Align.right);
		dialogue.show(metaStage);
	}

	private Table tableEnergies(int[] quantites) {
		Table table = new Table();
		ressources.remplirEnergies(table, quantites, 64);
		return table;
	}

	private void afficherRegles() {
		Dialog dialogue = new Dialog("Règles du Jeu", skin) {
			@Override
			protected void result(Object object) {
			}
		};
		dialogue.text(Gdx.files.internal("assets/rules.txt").readString());
		dialogue.button("Fermer");
		dialogue.show(metaStage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		metaStage.act(delta);
		metaStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		metaStage.getViewport().update(width, height, true);
		metaStage.getViewport().setScreenBounds(0, 0, width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		if (metaStage != null) metaStage.dispose();
		if (skin != null) skin.dispose();
		if (ressources != null) ressources.dispose();
	}
}
