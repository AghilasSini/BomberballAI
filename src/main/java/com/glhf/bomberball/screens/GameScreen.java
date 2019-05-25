package com.glhf.bomberball.screens;




import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.glhf.bomberball.utils.Action;

import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.gameobject.NumberTurn;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.MazeDrawer;
import com.glhf.bomberball.maze.MazeTransversal;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;
import com.glhf.bomberball.utils.VectorInt2;


import java.util.ArrayList;
import java.util.List;

public abstract class GameScreen extends AbstractScreen {
    protected Timer.Task task;
    protected Maze maze;
    protected MazeDrawer maze_drawer;

    protected Player current_player;
    protected ArrayList<Cell> selected_cells = new ArrayList<>();
 
    
	
    public GameScreen(Maze maze) {
        super();
        this.maze = maze;
        this.maze_drawer = new MazeDrawer(maze,1/3f,1f,2/10f,1f, MazeDrawer.Fit.BEST);
        
    }

    @Override
    public void hide() {
        super.hide();
        Audio.silence();
    }

    @Override
    public void show() {
        super.show();
        // Audio.GAME_SONG.playMusique();
    }

    @Override
    public void registerActionsHandlers() {
        super.registerActionsHandlers();
        input_handler.registerActionHandler(Action.MODE_BOMB, this::setBombMode);
        input_handler.registerActionHandler(Action.MODE_MOVE, this::setMoveMode);
        input_handler.registerActionHandler(Action.ENDTURN, this::endTurn);
        input_handler.registerActionHandler(Action.DROP_BOMB, this::dropBombAt);
        input_handler.registerActionHandler(Action.DROP_BOMB_DOWN, () -> dropBomb(Directions.DOWN));
        input_handler.registerActionHandler(Action.DROP_BOMB_UP, () -> dropBomb(Directions.UP));
        input_handler.registerActionHandler(Action.DROP_BOMB_LEFT, () -> dropBomb(Directions.LEFT));
        input_handler.registerActionHandler(Action.DROP_BOMB_RIGHT, () -> dropBomb(Directions.RIGHT));
    }

    public void dropBombAt(float x, float y) {
        VectorInt2 cell_pos = maze_drawer.screenPosToCell(x, y);
        Directions dir = current_player.getCell().getCellDir(maze.getCellAt(cell_pos.x, cell_pos.y));
        if (dir != null) {
            dropBomb(dir);
            clearCellsEffect();
            setMoveEffect();
        }
    }

    protected void dropBomb(Directions dir) {
        if (current_player.dropBomb(dir)) {
            this.setMoveMode();
            this.setMoveEffect();
        }
    }

    protected void clearCellsEffect() {
        for (Cell c : selected_cells) {
            c.removeEffect();
        }
        selected_cells.clear();
    }

    protected void setBombEffect() {
        clearCellsEffect();
        ArrayList<Cell> cells_in_range = MazeTransversal.getReacheableCellsInRange(current_player.getCell(), 1);
        cells_in_range.remove(current_player.getCell());
        for (Cell c : cells_in_range) {
            c.setSelectEffect(Color.RED);
            selected_cells.add(c);
        }
    }

    protected void setMoveEffect() {
        clearCellsEffect();
        ArrayList<Cell> cells_in_range = MazeTransversal.getReacheableCellsInRange(current_player.getCell(), current_player.getNumberMoveRemaining());
        for (Cell c : cells_in_range) {
            c.setSelectEffect(Color.WHITE);
            selected_cells.add(c);
        }
    }

    // Methods to change the mod when click on a button in ActionPlayer bar
    public void setBombMode(){
        setBombEffect();
        input_handler.registerActionHandler(Action.MOVE_DOWN, () -> dropBomb(Directions.DOWN));
        input_handler.registerActionHandler(Action.MOVE_UP, () -> dropBomb(Directions.UP));
        input_handler.registerActionHandler(Action.MOVE_LEFT, () -> dropBomb(Directions.LEFT));
        input_handler.registerActionHandler(Action.MOVE_RIGHT, () -> dropBomb(Directions.RIGHT));
    }


