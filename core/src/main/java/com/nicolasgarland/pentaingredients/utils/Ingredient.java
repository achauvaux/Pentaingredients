package com.nicolasgarland.pentaingredients.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Ingredient {
	public static enum Famille {
		VEGETALE,
		ANIMALE,
		MINERALE,
		MAGIQUE
	}
	
	public int id;
	public String name;
	public Famille famille;
	public int cout;
	public int[] energies;
	public Texture icon; // Icône de l'objet
    
	
	public Ingredient() {};
	
	public Ingredient(int id, String name, Famille fam, int cout, int[] energies) {
		this.id = id;
		this.name = name;
		this.famille = fam;
		this.cout = cout;
		this.energies = energies;
		this.icon = getIcon();
	}

	public Texture getIcon() {
		if(icon != null) return icon;
		String filePath = "assets/ingredients/ingr" + this.id + ".png";
        if (Gdx.files.internal(filePath).exists()) {
        	icon = (new Texture(Gdx.files.internal(filePath)));
        	return icon;
        } else {
        	Gdx.app.log("ERROR", "in loaging img : " + Gdx.files.internal(filePath).file().getAbsolutePath());
        	return null;
        }
	};

}
