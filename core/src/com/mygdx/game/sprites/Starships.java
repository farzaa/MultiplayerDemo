package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by farza on 1/23/2016.
 */
public class Starships extends Sprite{
    Vector2 previousPosition;
    public Starships(Texture texture) {
        super(texture);
    }

    public boolean hasMoved() {
        if(previousPosition.x != getX() || previousPosition.y != getY()) {
            previousPosition.x = getX();
            previousPosition.y = getY();
            return true;
        }

        return false;
    }
}
