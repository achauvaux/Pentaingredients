package com.nicolasgarland.pentaingredients.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nicolasgarland.pentaingredients.Main;

public class OptionsScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;

    public OptionsScreen(final Main game) {
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

        // TODO ajouter de quoi faire varier le son, et ajouter des sons
        
        // TODO ajouter de quoi faire varier la résolution
        
        // Créer les boutons
        TextButton returnButton = new TextButton("Retour", skin, "default");
        TextButton rulesButton = new TextButton("Règles", skin, "default");

        // Positionner les boutons
        rulesButton.setPosition(
                Gdx.graphics.getWidth() / 2 - rulesButton.getWidth() / 2,
                Gdx.graphics.getHeight() / 2
        );
        returnButton.setPosition(
            Gdx.graphics.getWidth() / 2 - returnButton.getWidth() / 2,
            Gdx.graphics.getHeight() / 2 - 50
        );

        // Ajouter des actions aux boutons
        rulesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showRulesDialog();
            }
        });
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // TODO taille des boutons
        // Ajouter les boutons à la scène
        stage.addActor(rulesButton);
        stage.addActor(returnButton);
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
        // TODO écrire les règles

        // Ajouter un bouton "Fermer"
        rulesDialog.button("Fermer");

        // Afficher le dialog
        rulesDialog.show(stage);
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
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        background.dispose();
    }
}
