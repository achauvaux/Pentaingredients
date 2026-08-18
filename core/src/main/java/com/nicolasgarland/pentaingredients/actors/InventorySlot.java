package com.nicolasgarland.pentaingredients.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Texture;
import com.nicolasgarland.pentaingredients.graphics.IngredientIcons;
import com.nicolasgarland.pentaingredients.utils.Ingredient;
import com.nicolasgarland.pentaingredients.utils.Positions;
import com.nicolasgarland.pentaingredients.utils.Positions.Emplacement;

public class InventorySlot extends Actor {
    public static final int SLOT_SIZE = 64;  // Taille d'une case (en pixels)
    private TextureRegion slotTexture;       // Texture de fond de la case
    private Ingredient item;              // Objet dans cette case (null si vide)
    private boolean isSelected;               // Case sélectionnée ?
    private Positions.Emplacement posEmpl;
    private int posInt;
    private final IngredientIcons icones;

    /** Sur l'étagère, une case vide s'efface ; sur le pentagramme, elle appelle. */
    private boolean discretSiVide;

    /** Temps écoulé, qui fait battre la surbrillance de la case sélectionnée. */
    private float temps;

    public InventorySlot(TextureRegion slotTexture, Emplacement empl, int num, IngredientIcons icones) {
        this.slotTexture = slotTexture;
        this.icones = icones;
        this.item = null;
        this.setSelected(false);
        setSize(SLOT_SIZE, SLOT_SIZE);
        this.setPosEmpl(empl);
        this.setPosInt(num);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        temps += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // L'acteur se dessine à la main : c'est ici, et nulle part ailleurs, que
        // l'échelle animée doit être appliquée. Elle rayonne depuis le centre.
        float largeur = getWidth() * getScaleX();
        float hauteur = getHeight() * getScaleY();
        float x = getX() + (getWidth() - largeur) / 2f;
        float y = getY() + (getHeight() - hauteur) / 2f;

        Color couleur = getColor();
        float opacite = couleur.a * parentAlpha * (discretSiVide && item == null ? 0.35f : 1f);
        batch.setColor(couleur.r, couleur.g, couleur.b, opacite);
        batch.draw(slotTexture, x, y, largeur, hauteur);

        // La case sélectionnée respire, plutôt que de porter un liseré figé
        if (isSelected()) {
            float battement = 0.55f + 0.45f * MathUtils.sin(temps * 6f);
            batch.setColor(Color.YELLOW.r, Color.YELLOW.g, Color.YELLOW.b, battement * parentAlpha);
            batch.draw(slotTexture, x - 2, y - 2, largeur + 4, hauteur + 4);
        }

        // L'icône garde ses propres couleurs, elle n'hérite pas de la teinte du cadre
        if (item != null) {
            Texture icone = icones.get(item.id);
            if (icone != null) {
                batch.setColor(1f, 1f, 1f, parentAlpha);
                batch.draw(icone, x + 5, y + 5, largeur - 10, hauteur - 10);
            }
        }

        batch.setColor(Color.WHITE);
    }

    /** Petit sursaut, pour signaler qu'un ingrédient vient d'atterrir ici. */
    public void animerPose() {
        clearActions();
        setScale(1f);
        addAction(Actions.sequence(
                Actions.scaleTo(1.28f, 1.28f, 0.09f, Interpolation.pow2Out),
                Actions.scaleTo(1f, 1f, 0.17f, Interpolation.pow2In)));
    }

    /** Atténue le cadre tant qu'aucun ingrédient n'occupe la case. */
    public void setDiscretSiVide(boolean discretSiVide) {
        this.discretSiVide = discretSiVide;
    }

    // Méthodes pour gérer l'objet
    public void setItem(Ingredient item) {
        this.item = item;
    }

    public Ingredient getItem() {
        return item;
    }

    public void clear() {
        this.item = null;
    }

    public boolean hasItem() {
        return item != null;
    }

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getPosInt() {
		return posInt;
	}

	public void setPosInt(int posInt) {
		this.posInt = posInt;
	}

	public Positions.Emplacement getPosEmpl() {
		return posEmpl;
	}

	public void setPosEmpl(Positions.Emplacement posEmpl) {
		this.posEmpl = posEmpl;
	}
}
