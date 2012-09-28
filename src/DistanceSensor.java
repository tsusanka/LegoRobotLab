import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class DistanceSensor extends Thread {

	public void run() {
		UltrasonicSensor distanceSensor = new UltrasonicSensor(SensorPort.S4);
		Movement controlEngines = new Movement(4);
		Random generator = new Random();
		int rand;

		Button.waitForAnyPress();

		while (Button.ESCAPE.isUp()) {
			controlEngines.startMotors(0, 0);

			while (true) {
				if (distanceSensor.getDistance() < 35) {
					rand = generator.nextInt() % 500 + 45;
					controlEngines.turn(rand);
					break;
				}
			}
		}

	}

}
