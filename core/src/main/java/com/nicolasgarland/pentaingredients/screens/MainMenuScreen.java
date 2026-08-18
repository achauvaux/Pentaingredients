package com.nicolasgarland.pentaingredients.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nicolasgarland.pentaingredients.Main;

public class MainMenuScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;

    public MainMenuScreen(final Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // TODO Charger le fond
        background = new Texture(Gdx.files.internal("assets/menu_background.png"));

        // Créer la scène et le viewport
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Charger la skin
//        Gdx.app.log("DEBUG", "Chemin : " + Gdx.files.internal("assets/skin/uiskin.json").file().getAbsolutePath());
        skin = new Skin(Gdx.files.internal("assets/skin/uiskin.json"));

        // Créer les boutons
        TextButton playButton = new TextButton("Jouer", skin, "default");
        TextButton optionsButton = new TextButton("Options", skin, "default");
        TextButton exitButton = new TextButton("Quitter", skin, "default");

        // Positionner les boutons
        playButton.setPosition(
            Gdx.graphics.getWidth() / 2 - playButton.getWidth() / 2,
            Gdx.graphics.getHeight() / 2 + 50
        );
        optionsButton.setPosition(
            Gdx.graphics.getWidth() / 2 - optionsButton.getWidth() / 2,
            Gdx.graphics.getHeight() / 2
        );
        exitButton.setPosition(
            Gdx.graphics.getWidth() / 2 - exitButton.getWidth() / 2,
            Gdx.graphics.getHeight() / 2 - 50
        );

        // Ajouter des actions aux boutons
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.changerEcran(new LevelSelectScreen(game));
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.changerEcran(new OptionsScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // TODO taille des boutons
        // Ajouter les boutons à la scène
        stage.addActor(playButton);
        stage.addActor(optionsButton);
        stage.addActor(exitButton);
    }

    @Override
    public void render(float delta) {
        // Effacer l'écran
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dessiner le fond
//        game.batch.begin();
//        game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        game.batch.end();

        // Dessiner la scène (boutons)
        stage.act(delta);
        stage.draw();
	}

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        // La libération revient à Main.changerEcran() : hide() est appelé à
        // chaque bascule, y compris quand l'écran doit survivre.
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        background.dispose();
    }
}

