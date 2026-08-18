package com.nicolasgarland.pentaingredients.actors;

import com.badlogic.gdx.graphics.Color;
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
    public void draw(Batch batch, float parentAlpha) {
        Color couleur = getColor();
        float opacite = couleur.a * parentAlpha * (discretSiVide && item == null ? 0.35f : 1f);
        batch.setColor(couleur.r, couleur.g, couleur.b, opacite);
        batch.draw(slotTexture, getX(), getY(), getWidth(), getHeight());

        // Dessiner une bordure si la case est sélectionnée
        if (isSelected()) {
            batch.setColor(Color.YELLOW);
            batch.draw(slotTexture, getX() - 2, getY() - 2, getWidth() + 4, getHeight() + 4);
        }

        // L'icône garde ses propres couleurs, elle n'hérite pas de la teinte du cadre
        if (item != null) {
            Texture icone = icones.get(item.id);
            if (icone != null) {
                batch.setColor(Color.WHITE);
                batch.draw(icone, getX() + 5, getY() + 5, getWidth() - 10, getHeight() - 10);
            }
        }

        batch.setColor(Color.WHITE);
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
