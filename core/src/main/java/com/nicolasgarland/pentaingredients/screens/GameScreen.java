package com.nicolasgarland.pentaingredients.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nicolasgarland.pentaingredients.Main;
import com.nicolasgarland.pentaingredients.actors.InventorySlot;
import com.nicolasgarland.pentaingredients.utils.Ingredient;
import com.nicolasgarland.pentaingredients.utils.Partie;
import com.nicolasgarland.pentaingredients.utils.Pentacle;
import com.nicolasgarland.pentaingredients.utils.Positions.Emplacement;

public class GameScreen implements Screen {
	private final int PENTAILLE = 650;
	private final Main game;
	
	private Stage metaStage;
    private Skin skin;
    private Texture background;
    
    private final Partie partie;
    private InventorySlot selectedSlot;
    private TextureRegion[] elements;
	private Image infoIcon;
	private Label infoName;
	private Label infoFamily;
	private Label infoCout;
	private Table elemTable;
	private TextureRegion emptySlot;
	private Label coutTotalLabel;
	private int[] synergies;
	private int[][][] puisCtrlLignes;
	private Table[][] puisCtrlTables;
	private Image[] imgLignes;
	private TextureRegion zeroLine;
	private TextureRegion okLine;
	private TextureRegion doubleLine;

	/** Toutes les textures chargées par cet écran, pour pouvoir les libérer. */
	private final List<Texture> texturesChargees = new ArrayList<Texture>();


	public GameScreen(Main game, int levelNb) {
        this.game = game;
        this.partie = Partie.charger(levelNb);
        this.selectedSlot = null;
        this.elements = new TextureRegion[]{
        		new TextureRegion(texture("assets/skin/fire.png")),
        		new TextureRegion(texture("assets/skin/earth.png")),
        		new TextureRegion(texture("assets/skin/lightning.png")),
        		new TextureRegion(texture("assets/skin/water.png")),
        		new TextureRegion(texture("assets/skin/wind.png")),
        		new TextureRegion(texture("assets/skin/spirit.png"))
        };
        this.emptySlot = new TextureRegion(texture("assets/skin/slot.png"));
        this.zeroLine = new TextureRegion(texture("assets/skin/line-noir.png"));
        this.okLine = new TextureRegion(texture("assets/skin/line.png"));
        this.doubleLine = new TextureRegion(texture("assets/skin/line-rouge.png"));
        this.imgLignes = new Image[] {new Image(okLine), new Image(okLine), new Image(okLine), new Image(okLine), new Image(okLine)};
        this.synergies = new int[] {1,1,1,1,1};
        this.puisCtrlLignes = new int[][][] { // [ligne][2][énergies]
        	{
        		{0,0,0,0,0,0},
        		{0,0,0,0,0,0}
        	},
        	{
        		{0,0,0,0,0,0},
        		{0,0,0,0,0,0}
        	},
        	{
        		{0,0,0,0,0,0},
        		{0,0,0,0,0,0}
        	},
        	{
        		{0,0,0,0,0,0},
        		{0,0,0,0,0,0}
        	},
        	{
        		{0,0,0,0,0,0},
        		{0,0,0,0,0,0}
        	}
        };
        this.puisCtrlTables = new Table[][] {
        	{new Table(), new Table()},
        	{new Table(), new Table()},
        	{new Table(), new Table()},
        	{new Table(), new Table()},
        	{new Table(), new Table()}
        	};
	}

	/**
	 * Charge une texture et la retient pour {@link #dispose()}. Toute texture de
	 * cet écran doit passer par ici : sinon elle fuit à chaque entrée dans un
	 * niveau, et l'écran est reconstruit à chaque niveau, chaque réinitialisation
	 * et chaque retour depuis la sélection.
	 */
	private Texture texture(String chemin) {
		Texture texture = new Texture(Gdx.files.internal(chemin));
		texturesChargees.add(texture);
		return texture;
	}

	@Override
	public void show() {
	    // Charger le fond d'écran
        background = texture("assets/menu_background.png");

        // Créer Viewport
        FitViewport viewport = new FitViewport(1920, 1080);

        // Créer les Stage
        metaStage = new Stage(viewport);

        // charger la skin
        skin = new Skin(Gdx.files.internal("assets/skin/uiskin.json"));

        // Ajouter des acteurs (boutons, labels, etc.)
        Table metaTable = new Table();
        metaTable.setFillParent(true);

        metaTable.add(addActorsToLeftStage());
        metaTable.add(addActorsToPentagrStage()).expand();
        metaTable.add(addActorsToEtagereStage()).expand();
        
        metaStage.addActor(metaTable);
//        metaTable.setDebug(true);

        // Définir les InputProcessor
        Gdx.input.setInputProcessor(metaStage);
	}

