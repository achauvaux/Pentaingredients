package com.nicolasgarland.pentaingredients.graphics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/**
 * Registre des icônes d'ingrédients : associe un identifiant d'ingrédient à sa
 * texture, la charge à la demande, et ne la charge qu'une seule fois.
 *
 * <p>Le modèle {@link com.nicolasgarland.pentaingredients.utils.Ingredient} ne
 * connaît plus le rendu. Deux conséquences : un ingrédient peut exister hors
 * contexte OpenGL (donc être testé), et les icônes survivent aux changements
 * d'écran au lieu d'être rechargées à chaque entrée dans un niveau.</p>
 */
public class IngredientIcons implements Disposable {

	private static final String PREFIXE = "assets/ingredients/ingr";

	private final Map<Integer, Texture> parIdentifiant = new HashMap<Integer, Texture>();

	/** Icône de l'ingrédient, ou {@code null} si le fichier est introuvable. */
	public Texture get(int idIngredient) {
		Texture icone = parIdentifiant.get(idIngredient);
		if (icone != null) return icone;

		String chemin = PREFIXE + idIngredient + ".png";
		if (!Gdx.files.internal(chemin).exists()) {
			Gdx.app.log("ERROR", "icône introuvable : " + chemin);
			return null;
		}

		icone = new Texture(Gdx.files.internal(chemin));
		parIdentifiant.put(idIngredient, icone);
		return icone;
	}

	@Override
	public void dispose() {
		for (Texture icone : parIdentifiant.values()) {
			icone.dispose();
		}
		parIdentifiant.clear();
	}
}
