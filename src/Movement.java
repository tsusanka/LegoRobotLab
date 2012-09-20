import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.Button;

public class Movement extends Thread
{
   private boolean press;

   public void run()
   {
      robotMenu();
   }

   private void robotMenu()
   {
      press = true;
      int buttonID;
      int costam=1;
      int menuPosition = 0;

      String[] menuStrings = new String[4];
      menuStrings[0] = "Square";
      menuStrings[1] = "Triangle";
      menuStrings[2] = "Circle";
      menuStrings[3] = "Spiral";

      LCD.drawString(">" + menuStrings[0], 1, 0);
      LCD.drawString(menuStrings[1], 2, 1);
      LCD.drawString(menuStrings[2], 2, 2);
      LCD.drawString(menuStrings[3], 2, 3);

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
            try
            {
               Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }

            new SoundPlayer().start();
            press = false;
            LCD.clear();

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
            }
         }

         if (Button.ID_ESCAPE == buttonID)
         {
            press = false;
            System.exit(0);
         }
      }
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

   }
}
