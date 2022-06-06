package pl.mariuszk.starfighter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class Ship {

    //ship characteristics
    float movementSpeed; //world units per seconds
    int shield;

    //position & dimension
    float xPosition;
    float yPosition;
    float width;
    float height;

    //graphics
    TextureRegion shipTexture;
    TextureRegion shieldTexture;

    public Ship(float xCentre, float yCentre, float width, float height, float movementSpeed,
                int shield, TextureRegion shipTexture, TextureRegion shieldTexture) {
        this.xPosition = xCentre - width / 2;
        this.yPosition = yCentre - height / 2;
        this.width = width;
        this.height = height;
        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.shipTexture = shipTexture;
        this.shieldTexture = shieldTexture;
    }

    public void draw(Batch batch) {
        batch.draw(shipTexture, xPosition, yPosition, width, height);
        if (shield > 0) {
            batch.draw(shieldTexture, xPosition, yPosition, width, height);
        }
    }
}
