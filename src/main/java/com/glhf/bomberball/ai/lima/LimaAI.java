package com.glhf.bomberball.ai.lima;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LimaAI extends AbstractAI{

    private Noeud noeud;
    private Player ia;
    private boolean calculDone;

    public LimaAI(GameConfig config, String player_skin, int playerId) {
        super(config,"skelet","LimaAI",playerId);
        ia=this;
    }

    @Override
    public Action choosedAction(GameState gameState) {
        memorizedAction = Action.ENDTURN; //safety
        System.out.println("debut LimaAI");
        System.out.println(Arrays.toString(gameState.getAllPossibleActions().toArray()));
        if(noeud==null || noeud.actionsAFaire==null || noeud.actionsAFaire.size()==0) {
            noeud = new NoeudMax(gameState, null);
            noeud.a = Float.NEGATIVE_INFINITY;
            noeud.b = Float.POSITIVE_INFINITY;
            float alpha = algoAlphaBeta(noeud, 1); //examine seulement ses actions prendrait trop de temps d'aller plus loin
            noeud = noeud.meilleurEnfant();
        }
        System.out.println(actionsToString(noeud.actionsAFaire));
        System.out.println("fin LimaAI  score : "+ noeud.a);
        return noeud.getAction();
    }
    private float algoAlphaBeta(Noeud noeud, int h) {
        if (noeud.estTerminal() || h == 0) {
            float utilite = noeud.utilite();
            if(noeud instanceof NoeudMax)
                noeud.a=utilite;
            else
                noeud.b=utilite;
//            if(noeud.estTerminal())
//            System.out.println(noeud.etat.getCurrentPlayerId()+") return "+ utilite + "\t"+noeud.debugTree);
            return utilite;
        }
        noeud.creerEnfants();
        if (noeud instanceof NoeudMax) {
            List<Noeud> enfants = noeud.enfants;
            for (int i = 0; i < enfants.size(); i++) {
                Noeud n = enfants.get(i);
                n.a = noeud.a;
                n.b = noeud.b;
//                if (noeud == noeud)
//                    System.out.println(n.etat.getPlateau() + " a:" + n.a + " b:" + n.b);
                noeud.a = Math.max(noeud.a, algoAlphaBeta(n, h-1));
                if (noeud.a >= noeud.b) {
//                    System.out.println("max-- "+noeud.a+">="+noeud.b);
                    break; // elagage
                }
//                else System.out.println("max "+noeud.a+"<"+noeud.b);
            }
//            System.out.println("fin noeud max");
            return noeud.a;
        } else {//NoeudMin
            List<Noeud> enfants = noeud.enfants;
            for (int i = 0; i < enfants.size(); i++) {
                Noeud n = enfants.get(i);
                n.a = noeud.a;
                n.b = noeud.b;
                noeud.b = Math.min(noeud.b, algoAlphaBeta(n, h-1));
                if (noeud.a >= noeud.b) {
//                    System.out.println("min-- "+noeud.a+">="+noeud.b);
                    break; // elagage
                }
//                else System.out.println("min "+noeud.a+"<"+noeud.b);
            }
//            System.out.println("fin noeud min");
            return noeud.b;
        }
    }

    public static String actionsToString(List<Action> actions) {
        String out="-";
        for (Action action : actions) {
            out+="-";
            for (String s : action.toString().split("_")) {
                out+=""+s.charAt(0);
            }
        }
        return out;
    }

    private abstract class Noeud {
        protected GameState etat;
        private List<Noeud> enfants;
        private List<Action> actionsAFaire;
        float a, b;
        String debugTree="o";

        Noeud(GameState etat, List<Action> actions) {
            this.etat = etat; //etat avec l'action faite
            this.actionsAFaire = actions;
            enfants = new ArrayList<>();
        }

        Noeud meilleurEnfant() {
            Noeud meilleur = enfants.get(0);
            for (Noeud n : enfants) {
                    if(n instanceof NoeudMin){
                    if(n.b>meilleur.b){
                        meilleur=n;
                    }
                }else{
                    if(n.a>meilleur.a){
                        meilleur=n;
                    }
                }
            }
            return meilleur;
        }

        boolean estTerminal() {
            return etat.gameIsOver();
        }

        float utilite() {
            Player winner = etat.getWinner();
            if(etat.gameIsOver()){
//                etat.printCells();
                if(winner == null) {//draw
                    return 0;
                }
                if (ia.getPlayerId() == winner.getPlayerId()) {
                    return Float.POSITIVE_INFINITY;
                } else {
                    return Float.NEGATIVE_INFINITY;
                }
            }
            return Heuristique.calculate(etat, ia.getPlayerId());
        }

        void creerEnfants() {
            creerEnfantsAux(etat, new ArrayList<>());
        }

        private void creerEnfantsAux(GameState etat, List<Action> actionsToDo){
//            if(actionsAFaire==Action.ENDTURN) return;
            if(enfants.size()>10000){ //limit the number of children
                System.err.println("too much possibilities");
                return;
            }
            List<Action> actions = etat.getAllPossibleActions();
            Collections.shuffle(actions);
            for (Action a : actions) {
//                if(a==Action.ENDTURN)continue;
                GameState etatClone = etat.clone();
                List<Action> actionsToDoTmp = new ArrayList<>(actionsToDo);
                etatClone.apply(a);
                actionsToDoTmp.add(a);
                if(etatClone.gameIsOver() || etat.getCurrentPlayerId()!=etatClone.getCurrentPlayerId()){
                    Noeud e = this.creerEnfant(etatClone, actionsToDoTmp);
                    enfants.add(e);
                    e.debugTree=debugTree + actionsToString(actionsToDoTmp);
                }else{
                    if(!etatClone.gameIsOver())
                        creerEnfantsAux(etatClone, actionsToDoTmp);
                }
            }
        }


        abstract Noeud creerEnfant(GameState gm, List<Action> actions);

        public Action getAction() {
            return actionsAFaire.remove(0);
        }

        @Override
        public String toString() {
            return "{a:"+a+", b:"+b+", tree:"+debugTree+"}";
        }
    }

    private class NoeudMax extends Noeud {
        NoeudMax(GameState etat, List<Action> actions) {
            super(etat, actions);
        }

        @Override
        Noeud creerEnfant(GameState gm, List<Action> actions) {
            return new NoeudMin(gm, actions);
        }
    }

    private class NoeudMin extends Noeud {
        NoeudMin(GameState etat, List<Action> actions) {
            super(etat, actions);
        }

        @Override
        Noeud creerEnfant(GameState gm, List<Action> actions) {
            return new NoeudMax(gm, actions);
        }
    }
}