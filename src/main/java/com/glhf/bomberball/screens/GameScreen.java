package com.glhf.bomberball.screens;




import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.glhf.bomberball.utils.Action;

import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.MazeDrawer;
import com.glhf.bomberball.maze.MazeTransversal;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;
import com.glhf.bomberball.utils.VectorInt2;


import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class GameScreen extends AbstractScreen {
    protected Timer.Task task;
    protected Maze maze;
    protected MazeDrawer maze_drawer;

    protected Player current_player;
    protected ArrayList<Cell> selected_cells = new ArrayList<>();
 
    
	
    public GameScreen(Maze maze) {
        super();
//        maze = new Maze(11, 13);
//        maze.exportConfig(filename);
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
        
        task = new Timer.Task() {
            @Override
            public void run() {
            	
            	System.out.println(current_player);
                nextPlayer();
            }
        };
        Timer.schedule(task, 0.5f);
    }
    
	private void forceThreadStop() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Pattern p = Pattern.compile("^pool-[0-9]+-thread-[0-9]+$");
		for (Thread t : threadSet) {
			if (p.matcher(t.getName()).matches()) {
				t.stop();
			}
		}
	}
    

    protected abstract void nextPlayer();

    protected abstract void endGame();

    protected abstract void startGame();
    
    
   
    
    
}
