/**
 * SportsEngin.java[v 1.0.0]
 * classes:com.jht.tapc.platform.media.SportsEngin
 * fch Create of at 2015�?3�?26�? 下午2:09:15
 */
package com.tapc.platform.sportsrunctrl;

import com.tapc.platform.entity.FitnessSetAllEntity;
import com.tapc.platform.media.PlayEntity;
import com.tapc.platform.workout.WorkoutListener;

public interface SportsEngin {
	void start();
	void stop();
	void pause();
	void restart();

	void openScene(PlayEntity entity);
	PlayEntity getScene();
	
	void openFitness(FitnessSetAllEntity entity);
	FitnessSetAllEntity getFitness();
	void setWorkoutListener(WorkoutListener listener);

	double addSpeed();
	double subSpeed();
	double setSpeed(double speed);

	double addIncline();
	double subIncline();
	double setIncline(double inlcine);

	boolean isRunning();
	boolean isPause();
	void setPause(boolean flag);
	
	void setCooldown();
}
