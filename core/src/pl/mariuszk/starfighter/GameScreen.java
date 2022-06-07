package pl.mariuszk.starfighter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

class GameScreen implements Screen {

    //screen
    private final Camera camera;
    private final Viewport viewport;

    //graphics
    private final SpriteBatch batch;
    private final TextureRegion[] backgrounds;
    TextureRegion enemyShipTextureRegion;
    TextureRegion enemyShieldTextureRegion;
    TextureRegion enemyLaserTextureRegion;
    private final Texture explosionTexture;

    //timing
    private final float[] backgroundOffsets = {0, 0, 0, 0};
    private final float backgroundMaxScrollingSpeed;
    private float enemySpawnTimer = 0;

    //world parameters
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;

    //game objects
    private final PlayerShip playerShip;
    private final LinkedList<EnemyShip> enemyShipList;
    private final List<Laser> playerLaserList;
    private final List<Laser> enemyLaserList;
    private final LinkedList<Explosion> explosionList;
    private int score = 0;

    //Heads-Up Display
    BitmapFont font;
    float hudVerticalMargin;
    float hudLeftX;
    float hudRightX;
    float hudCentreX;
    float hudRow1Y;
    float hudRow2Y;
    float hudSectionWidth;


    GameScreen() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //set up the texture atlas
        TextureAtlas textureAtlas = new TextureAtlas("images.atlas");

        //setting up background
        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("Starscape00");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundMaxScrollingSpeed = (float)(WORLD_HEIGHT) / 4;

        //initialize texture regions
        TextureRegion playerShipTextureRegion = textureAtlas.findRegion("playerShip2_green");
        TextureRegion playerShieldTextureRegion = textureAtlas.findRegion("shield2");
        TextureRegion playerLaserTextureRegion = textureAtlas.findRegion("laserGreen03");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyBlue1");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserBlue13");
        explosionTexture = new Texture("exp.png");

        //set up game objects
        playerShip = new PlayerShip((float)WORLD_WIDTH / 2, (float)WORLD_HEIGHT / 4,
                10, 10, 48, 3,
                0.4f, 4, 45, 0.5f,
                playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShipList = new LinkedList<>();

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();
        explosionList = new LinkedList<>();

        batch = new SpriteBatch();

        prepareHUD();
    }

    private void prepareHUD() {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1, 1, 1, 0.3f);
        fontParameter.borderColor = new Color(0, 0, 0, 0.3f);

        font = fontGenerator.generateFont(fontParameter);
        font.getData().setScale(0.08f);

        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = (float)WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCentreX = (float)WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = (float) WORLD_WIDTH /3;
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        //scrolling background
        renderBackground(deltaTime);

        detectInput(deltaTime);
        playerShip.update(deltaTime);

        spawnEnemyShips(deltaTime);

        ListIterator<EnemyShip> enemyShipsListIterator = enemyShipList.listIterator();
        while (enemyShipsListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipsListIterator.next();

            moveEnemy(enemyShip, deltaTime);
            enemyShip.update(deltaTime);
            enemyShip.draw(batch);

            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                enemyLaserList.addAll(Arrays.asList(lasers));
            }
        }

        //player ship
        playerShip.draw(batch);

        //lasers
        renderLasers(deltaTime);

        //detect collisions
        detectCollisions();

        //explosions
        updateAndRenderExplosions(deltaTime);

        //hud rendering
        updateAndRenderHud();

        batch.end();
    }

    private void spawnEnemyShips(float deltaTime) {
        enemySpawnTimer += deltaTime;
        float timeBetweenEnemySpawns = 1f;
        if (enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyShipList.add(
                    new EnemyShip(StarfighterGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                            WORLD_HEIGHT - 5,
                            10, 10, 48, 1,
                            0.3f, 5, 50, 0.8f,
                            enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion)
            );
            enemySpawnTimer -= timeBetweenEnemySpawns;
        }
    }


    private void detectInput(float deltaTime) {
        float leftLimit = -playerShip.boundingBox.x;
        float downLimit = -playerShip.boundingBox.y;
        float rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        float upLimit = (float)WORLD_HEIGHT / 2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        //keyboard input
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
            playerShip.translate(Math.min(playerShip.movementSpeed * deltaTime, rightLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
            playerShip.translate(0f, Math.min(playerShip.movementSpeed * deltaTime, upLimit));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
            playerShip.translate(Math.max(-playerShip.movementSpeed * deltaTime, leftLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
            playerShip.translate(0f, Math.max(-playerShip.movementSpeed * deltaTime, downLimit));
        }

        //touch input (also mouse)
        if (Gdx.input.isTouched()) {
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            //transforming screen coordinates to world coordinates
            touchPoint = viewport.unproject(touchPoint);

            Vector2 playerShipCentre =
                    new Vector2(playerShip.boundingBox.x + playerShip.boundingBox.width / 2,
                            playerShip.boundingBox.y + playerShip.boundingBox.height / 2);
            float touchDistance = touchPoint.dst(playerShipCentre);

            float movementThreshold = 0.5f;
            if (touchDistance > movementThreshold) {
                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                //scale to the maximum speed of the ship
                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;

                if (xMove > 0) {
                    xMove = Math.min(xMove, rightLimit);
                } else {
                    xMove = Math.max(xMove, leftLimit);
                }
                if (yMove > 0) {
                    yMove = Math.min(yMove, upLimit);
                } else {
                    yMove = Math.max(yMove, downLimit);
                }
//
                playerShip.translate(xMove, yMove);
            }
        }
    }

    private void moveEnemy(EnemyShip enemyShip, float deltaTime) {
        float leftLimit = -enemyShip.boundingBox.x;
        float downLimit = (float)WORLD_HEIGHT / 2 - enemyShip.boundingBox.y;
        float rightLimit = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        float upLimit = WORLD_HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;

        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * deltaTime;

        if (xMove > 0) {
            xMove = Math.min(xMove, rightLimit);
        } else {
            xMove = Math.max(xMove, leftLimit);
        }
        if (yMove > 0) {
            yMove = Math.min(yMove, upLimit);
        } else {
            yMove = Math.max(yMove, downLimit);
        }

        enemyShip.translate(xMove, yMove);
    }

    private void renderBackground(float deltaTime) {
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 8;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += deltaTime * backgroundMaxScrollingSpeed;

        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > WORLD_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }

            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer], WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer] + WORLD_HEIGHT,
                    WORLD_WIDTH, WORLD_HEIGHT);
        }
    }

    private void renderLasers(float deltaTime) {
        //create new lasers
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            playerLaserList.addAll(Arrays.asList(lasers));
        }
        //draw lasers
        //remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    private void detectCollisions() {
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.boundingBox)) {
                    if (enemyShip.hitAndCheckDestroyed(laser)) {
                        enemyShipListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture,
                                new Rectangle(enemyShip.boundingBox), 0.7f));
                        score += 100;
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
        laserListIterator = enemyLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                if (playerShip.hitAndCheckDestroyed(laser)) {
                    explosionList.add(new Explosion(explosionTexture,
                            new Rectangle(playerShip.boundingBox), 1.6f));
                    playerShip.shield = 10;
                    playerShip.lives--;
                }
                laserListIterator.remove();
            }
        }
    }

    private void updateAndRenderExplosions(float deltaTime) {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if (explosion.isFinished()) {
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }
    }

    private void updateAndRenderHud() {
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Shield", hudCentreX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);

        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftX, hudRow2Y,
                hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield), hudCentreX, hudRow2Y, hudSectionWidth,
                Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.lives), hudRightX, hudRow2Y, hudSectionWidth,
                Align.right, false);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {

    }
}
