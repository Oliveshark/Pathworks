package com.oliveshark.pathworks.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.oliveshark.pathworks.Pathworks;
import com.oliveshark.pathworks.config.Config;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Config.load();
		config.width = Config.getTileDimension() * Config.getGridWidth();
		config.height = Config.getTileDimension() * Config.getGridHeight();
		new LwjglApplication(new Pathworks(), config);
	}
}