    public void setMoveMode(){
        setMoveEffect();
        input_handler.registerActionHandler(Action.MOVE_DOWN, () -> moveCurrentPlayer(Directions.DOWN));
        input_handler.registerActionHandler(Action.MOVE_UP, () -> moveCurrentPlayer(Directions.UP));
        input_handler.registerActionHandler(Action.MOVE_LEFT, () -> moveCurrentPlayer(Directions.LEFT));
        input_handler.registerActionHandler(Action.MOVE_RIGHT, () -> moveCurrentPlayer(Directions.RIGHT));
    }

// 
    public boolean turnIsOver() {
    	return (current_player.getMovesRemaining() == 0 && current_player.getNumberBombRemaining() == 0);
    }

    
    public boolean isPossibleAction(Action action) {
    	List<Cell> adjacentCells = current_player.getCell().getAdjacentCells();
    	// End turn
    	if (action == Action.ENDTURN) {
    		return true;
    	}
    	// Right
		if (adjacentCells.get(0) != null && adjacentCells.get(0).isWalkable()) {
			if (current_player.getNumberBombRemaining() > 0 && action == Action.DROP_BOMB_RIGHT) {
				return true;
			}
			if (current_player.getMovesRemaining() > 0 && action == Action.MOVE_RIGHT) {
				return true;
			}
		}
		// Up
		if (adjacentCells.get(1) != null && adjacentCells.get(1).isWalkable()) {
			if (current_player.getNumberBombRemaining() > 0 && action == Action.DROP_BOMB_UP) {
				return true;
			}
			if (current_player.getMovesRemaining() > 0 && action == Action.MOVE_UP) {
				return true;
			}
		}
		// Left
		if (adjacentCells.get(2) != null && adjacentCells.get(2).isWalkable()) {
			if (current_player.getNumberBombRemaining() > 0 && action == Action.DROP_BOMB_LEFT) {
				return true;
			}
			if (current_player.getMovesRemaining() > 0 && action == Action.MOVE_LEFT) {
				return true;
			}
		}
		// Down
		if (adjacentCells.get(3) != null && adjacentCells.get(3).isWalkable()) {
			if (current_player.getNumberBombRemaining() > 0 && action == Action.DROP_BOMB_DOWN) {
				return true;
			}
			if (current_player.getMovesRemaining() > 0 && action == Action.MOVE_DOWN) {
				return true;
			}
		}
		return false;
    }
    
    public void applyAction(Action a) {
    	
		switch (a) {
		case MOVE_UP:
			moveCurrentPlayer(Directions.UP);
			
			break;
		case MOVE_DOWN:
			moveCurrentPlayer(Directions.DOWN);
			break;
		case MOVE_LEFT:
			moveCurrentPlayer(Directions.LEFT);
			break;
		case MOVE_RIGHT:
			moveCurrentPlayer(Directions.RIGHT);
			break;
		case DROP_BOMB_RIGHT:
			dropBomb(Directions.RIGHT);
			break;
		case DROP_BOMB_UP:
			dropBomb(Directions.UP);
			break;
		case DROP_BOMB_LEFT:
			
			dropBomb(Directions.LEFT);
			break;
		case DROP_BOMB_DOWN:
			
			dropBomb(Directions.DOWN);
			break;
		case ENDTURN:
			endTurn();
			
			break;
		default:
			break;
		}
		// Force to end the turn if no more action are possible
		if (a != Action.ENDTURN && turnIsOver()) {
			endTurn();
		}
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }   
    
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////

    protected void moveCurrentPlayer(Directions dir) {
//        try {
            current_player.move(dir);
            clearCellsEffect();
            setMoveEffect();
//        } catch (RuntimeException e) {
//            System.out.println("The player probably died");
//        }
    }

    public void endTurn()
    {
        input_handler.lock(true);
		clearCellsEffect();
	    maze.processEndTurn();
	    current_player.endTurn();
	    
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    NumberTurn.getInstance().decreaseTurn(1);
	    nextPlayer();

    }
    

    protected abstract void nextPlayer();

    protected abstract void endGame();

    protected abstract void startGame();
    
    
   
    
    
}
