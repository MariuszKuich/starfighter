package pl.mariuszk.starfighter;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pl.mariuszk.starfighter.StarfighterGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Starfighter");
		config.setWindowSizeLimits(360, 640, 360, 640);
		new Lwjgl3Application(new StarfighterGame(), config);
	}
}
