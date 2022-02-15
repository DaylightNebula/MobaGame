package daylightnebula.mobagamegdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import daylightnebula.mobagamegdx.MobaGame;
import daylightnebula.mobagamegdx.tests.ModelViewer;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 4;
		new LwjglApplication(new MobaGame(), config);
	}
}
