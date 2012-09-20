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
      System.out.println("LEFT for SQAURE");
      System.out.println("ENTER for TRIANGLE");
      System.out.println("RIGHT for SPIRAL");
      System.out.println("ESCAPE to LEAVE");
      
      press = true;
      while (press)
      {
         if (Button.ENTER.isDown())
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
            triangle();
            press = false;
         }
         
         if (Button.LEFT.isDown())
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
            square();
            press = false;
         }
         
         if (Button.RIGHT.isDown())
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
            spiral();
            press = false;
         }
         
         if (Button.ESCAPE.isDown())
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
      System.out.println("    SQUARE");
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
      System.out.println("    TRIANGLE");
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
      System.out.println("    SPIRAL");
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
      // thju e threbe
   }
}
