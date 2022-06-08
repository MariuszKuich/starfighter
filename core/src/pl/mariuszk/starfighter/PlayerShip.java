package pl.mariuszk.starfighter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerShip extends Ship {

    int lives;
    boolean down;

    static int SHIELD_VALUE = 10;
    static int LIVES_VALUE = 3;

    PlayerShip(float xCentre, float yCentre, float width, float height, float movementSpeed,
                      float laserWidth, float laserHeight, float laserMovementSpeed,
                      float timeBetweenShots, TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion, TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, SHIELD_VALUE, laserWidth, laserHeight,
                laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        lives = LIVES_VALUE;
        down = false;
    }

    @Override
    Laser[] fireLasers() {
        if (down) {
            return new Laser[0];
        }
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
        if (down) {
            return;
        }
        draw(batch, boundingBox.y + boundingBox.height * 0.2f);
    }
}
