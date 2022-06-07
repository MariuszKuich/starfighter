package pl.mariuszk.starfighter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerShip extends Ship {

    int lives;

    PlayerShip(float xCentre, float yCentre, float width, float height, float movementSpeed,
                      int shield, float laserWidth, float laserHeight, float laserMovementSpeed,
                      float timeBetweenShots, TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion, TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, shield, laserWidth, laserHeight,
                laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        lives = 3;
    }

    @Override
    Laser[] fireLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(boundingBox.x + boundingBox.width * 0.07f,
                boundingBox.y + boundingBox.height * 0.45f, laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);
        lasers[1] = new Laser(boundingBox.x + boundingBox.width * 0.93f,
                boundingBox.y + boundingBox.height * 0.45f, laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;

        return lasers;
    }

    @Override
    void draw(Batch batch) {
        draw(batch, boundingBox.y + boundingBox.height * 0.2f);
    }
}
