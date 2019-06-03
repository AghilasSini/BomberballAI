package com.glhf.bomberball.ai;

import java.util.Vector;

import com.glhf.bomberball.ai.alpha.AlphaBeta;
import com.glhf.bomberball.ai.bravo.NotreIA;
import com.glhf.bomberball.ai.charlie.AlphaBetaCharlie;
import com.glhf.bomberball.ai.delta.DeltaAI;
import com.glhf.bomberball.ai.echo.AlphaBetaEcho;
import com.glhf.bomberball.ai.foxtrot.IAAlphaBetaV1;
import com.glhf.bomberball.ai.golf.GolfAI;
import com.glhf.bomberball.ai.hotel.AlphaBetaID;
import com.glhf.bomberball.ai.india.AdvanceAI;
import com.glhf.bomberball.ai.juliett.AlphaBetaJuliett;
import com.glhf.bomberball.ai.kilo.FirstAI;
import com.glhf.bomberball.ai.lima.LimaAI;
import com.glhf.bomberball.ai.mike.BombyMike_AlphaBeta;
import com.glhf.bomberball.ai.novembre.AlphaBetaNovembre;
import com.glhf.bomberball.ai.oscar.OscarAI;
import com.glhf.bomberball.ai.papa.PapaIA;
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
	    		  player=new AlphaBeta(configMultiPlayers,player_skin,playerId);
		    		cell.addGameObject(player);
		    		return player;	
		
			// Groupe Bravo
			case "Bravo":
				  player=new NotreIA(configMultiPlayers,player_skin,playerId);
		    		cell.addGameObject(player);
		    		return player;	
			// Groupe Charlie
			case "Charlie":
	    	     player=new AlphaBetaCharlie(configMultiPlayers,player_skin,playerId);
	    		cell.addGameObject(player);
	    		return player;	
	    	// Groupe Delta
			case "Delta":
			     player=new DeltaAI(configMultiPlayers,player_skin,playerId);
		    	 cell.addGameObject(player);
		    	 return player;
			// Groupe Echo
			case "Echo":
                player=new AlphaBetaEcho(configMultiPlayers,player_skin,playerId);
                cell.addGameObject(player);
				return player;
			// Groupe Foxtrot
			case "Foxtrot":
				player=new IAAlphaBetaV1(configMultiPlayers, player_skin, playerId);
				cell.addGameObject(player);
				return player;
				
			// Groupe Golf
			case "Golf":
				player=new GolfAI(configMultiPlayers, player_skin, playerId);
				cell.addGameObject(player);
				return player;
				
			// Groupe Hotel
			case "Hotel":
				player=new AlphaBetaID(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
				
			// Groupe India
			case "India":
				player=new AdvanceAI(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
				
			// Groupe Juliett
			case "Juliett":
				player=new AlphaBetaJuliett(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;

			//Groupe  Kilo
			case "Kilo":
				player=new FirstAI(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;

			//Groupe  Lima
			case "Lima":
				player=new LimaAI(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
			
			//Groupe  Mike
			case "Mike":
				player=new BombyMike_AlphaBeta(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
			//  Groupe Novembre
			case "Novembre":
				player=new AlphaBetaNovembre(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
				
			//Groupe Oscar
			case "Oscar":
				player=new OscarAI(configMultiPlayers,player_skin,playerId);
				cell.addGameObject(player);
				return player;
			// Groupe Papa
			case "Papa":
				player=new PapaIA(configMultiPlayers,player_skin,playerId);
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
