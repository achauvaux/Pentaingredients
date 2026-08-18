package com.nicolasgarland.pentaingredients;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nicolasgarland.pentaingredients.graphics.IngredientIcons;
import com.nicolasgarland.pentaingredients.screens.MainMenuScreen;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
	public FitViewport viewport;
	/** Icônes des ingrédients, chargées une fois et partagées par tous les écrans. */
	public IngredientIcons ingredientIcons;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ingredientIcons = new IngredientIcons();
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
        font.dispose();
        ingredientIcons.dispose();
    }
}