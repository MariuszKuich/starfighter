package pl.mariuszk.starfighter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class Laser {

    //position and dimensions
    float xPosition; //bottom centre of the laser
    float yPosition; //bottom centre of the laser
    float width;
    float height;

    //laser physical characteristics
    float movementSpeed; //world units per second

    //graphics;
    TextureRegion textureRegion;

    public Laser(float xPosition, float yPosition, float width, float height, float movementSpeed,
                 TextureRegion textureRegion) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.movementSpeed = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch) {
        batch.draw(textureRegion, xPosition - width / 2, yPosition, width, height);
    }
}
