/**
 * Classe appellée lorqu'une fin de partie est détectée dans l'algorithme alpha-beta et servant à stocker les différents paramètres de l'algorithme
 */
package com.glhf.bomberball.ai.novembre;

import com.glhf.bomberball.utils.Action;

public class checkGameResultReturnObject {
	MyArrayList<Action> actions;
	double alpha;
	double beta;
	String message;

	public checkGameResultReturnObject(MyArrayList<Action> actions,double alpha,double beta,String message){
		this.actions=actions;
		this.alpha=alpha;
		this.beta=beta;
		this.message=message;
	}
}
