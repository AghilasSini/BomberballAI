package com.glhf.bomberball.ai;

import java.util.Vector;

import com.glhf.bomberball.ai.alpha.Alpha;
import com.glhf.bomberball.ai.bravo.Bravo;
import com.glhf.bomberball.ai.charlie.Charlie;

import com.glhf.bomberball.ai.delta.Delta;
import com.glhf.bomberball.ai.echo.Echo;
import com.glhf.bomberball.ai.foxtrot.Foxtrot;
import com.glhf.bomberball.ai.golf.Golf;
import com.glhf.bomberball.ai.hotel.Hotel;
import com.glhf.bomberball.ai.india.India;
import com.glhf.bomberball.ai.juliett.Juliett;
import com.glhf.bomberball.ai.kilo.Kilo;
import com.glhf.bomberball.ai.lima.Lima;
import com.glhf.bomberball.ai.mike.Mike;
import com.glhf.bomberball.ai.novembre.November;
import com.glhf.bomberball.ai.oscar.Oscar;
import com.glhf.bomberball.ai.papa.Papa;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.screens.MultiMenuScreen;



public class FactoryMethod {

	Vector<String> vectAi;

	
	public FactoryMethod()
	{
		this.vectAi = new Vector<String>();
		this.vectAi.add("RandomAI");
		this.vectAi.add("VanillaAI");
		this.vectAi.add("ChiliAI");
	
	}
	
	
	public AbstractAI getAI(String name, GameMultiConfig configMultiPlayers, String player_skin, Cell cell,int playerId) {
		try
		{

			AbstractAI player;
			switch (name) {
			case "RandomAI":
				player = new RandomAI(configMultiPlayers,player_skin,playerId);
			    cell.addGameObject(player);
			    return player;
			    
			case "VanillaAI":
				player=new VanillaAI(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
	    		return player;
	    		
	    	// Groupe Alpha
	    	case "Alpha":
	    		  player=new Alpha(configMultiPlayers,player_skin,playerId);
		    		cell.addGameObject(player);
		    		return player;	
		
			// Groupe Bravo
			case "Bravo":
				  player=new Bravo(configMultiPlayers,player_skin,playerId);
		    		cell.addGameObject(player);
		    		return player;	
			// Groupe Charlie
			case "Charlie":
	    	     player=new Charlie(configMultiPlayers,player_skin,playerId);
	    		cell.addGameObject(player);
	    		return player;	
	    	// Groupe Delta
			case "Delta":
			     player=new Delta(configMultiPlayers,player_skin,playerId);
		    	 cell.addGameObject(player);
		    	 return player;
			// Groupe Echo
			case "Echo":
                player=new Echo(configMultiPlayers,player_skin,playerId);
                cell.addGameObject(player);
				return player;
			// Groupe Foxtrot
			case "Foxtrot":
				player=new Foxtrot(configMultiPlayers, player_skin, playerId);
				cell.addGameObject(player);
				return player;
				
			// Groupe Golf
			case "Golf":
				player=new Golf(configMultiPlayers, player_skin, playerId);
				cell.addGameObject(player);
				return player;
				
			// Groupe Hotel
			case "Hotel":
				player=new Hotel(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
				
			// Groupe India
			case "India":
				player=new India(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
				
			// Groupe Juliett
			case "Juliett":
				player=new Juliett(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;

			//Groupe  Kilo
			case "Kilo":
				player=new Kilo(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;

			//Groupe  Lima
			case "Lima":
				player=new Lima(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
			
			//Groupe  Mike
			case "Mike":
				player=new Mike(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
			//  Groupe Novembre
			case "November":
				player=new November(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
				
			//Groupe Oscar
			case "Oscar":
				player=new Oscar(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
			// Groupe Papa
			case "Papa":
				player=new Papa(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
			case "ChiliAI":
				player = new ChiliAI(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
	    		return player;
	    		
			default:
				AbstractAI playerDefault=new RandomAI(configMultiPlayers,player_skin,playerId);
			    cell.addGameObject(playerDefault);
			    return playerDefault;
		
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.exit(-1);
		}

		return null;
	}
	
	

	
	
	
	

	public Vector<String> getVectAi()
	{
		return vectAi;
	}


	
	
}
