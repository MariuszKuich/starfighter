package pl.mariuszk.starfighter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class EnemyShip extends Ship {

    EnemyShip(float xCentre, float yCentre, float width, float height, float movementSpeed,
                      int shield, float laserWidth, float laserHeight, float laserMovementSpeed,
                      float timeBetweenShots, TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion, TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, shield, laserWidth, laserHeight,
                laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
    }

    @Override
    Laser[] fireLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(xPosition + width * 0.25f, yPosition - laserHeight,
                laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
        lasers[1] = new Laser(xPosition + width * 0.75f, yPosition - laserHeight,
                laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;

        return lasers;
    }

    @Override
    void draw(Batch batch) {
        draw(batch, yPosition - height * 0.2f);
    }
}