package com.nicolasgarland.pentaingredients.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nicolasgarland.pentaingredients.Main;

public class LevelSelectScreen implements Screen {
	private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private List<Integer> levelsNb;
    private Table levelsTable;

    public LevelSelectScreen(Main game) {
        this.game = game;
    }

	@Override
	public void show() {
	    // TODO Charger le fond d'écran
        background = new Texture(Gdx.files.internal("assets/menu_background.png"));

        // Créer le Stage et le Viewport
        stage = new Stage();

        // Charger la Skin
        skin = new Skin(Gdx.files.internal("assets/skin/uiskin.json"));
//        BitmapFont font = new BitmapFont(); // Police par défaut
//        Label.LabelStyle labelStyleTitle = new Label.LabelStyle(font, Color.GOLD);
//        skin.add("title", labelStyleTitle);

        // TODO : ajouter niveau tutoriel
        
        // Charger les niveaux
        loadLevels();

        // Créer la table des niveaux
        createLevelsTable();

        // Ajouter un bouton "Retour"
        addBackButton();

        // Définir l'InputProcessor
        Gdx.input.setInputProcessor(stage);
	}

	private void addBackButton() {
        TextButton backButton = new TextButton("Retour", skin); // TODO taille du bouton
        backButton.setPosition(20, 20);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backButton);
	}

	private void createLevelsTable() {
	    // Créer une table pour organiser les niveaux
        levelsTable = new Table();
        levelsTable.defaults().pad(10);

        // Ajouter un titre
        Label titleLabel = new Label("Sélection des Niveaux", skin, "title");
//        titleLabel.setColor(Color.GOLD);
        levelsTable.add(titleLabel).colspan(3).center();
        levelsTable.row();

        // TODO ajouter des niveaux dans assets/levels/level*.json
        // Ajouter les boutons de niveaux (3 par ligne)
        for (int i = 0; i < levelsNb.size(); i++) {
            if (i % 3 == 0 && i != 0) {
                levelsTable.row(); // Nouvelle ligne tous les 3 niveaux
            }

            int num = levelsNb.get(i);
            TextButton levelButton = new TextButton(""+num, skin);

            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new GameScreen(game, num)); // Lancer le niveau
                }
            });
            
            levelsTable.add(levelButton).width(200).height(60);
        }
        // TODO ajouter des étoiles pour les niveaux déjà réussis

        // Créer un ScrollPane pour permettre le défilement (utile pour mobile)
        ScrollPane scrollPane = new ScrollPane(levelsTable, skin);
        scrollPane.setScrollingDisabled(true, false); // Désactiver le défilement vertical
        scrollPane.setFlickScroll(true); // Permettre le flick (glisser rapidement)

        // Ajouter le ScrollPane à la scène
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.add(scrollPane).expand().fill();
        stage.addActor(mainTable);
	}

	private void loadLevels() {
        levelsNb = new ArrayList<>();

        // Charger tous les fichiers de niveaux dans assets/levels/
        for (int i = 0; i <= 100; i++) {
            String filePath = "assets/levels/level" + i + ".json";
            if (Gdx.files.internal(filePath).exists()) {
                levelsNb.add(i);
            }
        }
	}

	@Override
	public void render(float delta) {
        // Effacer l'écran
        ScreenUtils.clear(0, 0, 0, 1);

        // Dessiner le fond
        game.batch.begin();
        game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // Mettre à jour et dessiner le Stage
        stage.act(delta);
        stage.draw();
	}

	@Override
	public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        skin.dispose();
        background.dispose();
	}

}
