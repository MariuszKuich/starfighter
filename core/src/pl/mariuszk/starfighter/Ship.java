package pl.mariuszk.starfighter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

abstract class Ship {

    //ship characteristics
    float movementSpeed; //world units per seconds
    int shield;

    //position & dimension
    float xPosition;
    float yPosition;
    float width;
    float height;
    Rectangle boundingBox;

    //laser information;
    float laserWidth;
    float laserHeight;
    float laserMovementSpeed;
    float timeBetweenShots;
    float timeSinceLastShot = 0;

    //graphics
    TextureRegion shipTextureRegion;
    TextureRegion shieldTextureRegion;
    TextureRegion laserTextureRegion;

    Ship(float xCentre, float yCentre, float width, float height, float movementSpeed,
                int shield, float laserWidth, float laserHeight, float laserMovementSpeed,
                float timeBetweenShots, TextureRegion shipTextureRegion,
                TextureRegion shieldTextureRegion, TextureRegion laserTextureRegion) {
        this.xPosition = xCentre - width / 2;
        this.yPosition = yCentre - height / 2;
        this.width = width;
        this.height = height;
        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.laserWidth = laserWidth;
        this.laserHeight = laserHeight;
        this.laserMovementSpeed = laserMovementSpeed;
        this.timeBetweenShots = timeBetweenShots;
        this.shipTextureRegion = shipTextureRegion;
        this.shieldTextureRegion = shieldTextureRegion;
        this.laserTextureRegion = laserTextureRegion;

        boundingBox = new Rectangle(xPosition, yPosition, width, height);
    }

    void update(float deltaTime) {
        boundingBox.set(xPosition, yPosition, width, height);
        timeSinceLastShot += deltaTime;
    }

    abstract void draw(Batch batch);

    void draw(Batch batch, float shieldPosition) {
        batch.draw(shipTextureRegion, xPosition, yPosition, width, height);
        if (shield > 0) {
            batch.draw(shieldTextureRegion, xPosition, shieldPosition, width, height);
        }
    }

    boolean canFireLaser() {
        return (timeSinceLastShot - timeBetweenShots >= 0);
    }

    abstract Laser[] fireLasers();

    boolean intersects(Rectangle otherRectangle) {
        return boundingBox.overlaps(otherRectangle);
    }

    void hit(Laser laser) {
        if (shield > 0) {
            shield--;
        }
    }
}
