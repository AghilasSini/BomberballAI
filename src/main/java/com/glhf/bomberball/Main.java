package com.glhf.bomberball;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.glhf.bomberball.config.AppConfig;



public class Main{
	
	//private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
	
		
		// Default Mode 
		AppConfig app_config = AppConfig.get();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = app_config.resolution.width;
		config.height = app_config.resolution.height;
		config.fullscreen = app_config.fullscreen;
		config.resizable = true;
		Bomberball bomberball=null;
					
		// Console mode
		if (args.length !=0) {
			// IA mode 
			System.out.println("AI Mode");
			bomberball=new Bomberball(args[0],args[1]);		
			
		}else {
				
			
			bomberball=new Bomberball();
			
		}
		new LwjglApplication(bomberball,config);
		
	}
	

}