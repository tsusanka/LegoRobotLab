import java.util.Vector;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.Button;

public class Movement extends Thread
{

   public void run()
   {
      robotMenu();
   }

   private void stopMotors()
   {
      MotorPort.B.controlMotor(0, 3);
      MotorPort.C.controlMotor(0, 3);
   }

   private void resetTachos()
   {
      MotorPort.B.resetTachoCount();
      MotorPort.C.resetTachoCount();
   }

   private void stopReset()
   {
      stopMotors();
      resetTachos();
   }

   private void robotMenu()
   {

      // initializations
      boolean press = true;
      int buttonID;
      int menuPosition = 0;
      String[] menuStrings = new String[5];
      menuStrings[0] = "Square";
      menuStrings[1] = "Triangle";
      menuStrings[2] = "Circle";
      menuStrings[3] = "Spiral";
      menuStrings[4] = "Custom";

      // drawing menu
      LCD.drawString(">", 1, 0);
      for (int i = 0; i < menuStrings.length; i++)
         LCD.drawString(menuStrings[i], 2, i);

      // choosing loop
      while (press)
      {
         buttonID = Button.waitForAnyPress();

         if (Button.ID_RIGHT == buttonID)
         {
            LCD.drawString(" ", 1, menuPosition);
            menuPosition++;
            if (menuPosition >= menuStrings.length)
               menuPosition = 0;
            LCD.drawString(">", 1, menuPosition);
         }

         if (Button.ID_LEFT == buttonID)
         {
            LCD.drawString(" ", 1, menuPosition);
            menuPosition--;
            if (menuPosition < 0)
               menuPosition = menuStrings.length - 1;
            LCD.drawString(">", 1, menuPosition);
         }

         if (Button.ID_ENTER == buttonID)
         {

            // if a standard move scheme is choosen, wait a second to put him
            // down and play music
            if (menuPosition < 4)
            {
               try
               {
                  Thread.sleep(1000);
               }
               catch (InterruptedException e)
               {
                  e.printStackTrace();
               }
               new SoundPlayer().start();
            }

            // clear lcd
            press = false;
            LCD.clear();

            // pick proper movement type
            switch (menuPosition)
            {
               case 0:
                  square();
                  break;
               case 1:
                  triangle();
                  break;
               case 2:
                  circle();
                  break;
               case 3:
                  spiral();
                  break;
               case 4:
                  customMoveMenu();
                  break;
            }
         }

         if (Button.ID_ESCAPE == buttonID)
         {
            press = false;
            System.exit(0);
         }
      }
   }

   private void square()
   {
      int degree;
      for (int i = 0; i < 4; i++)
      {
         degree = 0;
         stopReset();

         while (degree < 720 && Button.RIGHT.isUp())
         {
            MotorPort.B.controlMotor(71, 1);
            MotorPort.C.controlMotor(70, 1);

            degree = MotorPort.B.getTachoCount();
         }

         degree = 0;
         MotorPort.B.resetTachoCount();
         MotorPort.C.controlMotor(0, 3);

         while (degree < 210 && Button.RIGHT.isUp())
         {
            MotorPort.B.controlMotor(70, 1);
            degree = MotorPort.B.getTachoCount();
         }
      }
      stopMotors();
      robotMenu();
   }

   private void triangle()
   {
      int degree;
      for (int i = 0; i < 3; i++)
      {
         degree = 0;
         stopReset();

         while (degree < 720 && Button.RIGHT.isUp())
         {
            MotorPort.B.controlMotor(71, 1);
            MotorPort.C.controlMotor(70, 1);

            degree = MotorPort.B.getTachoCount();
         }

         degree = 0;
         MotorPort.B.resetTachoCount();
         MotorPort.C.controlMotor(0, 3);

         while (degree < 280 && Button.RIGHT.isUp())
         {
            MotorPort.B.controlMotor(70, 1);
            degree = MotorPort.B.getTachoCount();
         }
      }

      stopMotors();
      robotMenu();
   }

   private void spiral()
   {
      int degree = 0;
      int i = 50;

      stopReset();

      while (degree < 8000)
      {
         MotorPort.B.controlMotor(70, 1);
         MotorPort.C.controlMotor(i, 1);
         if ((degree % 1001) == 0)
         {
            i += 1;
            System.out.println(MotorPort.B.getTachoCount());
         }

         degree = MotorPort.B.getTachoCount();
      }

      stopMotors();
      robotMenu();
   }