	private Actor addActorsToLeftStage() {
		Table mainTable = new Table();

		// bouton de règles
		TextButton rulesButton = new TextButton("Règles", skin, "default");
		rulesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showRulesDialog();
            }
        });
		mainTable.add(rulesButton).width(160).height(60).pad(10).align(Align.top);
		mainTable.row();
		
		// TODO : bouton d'options ?
		mainTable.add().center().height(500);
		mainTable.row();
		
		// bouton retour
        TextButton backButton = new TextButton("Retour", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	partie.sauvegarder();
                game.changerEcran(new LevelSelectScreen(game));
            }
        });
        mainTable.add(backButton).width(160).height(60).pad(10).align(Align.bottom);
		
		return mainTable;
	}

	private Table addActorsToEtagereStage() {
		Table mainTable = new Table();

		// titre du niveau
		Label titleLabel = new Label("Etagères", skin, "title");
		mainTable.add(titleLabel).center();
	    mainTable.row();
		
	    // table des ingrédients sur les étagères
//		InventorySlot[][] slots = new InventorySlot[10][10];
        Table inventoryTable = new Table();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
            	final InventorySlot slot = new InventorySlot(emptySlot, Emplacement.ETAGERE, row*10+col, game.ingredientIcons);
                
            	slot.setItem(partie.ingredient(partie.idSur(row*10+col, Emplacement.ETAGERE)));
                // Ajouter un écouteur pour les clics
            	slot.addListener(new ClickListener() {
            		@Override
            		public void clicked(InputEvent event, float x, float y) {
            			newlyClicked(slot);
            		}
            	});
                inventoryTable.add(slot).size(InventorySlot.SLOT_SIZE);
            }
            inventoryTable.row();  // Nouvelle ligne après chaque rangée
        }
        mainTable.add(inventoryTable).center();
        mainTable.row();
        
        // description de l'ingrédient sélectionné
        Window infoWindow = new Window("Ingrédient sélectionné :", skin);
        Table ingrSelectedTable = new Table();

       	infoIcon = new Image(emptySlot);
        ingrSelectedTable.add(infoIcon).size(64).align(Align.left);
        infoName = new Label("Aucun ingrédient sélectionné", skin, "default");
        ingrSelectedTable.add(infoName).align(Align.left);
        ingrSelectedTable.row();
        elemTable = new Table();
        elemTable.add(new Image()).size(64).center();
        ingrSelectedTable.add(elemTable).colspan(2).center();
        ingrSelectedTable.row();
        infoCout = new Label("", skin, "default");
        ingrSelectedTable.add(infoCout).align(Align.left);
        infoFamily = new Label("", skin, "default");
        ingrSelectedTable.add(infoFamily).align(Align.right);
        infoWindow.add(ingrSelectedTable).size(640, 250);
		
		mainTable.add(infoWindow).center();
        mainTable.row();

        // bouton réinitialiser position
        TextButton resetButton = new TextButton("Réinitialisation", skin);
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	partie.reinitialiser();
            	game.changerEcran(new GameScreen(game, partie.numeroNiveau));
            }
        });
        mainTable.add(resetButton).width(240).height(60).pad(10).align(Align.right);
        
