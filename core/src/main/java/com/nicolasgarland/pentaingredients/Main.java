package com.nicolasgarland.pentaingredients;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nicolasgarland.pentaingredients.graphics.IngredientIcons;
import com.nicolasgarland.pentaingredients.utils.Progression;
import com.nicolasgarland.pentaingredients.screens.MainMenuScreen;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
	public FitViewport viewport;
	/** Icônes des ingrédients, chargées une fois et partagées par tous les écrans. */
	public IngredientIcons ingredientIcons;

	/** Ce que le joueur a déjà réussi, partagé par tous les écrans. */
	public Progression progression;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ingredientIcons = new IngredientIcons();
        progression = Progression.charger();
        // use libGDX's default font
     	font = new BitmapFont();
     	viewport = new FitViewport(8, 5);
     		
     	//font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height 
     	font.setUseIntegerPositions(false);
     	font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());
     		
        changerEcran(new MainMenuScreen(this)); // Affiche le menu au démarrage
    }

    /**
     * Remplace l'écran courant et libère le précédent.
     *
     * <p>{@link Game#setScreen} se contente d'appeler {@code hide()} : sans
     * cela, chaque changement d'écran abandonnerait ses textures. La libération
     * est différée d'une trame car la bascule est presque toujours déclenchée
     * depuis un clic, donc pendant que le stage sortant traite encore
     * l'événement.</p>
     */
    public void changerEcran(Screen nouvelEcran) {
        final Screen precedent = getScreen();
        setScreen(nouvelEcran);
        if (precedent != null) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    precedent.dispose();
                }
            });
        }
    }

    @Override
    public void render() {
        super.render(); // Appelle le render() de l'écran actuel
    }

    @Override
    public void dispose() {
        // Game.dispose() n'appelle que hide() sur l'écran courant.
        if (getScreen() != null) getScreen().dispose();
        batch.dispose();
        font.dispose();
        ingredientIcons.dispose();
    }
}