package pl.mariuszk.starfighter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class Laser {

    //position and dimensions
    Rectangle boundingBox; //bottom centre of the laser

    //laser physical characteristics
    float movementSpeed; //world units per second

    //graphics;
    TextureRegion textureRegion;

    public Laser(float xCentre, float yBottom, float width, float height, float movementSpeed,
                 TextureRegion textureRegion) {
        boundingBox = new Rectangle(xCentre - width / 2, yBottom, width, height);
        this.movementSpeed = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch) {
        batch.draw(textureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
