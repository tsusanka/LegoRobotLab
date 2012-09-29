import java.util.Random;
import java.util.Vector;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class Movement extends Thread {

	private int baseSpeedLeft = 70;
	private int baseSpeedRight = 70;
	private double radiusMultiplier = 1;
	private double turnMultiplier = 1;

	/**
	 * Class constructor.
	 * 
	 * @param wheelsRadius
	 *            Radius of installed wheels in centimeters. Possible choices
	 *            are: 4. TODO Wheel sizes. Add another possible sizes of the wheels.
	 */
	public Movement(int wheelsRadius) {
		if (wheelsRadius == 4) {
			this.radiusMultiplier = 11;
			this.turnMultiplier = 0.09;
		}
	}

	/**
	 * Method ran after creating this class using threading.
	 */
	public void run() {
		robotMenu();
	}

	/**
	 * Use this when robot is turning slightly right while he was supposed to go
	 * straight.
	 */
	public void increaseBaseSpeedLeft() {
		this.baseSpeedLeft++;
	}

	/**
	 * Use this when robot is turning slightly left while he was supposed to go
	 * straight.
	 */
	public void increaseBaseSpeedRight() {
		this.baseSpeedRight++;
	}

	/**
	 * Moves robot straight for the given distance.
	 * 
	 * @param centimetersDistance
	 *            Distance to cover given in centimeters
	 */
	public void driveStraight(int centimetersDistance) {
		centimetersDistance *= radiusMultiplier;

		MotorPort.B.controlMotor(baseSpeedRight, centimetersDistance > 0 ? 1
				: 2);
		MotorPort.C
				.controlMotor(baseSpeedLeft, centimetersDistance > 0 ? 1 : 2);

		while (true) {
			if (Math.abs(MotorPort.B.getTachoCount()) > Math
					.abs(centimetersDistance))
				break;
		}

		stopReset();
	}

	/**
	 * Starts both motors for an unspecified time. They need to be then stopped
	 * by stopMotors or stopReset methods.
	 * 
	 * @param powerModificator
	 *            Modifies the standard power of motors. Value under -30 wont
	 *            start the motors at all, while value over 30 gives them
	 *            maximum power.
	 * @param backwards
	 *            Value 1 if you want to run motors backwards. Has to be 0
	 *            otherwise.
	 */
	public void startMotors(int powerModificator, int backwards) {
		MotorPort.B.controlMotor(powerModificator + baseSpeedRight,
				backwards + 1);
		MotorPort.C.controlMotor(powerModificator + baseSpeedLeft,
				backwards + 1);
	}

	/**
	 * Turns robot for a given angle using both motors.
	 * 
	 * @param angle
	 *            Angle to turn the robot. Negative value turns left and
	 *            positive turns right.
	 */
	public void turn(int angle) {
		stopReset();

		MotorPort.B.controlMotor(baseSpeedRight, angle > 0 ? 2 : 1);
		MotorPort.C.controlMotor(baseSpeedLeft, angle > 0 ? 1 : 2);

		while (true) {
			if (Math.abs(MotorPort.B.getTachoCount()) > Math.abs(angle
					* radiusMultiplier * turnMultiplier))
				break;
		}

		stopReset();
	}

	/**
	 * Stops the wheel motors. Does not reset their tachometers.
	 */
	public void stopMotors() {
		MotorPort.B.controlMotor(0, 3);
		MotorPort.C.controlMotor(0, 3);
	}

	/**
	 * Resets the tachometers of wheel motors.
	 */
	private void resetTachos() {
		MotorPort.B.resetTachoCount();
		MotorPort.C.resetTachoCount();
	}

	/**
	 * Stops the wheel motors and resets their tachometers.
	 */
	public void stopReset() {
		stopMotors();
		resetTachos();
	}

	/**
	 * Shows the standard version of robot menu.
	 */
	private void robotMenu() {

		// initializations
		boolean press = true;
		int buttonID;
		int menuPosition = 0;
		String[] menuStrings = new String[6];
		menuStrings[0] = "Square";
		menuStrings[1] = "Triangle";
		menuStrings[2] = "Circle";
		menuStrings[3] = "Spiral";
		menuStrings[4] = "Custom";
		menuStrings[5] = "Random";

		// drawing menu
		LCD.drawString(">", 1, 0);
		for (int i = 0; i < menuStrings.length; i++)
			LCD.drawString(menuStrings[i], 2, i);

		// choosing loop
		while (press) {
			buttonID = Button.waitForAnyPress();

			if (Button.ID_RIGHT == buttonID) {
				LCD.drawString(" ", 1, menuPosition);
				menuPosition++;
				if (menuPosition >= menuStrings.length)
					menuPosition = 0;
				LCD.drawString(">", 1, menuPosition);
			}

			if (Button.ID_LEFT == buttonID) {
				LCD.drawString(" ", 1, menuPosition);
				menuPosition--;
				if (menuPosition < 0)
					menuPosition = menuStrings.length - 1;
				LCD.drawString(">", 1, menuPosition);
			}

			if (Button.ID_ENTER == buttonID) {

				// if a standard move scheme is chosen, wait a second to put
				// him down and play music
				if (menuPosition != 4) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					new SoundPlayer().start();
				}

				// clear the lcd
				press = false;
				LCD.clear();

				// pick proper movement type
				switch (menuPosition) {
				case 0:
					this.square(100);
					break;
				case 1:
					this.triangle(100);
					break;
				case 2:
					this.circle();
					break;
				case 3:
					this.spiral();
					break;
				case 4:
					this.customMoveMenu();
					break;
				case 5:
					this.randomMovement();
					break;
				}
			}

			if (Button.ID_ESCAPE == buttonID) {
				press = false;
				System.exit(0);
			}
		}
	}

	/**
	 * Moves the robot in a square path
	 * 
	 * @param sideLength
	 *            Side length in centimeters
	 */
	private void square(int sideLength) {
		for (int i = 0; i < 4; i++) {
			this.driveStraight(sideLength);
			this.turn(-90);
		}
		this.run();
	}

	/**
	 * Moves the robot in a triangle path
	 * 
	 * @param sideLength
	 *            Side length in centimeters
	 */
	private void triangle(int sideLength) {
		for (int i = 0; i < 3; i++) {
			this.driveStraight(sideLength);
			this.turn(-120);
		}
		this.run();
	}

	/**
	 * Moves the robot in a spiral path. TODO Spiral size. Add arguments to this
	 * method so you can pick the length/end radius of the spiral and if it
	 * should move from the inside to the outside or vice versa
	 */
	private void spiral() {
		int degree = 0;
		int i = 50;

		while (degree < 8000) {
			MotorPort.B.controlMotor(70, 1);
			MotorPort.C.controlMotor(i, 1);
			if ((degree % 1001) == 0) {
				i += 1;
				System.out.println(MotorPort.B.getTachoCount());
			}

			degree = MotorPort.B.getTachoCount();
		}

		stopReset();
		this.run();
	}

	/**
	 * Moves the robot in a circle path.
	 * 
	 * TODO Circle radius. Add argument to this method so you can pick a radius
	 * of the circle.
	 */
	private void circle() {
		int degree = 0;

		stopReset();

		while (degree < 5000) {
			MotorPort.B.controlMotor(70, 1);
			MotorPort.C.controlMotor(65, 1);
			degree = MotorPort.B.getTachoCount();
		}

		stopMotors();
		this.run();
	}

	/**
	 * Shows the menu of custom movement programming.
	 */
	private void customMoveMenu() {
		boolean addMoves = true, press = true;
		int goUnits = 5, turnAngle = 0, buttonID;
		Vector<Integer> moveVector = new Vector<Integer>();
		Vector<Integer> turnVector = new Vector<Integer>();

		// if he wants to add another move, show him a menu to do that
		while (addMoves) {
			goUnits = 5;
			turnAngle = 0;
			LCD.drawString("Go straight for: ", 0, 0);
			LCD.drawString("Angle to turn: ", 0, 3);
			LCD.drawString("Add next move? ", 0, 6);
			LCD.drawString("< " + goUnits + " >", 6, 1);
			LCD.drawString("" + turnAngle, 8, 4);
			LCD.drawString("Yes", 7, 7);

			// picking value of units to go straight
			press = true;
			while (press) {
				buttonID = Button.waitForAnyPress();

				if (Button.ID_RIGHT == buttonID) {
					goUnits++;
					LCD.drawString(goUnits + " >", 8, 1);
				}
				if (Button.ID_LEFT == buttonID) {
					goUnits--;
					LCD.drawString(goUnits + " >", 8, 1);
				}
				if (Button.ID_ENTER == buttonID) {
					LCD.clear(1);
					LCD.drawString("" + goUnits, 8, 1);
					press = false;
				}
			}
			press = true;
			LCD.drawString("< " + turnAngle + " >", 6, 4);

			// picking the degree to turn
			while (press) {
				buttonID = Button.waitForAnyPress();

				if (Button.ID_RIGHT == buttonID) {
					turnAngle++;
					LCD.drawString(turnAngle + " >", 8, 4);
				}
				if (Button.ID_LEFT == buttonID) {
					turnAngle--;
					LCD.drawString(turnAngle + " >", 8, 4);
				}
				if (Button.ID_ENTER == buttonID) {
					LCD.clear(4);
					LCD.drawString("" + turnAngle, 8, 4);
					press = false;
				}
			}
			press = true;
			LCD.drawString("< Yes >", 5, 7);

			// deciding if he wants to add another move
			while (press) {
				buttonID = Button.waitForAnyPress();

				if (Button.ID_RIGHT == buttonID || Button.ID_LEFT == buttonID) {
					addMoves = !addMoves;
					if (addMoves)
						LCD.drawString("< Yes >", 5, 7);
					else
						LCD.drawString("< No  >", 5, 7);
				}
				if (Button.ID_ENTER == buttonID) {
					LCD.clear(7);
					if (addMoves)
						LCD.drawString("Yes", 7, 7);
					else
						LCD.drawString("No", 7, 7);
					press = false;
				}
			}

			moveVector.addElement(goUnits);
			turnVector.addElement(turnAngle);
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customMove(moveVector, turnVector);
		LCD.clear();
		this.run();
	}

	/**
	 * Moves the robot in a custom, given path. The pattern is going straight
	 * for a given distance, then turning for a given angle and then again from
	 * the beginning. This path can be endless. Argument vectors should have the
	 * same size.
	 * 
	 * @param moveVector
	 *            Vector of integers. Holds the distances to go straight for.
	 * @param turnVector
	 *            Vector of integers. Holds the angles to turn around.
	 */
	private void customMove(Vector<Integer> moveVector,
			Vector<Integer> turnVector) {

		new SoundPlayer().start();
		stopReset();
		int mode = 1;

		for (int i = 0; i < moveVector.size(); i++) {
			if (moveVector.elementAt(i) > 0)
				mode = 1;
			else
				mode = 2;

			// run straight
			while (Math.abs(MotorPort.B.getTachoCount()) < Math.abs(moveVector
					.elementAt(i)) * 300) {
				MotorPort.B.controlMotor(70, mode);
				MotorPort.C.controlMotor(71, mode);
			}
			stopReset();

			// turn
			if (turnVector.elementAt(i) > 0) {
				while (Math.abs(MotorPort.B.getTachoCount()) < Math
						.abs(turnVector.elementAt(i)) * 55)
					MotorPort.B.controlMotor(70, 1);
			} else {
				while (Math.abs(MotorPort.C.getTachoCount()) < Math
						.abs(turnVector.elementAt(i)) * 55)
					MotorPort.C.controlMotor(70, 1);
			}
			stopReset();

		}

		LCD.clear();

		for (int i = 0; i < moveVector.size(); i++) {
			LCD.drawString(moveVector.elementAt(i).toString(), 1, i);
			LCD.drawString(turnVector.elementAt(i).toString(), 7, i);
		}

		Button.waitForAnyPress();

		stopReset();
		LCD.clear();
		this.run();
	}

	/**
	 * Moves the robot in a random path. It uses the distance sensor not to
	 * crash into a wall. The pattern is moving straight until almost hitting a
	 * wall, then turning random angle and then going straight again. Just like
	 * automatic/robot vacuum cleaners.
	 */
	public void randomMovement() {
		UltrasonicSensor distanceSensor = new UltrasonicSensor(SensorPort.S4);
		Random generator = new Random();
		int rand;

		while (Button.ESCAPE.isUp()) {
			this.startMotors(0, 0);

			while (true) {
				if (distanceSensor.getDistance() < 35) {
					rand = generator.nextInt() % 500 + 45;
					this.turn(rand);
					break;
				}
			}
		}
		
		this.run();
	}
}
