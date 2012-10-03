import java.util.Random;
import java.util.Vector;

import lejos.nxt.Battery;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class Movement extends Thread {
	public static final boolean DEBUG_MODE = true;

	private int baseSpeedLeft = 70;
	private int baseSpeedRight = 70;
	private double wheelsRadius;
	private double wheelsSpacing;

	/**
	 * Class constructor.
	 * 
	 * @param wheelsRadius
	 *            Radius of installed wheels in centimeters.
	 * @param wheelsSpacing
	 *            Distance between installed wheels in centimeters.
	 */
	public Movement(double wheelsRadius, double wheelsSpacing) {
		this.wheelsRadius = wheelsRadius;
		this.wheelsSpacing = wheelsSpacing;
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
	public void driveStraight(double centimetersDistance) {
		centimetersDistance = (360 * centimetersDistance)
				/ (wheelsRadius * Math.PI * 2);
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
	 *            Value 1 if you want to run motors backwards. Otherwise it has
	 *            to be 0.
	 */
	public void startMotors(int powerModificator, int backwards) {
		MotorPort.B.controlMotor(powerModificator + baseSpeedRight,
				backwards + 1);
		MotorPort.C.controlMotor(powerModificator + baseSpeedLeft,
				backwards + 1);
	}

	/**
	 * Turns robot for a given angle using only one of the motors.
	 * 
	 * @param angle
	 *            Angle to turn the robot. Negative value turns left and
	 *            positive turns right.
	 */
	public void turn(int angle) {
		stopReset();

		MotorPort.B.controlMotor(baseSpeedLeft, angle < 0 ? 1 : 3);
		MotorPort.C.controlMotor(baseSpeedLeft, angle > 0 ? 1 : 3);

		while (true) {
			if (angle > 0
					&& MotorPort.C.getTachoCount() > Math.abs(angle)
							* this.wheelsSpacing / this.wheelsRadius)
				break;
			if (angle < 0
					&& MotorPort.B.getTachoCount() > Math.abs(angle)
							* this.wheelsSpacing / this.wheelsRadius)
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
		byte maxMenuItems = DEBUG_MODE ? 9 : 6;
		String[] menuStrings = new String[maxMenuItems];
		menuStrings[0] = "Square";
		menuStrings[1] = "Triangle";
		menuStrings[2] = "Circle";
		menuStrings[3] = "Spiral";
		menuStrings[4] = "Custom";
		menuStrings[5] = "Random";
		if (DEBUG_MODE) {
			menuStrings[6] = "Test Method";
			menuStrings[7] = "Check Voltage";
			menuStrings[8] = "CheckTachoCount";
		}

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
					// new SoundPlayer().start(); TODO uncomment
				}

				// clear the lcd
				press = false;
				LCD.clear();

				// pick proper movement type
				switch (menuPosition) {
				case 0:
					this.square(50);
					break;
				case 1:
					this.triangle(50);
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
				case 6:
					this.testMethod();
					break;
				case 7:
					this.checkVoltage();
					break;
				case 8:
					this.checkTachoCount();
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
			
			try {
				this.driveStraight(sideLength);
				Thread.sleep(100);
				this.turn(-90);
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
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
	 * Moves the robot in a circle path. TODO Circle radius. Add argument to
	 * this method so you can pick a radius of the circle.
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

		// new SoundPlayer().start(); TODO uncomment
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

	/**
	 * If DEBUG_MODE == true Changeable contents. Test whatever you want inside
	 */
	private void testMethod() {
		try {
			Thread.sleep(1000);
			this.turn(90);
			Thread.sleep(200);
			this.turn(-360);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		

		this.run();
	}

	/**
	 * If DEBUG_MODE == true Print the battery voltage in volts on the screen.
	 */
	private void checkVoltage() {
		String tmp = Float.toString(Battery.getVoltage());
		LCD.drawString(tmp, 0, 0);

		while (Button.ESCAPE.isUp()) {
		}
		this.run();
	}

	/**
	 * If DEBUG_MODE == true Test if TachoCounter is set up correctly. You
	 * should manually check if wheel made a full rotation
	 */
	private void checkTachoCount() {
		MotorPort.B.controlMotor(baseSpeedRight, 1);
		MotorPort.C.controlMotor(baseSpeedLeft, 1);

		while (true) {
			if (MotorPort.B.getTachoCount() >= 360)
				break;
		}

		String tmp = Integer.toString(MotorPort.B.getTachoCount());
		LCD.drawString(tmp, 0, 0);

		stopMotors();

		while (Button.ESCAPE.isUp()) {
		}
		this.run();
	}
}
