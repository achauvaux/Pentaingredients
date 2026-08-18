package com.nicolasgarland.pentaingredients;

//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
//public class Main extends ApplicationAdapter {
//    private SpriteBatch batch;
//    private Texture image;
//
//    @Override
//    public void create() {
//        batch = new SpriteBatch();
//        image = new Texture("libgdx.png");
//        System.out.println("Mon jeu de casse-tête est lancé !");
//        LevelManager levelManager = new LevelManager();
//        levelManager.loadLevels();
//        Level level1 = levelManager.getLevel(1);
//        System.out.println("Niveau chargé : " + level1.name);
//    }
//
//    @Override
//    public void render() {
//        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
//        batch.begin();
//        batch.draw(image, 140, 210);
//        batch.end();
//    }
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//        image.dispose();
//    }
//}

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nicolasgarland.pentaingredients.screens.MainMenuScreen;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
	public FitViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // use libGDX's default font
     	font = new BitmapFont();
     	viewport = new FitViewport(8, 5);
     		
     	//font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height 
     	font.setUseIntegerPositions(false);
     	font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());
     		
        this.setScreen(new MainMenuScreen(this)); // Affiche le menu au démarrage
    }

    @Override
    public void render() {
        super.render(); // Appelle le render() de l'écran actuel
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}