   private void circle()
   {
      int degree = 0;

      stopReset();

      while (degree < 5000)
      {
         MotorPort.B.controlMotor(70, 1);
         MotorPort.C.controlMotor(65, 1);
         degree = MotorPort.B.getTachoCount();
      }

      stopMotors();
      robotMenu();
   }

   private void customMoveMenu()
   {
      boolean addMoves = true, press = true;
      int goUnits = 5, turnAngle = 0, buttonID;
      Vector<Integer> moveVector = new Vector<Integer>();
      Vector<Integer> turnVector = new Vector<Integer>();

      // if he wants to add another move, show him a menu to do that
      while (addMoves)
      {
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
         while (press)
         {
            buttonID = Button.waitForAnyPress();

            if (Button.ID_RIGHT == buttonID)
            {
               goUnits++;
               LCD.drawString(goUnits + " >", 8, 1);
            }
            if (Button.ID_LEFT == buttonID)
            {
               goUnits--;
               LCD.drawString(goUnits + " >", 8, 1);
            }
            if (Button.ID_ENTER == buttonID)
            {
               LCD.clear(1);
               LCD.drawString("" + goUnits, 8, 1);
               press = false;
            }
         }
         press = true;
         LCD.drawString("< " + turnAngle + " >", 6, 4);

         // picking the degree to turn
         while (press)
         {
            buttonID = Button.waitForAnyPress();

            if (Button.ID_RIGHT == buttonID)
            {
               turnAngle++;
               LCD.drawString(turnAngle + " >", 8, 4);
            }
            if (Button.ID_LEFT == buttonID)
            {
               turnAngle--;
               LCD.drawString(turnAngle + " >", 8, 4);
            }
            if (Button.ID_ENTER == buttonID)
            {
               LCD.clear(4);
               LCD.drawString("" + turnAngle, 8, 4);
               press = false;
            }
         }
         press = true;
         LCD.drawString("< Yes >", 5, 7);

         // deciding if he wants to add another move
         while (press)
         {
            buttonID = Button.waitForAnyPress();

            if (Button.ID_RIGHT == buttonID || Button.ID_LEFT == buttonID)
            {
               addMoves = !addMoves;
               if (addMoves)
                  LCD.drawString("< Yes >", 5, 7);
               else
                  LCD.drawString("< No  >", 5, 7);
            }
            if (Button.ID_ENTER == buttonID)
            {
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

      try
      {
         Thread.sleep(2000);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
      customMove(moveVector, turnVector);
      LCD.clear();
      robotMenu();
   }

   private void customMove(Vector<Integer> moveVector,
         Vector<Integer> turnVector)
   {

      new SoundPlayer().start();
      stopReset();
      int mode = 1;

      for (int i = 0; i < moveVector.size(); i++)
      {
         if (moveVector.elementAt(i) > 0)
            mode = 1;
         else
            mode = 2;

         // run straight
         while (Math.abs(MotorPort.B.getTachoCount()) < Math.abs(moveVector
               .elementAt(i)) * 300)
         {
            MotorPort.B.controlMotor(70, mode);
            MotorPort.C.controlMotor(71, mode);
         }
         stopReset();

         // turn
         if (turnVector.elementAt(i) > 0)
         {
            while (Math.abs(MotorPort.B.getTachoCount()) < Math.abs(turnVector
                  .elementAt(i)) * 55)
               MotorPort.B.controlMotor(70, 1);
         }
         else
         {
            while (Math.abs(MotorPort.C.getTachoCount()) < Math.abs(turnVector
                  .elementAt(i)) * 55)
               MotorPort.C.controlMotor(70, 1);
         }
         stopReset();

      }

      LCD.clear();

      for (int i = 0; i < moveVector.size(); i++)
      {
         LCD.drawString(moveVector.elementAt(i).toString(), 1, i);
         LCD.drawString(turnVector.elementAt(i).toString(), 7, i);

      }

      Button.waitForAnyPress();

      stopReset();
      LCD.clear();
      robotMenu();
   }
}