//        mainTable.setDebug(true);
        return mainTable;
	}

	protected void newlyClicked(InventorySlot slot) {
		if(slot.isSelected()) {
			slot.setSelected(false);
			selectedSlot = null;
		} else {
			if(selectedSlot == null) {
				if(slot.getItem() != null) {
					slot.setSelected(true);
					selectedSlot = slot;					
				}
			} else {
				// permute puis déselectionne tout
				Ingredient ingr = slot.getItem();
				slot.setItem(selectedSlot.getItem());
				selectedSlot.setItem(ingr);
				partie.deplacer(slot.getPosInt(), slot.getPosEmpl(), selectedSlot.getPosInt(), selectedSlot.getPosEmpl());
				
				if(slot.getPosEmpl()==Emplacement.CONTROLE || slot.getPosEmpl()==Emplacement.PUISSANCE || 
				   selectedSlot.getPosEmpl()==Emplacement.CONTROLE || selectedSlot.getPosEmpl()==Emplacement.PUISSANCE) {
					// recalcule le coutTotal
					coutTotalLabel.setText("Coût total : " + partie.coutTotal()) ;
					
					//recalcule les lignes et les énergies dessus/dessous
					for(int i=0; i<5; i++) {
						synergies[i] = partie.synergie(i+1);
						switch(synergies[i]) {
							case 0:
								imgLignes[i].setDrawable(new TextureRegionDrawable(zeroLine));
								break;
							case 1:
								imgLignes[i].setDrawable(new TextureRegionDrawable(okLine));
								break;
							case 2:
								imgLignes[i].setDrawable(new TextureRegionDrawable(doubleLine));
								break;
						}
						puisCtrlLignes[i] = partie.energiesDeLaLigne(i+1);
						puisCtrlTables[i][0].clear();
						fillElemTable(puisCtrlTables[i][0], puisCtrlLignes[i][0], 18);
						puisCtrlTables[i][1].clear();
						fillElemTable(puisCtrlTables[i][1], puisCtrlLignes[i][1], 18);
					}
				}
				
				slot.setSelected(false);
				selectedSlot.setSelected(false);
				selectedSlot = null;
			}
		}
		updateInfoPanel();
	}

	private void updateInfoPanel() {
	    if (selectedSlot != null && selectedSlot.getItem() != null) {
	        Ingredient item = selectedSlot.getItem();
	        Texture icone = game.ingredientIcons.get(item.id);
	        if (icone != null) infoIcon.setDrawable(new TextureRegionDrawable(icone));
	    	infoName.setText(item.name);
	    	infoFamily.setText(item.famille.toString());
	    	infoCout.setText("Coût : "+item.cout);
	    	elemTable.clear();
	    	fillElemTable(elemTable, item.energies, 64);
	    } else {
	    	infoIcon.setDrawable(new TextureRegionDrawable(emptySlot));
	    	infoName.setText("Aucun ingrédient sélectionné");
	    	infoFamily.setText("");
	    	infoCout.setText("");
	    	elemTable.clear();
	    	elemTable.add(new Image()).size(64).center();
	    }
	}

	private void fillElemTable(Table elemTable, int[] energies, int taille) {
        for(int i=0 ; i<energies.length ; i++) {
        	for(int j=0 ; j < energies[i] ; j++) {
        		elemTable.add(new Image(elements[i])).size(taille);
        	}
        }
	}

	private Table addActorsToPentagrStage() {
		Table mainTable = new Table();
		
		// description du niveau
		Table levelTable = new Table();
		
		levelTable.add(new Label(partie.niveau.name, skin, "title")).colspan(3).center();
		levelTable.row();
        Table elemTable = new Table();
        fillElemTable(elemTable, partie.niveau.puissance, 64);
        levelTable.add(elemTable).colspan(3).center();
		levelTable.row();
		Texture etoile = texture("assets/skin/star.png");
		levelTable.add(new Image(etoile)).center();
		levelTable.add(new Image(etoile)).center();
		levelTable.add(new Image(etoile)).center();
		levelTable.row();
		levelTable.add(new Label(""+partie.niveau.objectifs[0], skin, "default")).center();
		levelTable.add(new Label(""+partie.niveau.objectifs[1], skin, "default")).center();
		levelTable.add(new Label(""+partie.niveau.objectifs[2], skin, "default")).center();
		levelTable.row();
	    
		mainTable.add(levelTable).colspan(2).center();
	    mainTable.row();
	    // TODO : mettre dans une window + supperposer étoile et valeur
		
	    // Créer un groupe pour les acteurs du pentagramme
	    Group pentagramGroup = new Group();
	    // image pentagramme
	    Image img = new Image(texture("assets/skin/Pentagramme.PNG"));
//	    Gdx.app.log("DEBUG", "pentagramme size : " + img.getWidth()+" x "+img.getHeight());
//	    img.setSize(909, 908);
	    img.setWidth(PENTAILLE);
	    img.setHeight(PENTAILLE);
	    pentagramGroup.addActor(img);
	    
	    // dessiner les lignes avec synergies
	    int[] angleLignes = {0,37,72,108,144};
	    float[][] centerLignes = {
	    		{-0.01f, 0.29f},
	    		{0.47f, -0.77f},
	    		{0.535f, -0.66f},
	    		{1.68f, -0.545f},
	    		{1.60f, -0.43f},
	    };
	    for(int i=0; i<5; i++) {
			synergies[i] = partie.synergie(i+1);
			switch(synergies[i]) {
				case 0:
					imgLignes[i].setDrawable(new TextureRegionDrawable(zeroLine));
					break;
				case 1:
					imgLignes[i].setDrawable(new TextureRegionDrawable(okLine));
					break;
				case 2:
					imgLignes[i].setDrawable(new TextureRegionDrawable(doubleLine));
					break;
			}
	    	imgLignes[i].setWidth(PENTAILLE-50);
	    	imgLignes[i].setHeight(128);
	    	imgLignes[i].setRotation(angleLignes[i]);
	    	imgLignes[i].setPosition(PENTAILLE/2*(1+centerLignes[i][0])-imgLignes[i].getWidth()/2, 
									 PENTAILLE/2*(1+centerLignes[i][1])-imgLignes[i].getHeight()/2);
	    	
	    	pentagramGroup.addActor(imgLignes[i]);
	    }
	    
	    // dessiner la puissance et le controle
	    int[] angleTables = {0,37,72,-72,-36};
	    float[][][] centerTables = {
	    		{{0f, 0.34f},     {0f, 0.24f}},
	    		{{0.2f, -0.3f},   {0.14f, -0.20f}},
	    		{{-0.35f, 0.09f}, {-0.23f, 0.08f}},
	    		{{0.34f, 0.09f},  {0.22f, 0.08f}},
	    		{{-0.2f, -0.3f},  {-0.14f, -0.2f}}
	    };
	    for(int i=0; i<5; i++) {
			puisCtrlLignes[i] = partie.energiesDeLaLigne(i+1);
	    	for(int pc=0; pc<2; pc++) {
				puisCtrlTables[i][pc].clear();
				fillElemTable(puisCtrlTables[i][pc], puisCtrlLignes[i][pc], 18);
				puisCtrlTables[i][pc].setTransform(true);
				puisCtrlTables[i][pc].setRotation(angleTables[i]);
				puisCtrlTables[i][pc].setPosition(PENTAILLE/2*(1+centerTables[i][pc][0])-puisCtrlTables[i][pc].getWidth()/2, 
						 			  			  PENTAILLE/2*(1+centerTables[i][pc][1])-puisCtrlTables[i][pc].getHeight()/2);
//				puisCtrlTables[i][pc].debug();
	    		pentagramGroup.addActor(puisCtrlTables[i][pc]);
	    	}
	    }
	    
	    // 10 slots
	    TextureRegion slotTexture = new TextureRegion(texture("assets/skin/circle.png"));
	    float[][] slotPositionsPuissance = {
	            {-0.915f,  0.29f},  // Position du slot 1 (x, y)
	            { 0.915f,  0.29f},  // Position du slot 2
	            {-0.57f, -0.80f},  // Position du slot 3
	            { 0f, 0.97f},             // Position du slot 4
	            { 0.57f, -0.80f}   // Position du slot 5
	        };
	    for(int i=0; i<5; i++) {
	    	final InventorySlot slotP = new InventorySlot(slotTexture, Emplacement.PUISSANCE, i, game.ingredientIcons);
	    	slotP.setItem(partie.ingredient(partie.idSur(i, Emplacement.PUISSANCE)));
	    	slotP.setPosition(	PENTAILLE/2*(1+slotPositionsPuissance[i][0])-slotP.getWidth()/2, 
	    						PENTAILLE/2*(1+slotPositionsPuissance[i][1])-slotP.getHeight()/2);
            // Ajouter un écouteur pour les clics
        	slotP.addListener(new ClickListener() {
        		@Override
        		public void clicked(InputEvent event, float x, float y) {
        			newlyClicked(slotP);
        		}
        	});
	    	pentagramGroup.addActor(slotP);
	    }
	    
	    float[][] slotPositionsControle = {
	            {-0.215f,  0.29f},  // Position du slot 1 (x, y)
	            { 0.215f,  0.29f},  // Position du slot 2
	            { 0.35f, -0.125f},  // Position du slot 3
	            { 0f, -0.382f},  	   // Position du slot 4
	            {-0.35f, -0.125f}   // Position du slot 5
	        };
	    for(int i=0; i<5; i++) {
	    	InventorySlot slotC = new InventorySlot(slotTexture, Emplacement.CONTROLE, i, game.ingredientIcons);
	    	slotC.setItem(partie.ingredient(partie.idSur(i, Emplacement.CONTROLE)));
	    	slotC.setPosition(	PENTAILLE/2*(1+slotPositionsControle[i][0])-slotC.getWidth()/2, 
	    						PENTAILLE/2*(1+slotPositionsControle[i][1])-slotC.getHeight()/2);
            // Ajouter un écouteur pour les clics
        	slotC.addListener(new ClickListener() {
        		@Override
        		public void clicked(InputEvent event, float x, float y) {
        			newlyClicked(slotC);
        		}
        	});
	    	pentagramGroup.addActor(slotC);
	    }
    	
	    mainTable.add(pentagramGroup).pad(50).colspan(2).size(PENTAILLE, PENTAILLE);
	    
	    mainTable.row();
	    coutTotalLabel = new Label("Coût total : " + partie.coutTotal(), skin, "default");
	    mainTable.add(coutTotalLabel).align(Align.left);
	    
	    TextButton validButton = new TextButton("Lancer l'incantation", skin);
        validButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	partie.sauvegarder();
            	showResultatDialog();
            }

        });
	    mainTable.add(validButton).align(Align.right);
        
