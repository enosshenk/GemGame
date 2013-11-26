package com.shenko.rockhound;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Rockhound";
		cfg.useGL20 = false;
		cfg.width = 1024;
		cfg.height = 584;
		
		new LwjglApplication(new Rockhound(), cfg);
	}
}
