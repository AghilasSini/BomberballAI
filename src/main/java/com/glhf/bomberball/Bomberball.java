package com.glhf.bomberball;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.glhf.bomberball.ai.FactoryMethod;
import com.glhf.bomberball.config.AppConfig;
import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.screens.GameMultiScreen;
import com.glhf.bomberball.screens.MultiMenuScreen;

import com.glhf.bomberball.utils.Resolutions;
import com.glhf.bomberball.utils.VectorInt2;

public class Bomberball extends Game {

	public static Bomberball instance;
    public static float time_elapsed;
    public static boolean debug = false;
    private MultiMenuScreen screen;
	private ArrayList<Player> players;
	private String player1=null;
	private String player2=null;
	

	// Default Constructor (No AI Constructor)
	public Bomberball() {
		
	}
	
	
	// Constructor For AI
	public Bomberball(String player1,String player2) {
	 this.player1=player1;
	 this.player2=player2;
		
	
	}
	@Override
	public void create() {
		instance = this;
		Bomberball.time_elapsed = 0;
		Graphics.load();
        AppConfig config = AppConfig.get();
        Translator.load(config.language);
        
        // Instead  of the Welcoming Menu Screen 
     
        this.screen=new MultiMenuScreen();
        this.screen.saveToConfig();
        
        GameMultiConfig configMultiPlayers = GameMultiConfig.get();
        this.players = new ArrayList<>();
        
        
        FactoryMethod  factoryAi = new FactoryMethod();
        GameMultiScreen gameMultiScreen=null;
        
        if(player1!=null && player2!=null) {
	        // player1 position
	        VectorInt2 pos1 = screen.maze.getSpawn_positions().get(0);
	        players.add(factoryAi.getAI(player1,configMultiPlayers, configMultiPlayers.player_skins[0], screen.maze.getCells()[pos1.x][pos1.y],0));
	        // player2 position 
			VectorInt2 pos2 = screen.maze.getSpawn_positions().get(1);
			players.add(factoryAi.getAI(player2,configMultiPlayers, configMultiPlayers.player_skins[1], screen.maze.getCells()[pos2.x][pos2.y],1));
			gameMultiScreen = new GameMultiScreen(this.screen.maze, this.screen.getMazeId(),this.players);
			
        }
        else {
        	gameMultiScreen = new GameMultiScreen(this.screen.maze, 0);
        }
       
        changeScreen(gameMultiScreen);
	}
	

	@Override
	public void render() {
		super.render(); // Renders current screen
		time_elapsed += Gdx.graphics.getDeltaTime();
	}

	public static void changeScreen(Screen screen) {
		instance.setScreen(screen);
	}
	public static void reinitialize() {
		instance.create();
	}

	public static void resizeWindow(Resolutions res){
		instance.resize(res.width, res.height);
		Gdx.graphics.setWindowedMode(res.width, res.height);
		//TODO résoudre le problème lors du changement de résolutions (résolution du texte et autre)
	}
}
