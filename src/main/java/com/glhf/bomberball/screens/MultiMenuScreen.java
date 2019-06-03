package com.glhf.bomberball.screens;

import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.ui.MultiMenuUI;

/**
 * @author Jyra
 * Class that contain the intelligence of the Multi Menu.
 * Extends AbstractScreen
 */
public class MultiMenuScreen extends MenuScreen {

    public Maze maze;
    public static String[] playable = {"knight_m", "knight_f", "black_knight", "elf_f", "elf_m", "wizzard_m", "wizzard_f", "no_player"};
    public static final int nb_Playable = playable.length;
    private int maze_id = 4;
    private int maze_count;

    public static int p1_id=0;
    public static int p2_id=1;
   // public static int p3_id=nb_Playable-1;
   // public static int p4_id=nb_Playable-1;

    public MultiMenuScreen() {
        maze_count = Maze.countMazesMulti();
        System.out.println("MC " + maze_count);
        maze = Maze.importMazeMulti("maze_" + maze_id);
       // this.addUI(new MultiMenuUI(this));
    }

    public void nextMaze() {
        maze_id = (maze_id + 1) % maze_count;
        maze = Maze.importMazeMulti("maze_" + maze_id);
        System.out.println("New maze = "+ maze_id);
    }

    public void previousMaze() {
        maze_id = (maze_id + maze_count - 1) % maze_count;
        maze = Maze.importMazeMulti("maze_" + maze_id);
        System.out.println("New maze = "+ maze_id);
    }
    public void randomMaze()
    {
        maze_id = (int)(Math.random() * maze_count);
        System.out.println("Selecting a random Maze, new Maze number = " + maze_id);
        maze = Maze.importMazeMulti("maze_" + maze_id);
    }
    public void nextP1(){
        do {
            p1_id++;
            if (p1_id==nb_Playable-1) { p1_id=0;}
        } while(p1_id==p2_id);// || p1_id==p3_id || p1_id==p4_id);
        System.out.println("New skin for p1 is : "+ playable[p1_id]);
    }
    public void nextP2(){
        do {
            p2_id++;
            if (p2_id==nb_Playable-1) { p2_id=0;}
        } while (p2_id==p1_id);// || p2_id==p3_id || p2_id==p4_id);
        System.out.println("New skin for p2 is : "+ playable[p2_id]);
    }

    /**
     * This function change the id of the player 3.
     * Here I use a flag because p3 and p4 can have the same id 'no_player'
     */
//    public void nextP3(){
//        boolean flag=true;
//        do {
//            p3_id++;
//            if (p3_id==nb_Playable) { p3_id=0;}
//            if(p3_id==nb_Playable-1) {flag=false;}
//            else if (p3_id!=p2_id && p1_id!=p3_id && p3_id!=p4_id) {
//                flag=false;
//            }
//        } while (flag) ;
//        System.out.println("New skin for p3 is : "+ playable[p3_id]);
//
//    }
//    public void nextP4(){
//        boolean flag=true;
//        do {
//            p4_id++;
//            if (p4_id==nb_Playable) { p4_id=0;}
//            if(p4_id==nb_Playable-1) {
//                flag=false;}
//            else if (p4_id!=p2_id && p1_id!=p4_id && p3_id!=p4_id) {
//                flag=false;
//            }
//        } while (flag) ;
//        System.out.println("New skin for p4 is : "+ playable[p4_id]);
//
//    }

    public void saveToConfig(){
        GameMultiConfig config = GameMultiConfig.get();
        System.out.println("Loading config"+"" +
                "\nPlayer 1 is: " + MultiMenuScreen.playable[MultiMenuScreen.p1_id]
                +"\nPlayer 2 is:" + MultiMenuScreen.playable[MultiMenuScreen.p2_id]);
           //     +"\nPlayer 3 is:" + MultiMenuScreen.playable[MultiMenuScreen.p3_id]
            //    +"\nPlayer 4 is:" + MultiMenuScreen.playable[MultiMenuScreen.p4_id]);
        // Initialization of the number of players
        config.player_count= 2;
        //if (MultiMenuScreen.p3_id==MultiMenuScreen.nb_Playable-1){config.player_count--;}
        //if (MultiMenuScreen.p4_id==MultiMenuScreen.nb_Playable-1){config.player_count--;}
        System.out.println("Number of players: "+ config.player_count);

        // Loading the players skins
        config.player_skins = new  String[config.player_count];
        int i=0;    //using a counter because the player 4 might be in position 3 for example if the player 3 isn't playing, can't make a loop because p1_id!=pi_id weird code but working
        config.player_skins[i]= MultiMenuScreen.playable[MultiMenuScreen.p1_id]; i++;
        config.player_skins[i]= MultiMenuScreen.playable[MultiMenuScreen.p2_id]; i++;
//        if (MultiMenuScreen.p3_id!=MultiMenuScreen.nb_Playable-1)
//        {
//            config.player_skins[i]= MultiMenuScreen.playable[MultiMenuScreen.p3_id]; i++;
//        }
//        if (MultiMenuScreen.p4_id!=MultiMenuScreen.nb_Playable-1)
//        {
//            config.player_skins[i]= MultiMenuScreen.playable[MultiMenuScreen.p4_id]; i++;
//        }
        System.out.println("End of config loading");
    }

    public int getMazeId() {
        return maze_id;
    }
}