//        mainTable.setDebug(true);
        return mainTable;
	}
	
	/**
	 * Rédige le verdict destiné au joueur. Le moteur de règles ne rend que des
	 * valeurs : la formulation, elle, appartient à l'écran.
	 */
	private String messageDuResultat(Pentacle resultat) {
		String message = "";

		if(!resultat.sousControle) message += "Aïe ! Aïe ! Aïe ! Le sort n'est pas sous contrôle !\n";
		if(!resultat.assezPuissant) message += "Humpf ! Le sort n'est pas assez puissant !\n";

		if(resultat.estReussi()) {
			switch(resultat.etoiles) {
				case 3:  message += "Bravo !"; break;
				case 2:  message += "Excellent !"; break;
				case 1:  message += "Bien joué !"; break;
				default: message += "Peu mieux faire"; break;
			}
		}

		return message;
	}

	private void showResultatDialog() {
        Dialog resultDialog = new Dialog("Résultat de l'incantation", skin) {
            @Override
            protected void result(Object object) {
                if (object.equals("return")) {
                	
                } else if (object.equals("next")) {
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
        
        Table mainTable = new Table();
//        mainTable.setFillParent(true);
        
        // Ajouter du texte
        mainTable.add(new Label(" Puissance requise : ", skin, "default")).align(Align.left);

        Table objTable = new Table();
        fillElemTable(objTable, partie.niveau.puissance, 64);
        mainTable.add(objTable).center();
        mainTable.row();
        
        Pentacle valid = partie.valider();
        
        mainTable.add(new Label(" Puissance du rituel : ", skin, "default")).align(Align.left);
//        rulesDialog.text("\n Puissance du rituel : ");
        Table puissTable = new Table();
        fillElemTable(puissTable, valid.puissance, 64);
        mainTable.add(puissTable).center();
        mainTable.row();
        
        mainTable.add(new Label(" Contrôle du rituel : ", skin, "default")).align(Align.left);
//        rulesDialog.text("\n Contrôle du rituel : ");
        Table ctrlTable = new Table();
        fillElemTable(ctrlTable, valid.controle, 64);
        mainTable.add(ctrlTable).center();
        mainTable.row();
        
        mainTable.add(new Label(messageDuResultat(valid), skin, "default")).colspan(2).center();
//        resultDialog.text(valid.description);
        
        resultDialog.getContentTable().add(mainTable).size(1000, 700);

        // Ajouter des bouton 
        resultDialog.button("Continuer d'essayer", "return").align(Align.left);
        resultDialog.button("Niveau suivant", "next").align(Align.right);

//        resultDialog.setSize(1000, 1000);
        // Afficher le dialog
        resultDialog.show(metaStage);
	}

	private void showRulesDialog() {
        Dialog rulesDialog = new Dialog("Règles du Jeu", skin) {
            @Override
            protected void result(Object object) {
                // Called when a button is clicked
            }
        };

        // Ajouter du texte
        rulesDialog.text(Gdx.files.internal("assets/rules.txt").readString());

        // Ajouter un bouton "Fermer"
        rulesDialog.button("Fermer");

        // Afficher le dialog
        rulesDialog.show(metaStage);
    }

	@Override
	public void render(float delta) {
        // Effacer l'écran
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Mettre à jour et dessiner chaque Stage
        metaStage.act(delta);
        metaStage.draw();
	}

	@Override
	public void resize(int width, int height) {
	    // Mettre à jour les Viewports
		metaStage.getViewport().update(width, height, true);

	    // Redéfinir les ScreenBounds
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
        metaStage.dispose();
        skin.dispose();
        for (Texture texture : texturesChargees) {
            texture.dispose();
        }
        texturesChargees.clear();
	}

}
