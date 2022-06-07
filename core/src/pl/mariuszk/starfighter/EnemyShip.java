package pl.mariuszk.starfighter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

class EnemyShip extends Ship {

    Vector2 directionVector;
    float timeSinceLastDirectionChange = 0;
    float directionChangeFrequency = 0.75f;

    EnemyShip(float xCentre, float yCentre, float width, float height, float movementSpeed,
                      int shield, float laserWidth, float laserHeight, float laserMovementSpeed,
                      float timeBetweenShots, TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion, TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, shield, laserWidth, laserHeight,
                laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);

        directionVector = new Vector2(0, -1);
    }

    @Override
    Laser[] fireLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(boundingBox.x + boundingBox.width * 0.25f, boundingBox.y - laserHeight,
                laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
        lasers[1] = new Laser(boundingBox.x + boundingBox.width * 0.75f, boundingBox.y - laserHeight,
                laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;

        return lasers;
    }

    @Override
    void draw(Batch batch) {
        draw(batch, boundingBox.y - boundingBox.height * 0.2f);
    }

    @Override
    void update(float deltaTime) {
        super.update(deltaTime);
        timeSinceLastDirectionChange += deltaTime;
        if (timeSinceLastDirectionChange > directionChangeFrequency) {
            randomizeDirectionVector();
            timeSinceLastDirectionChange -= directionChangeFrequency;
        }
    }

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    private void randomizeDirectionVector() {
        double bearing = StarfighterGame.random.nextDouble() * 6.283185;
        directionVector.x = (float) Math.sin(bearing);
        directionVector.y = (float) Math.cos(bearing);
    }
}
