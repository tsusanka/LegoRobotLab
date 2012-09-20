import lejos.nxt.Button;

public class MainClass
{
   public static void main(String[] args) throws InterruptedException
   {      
      //Starting threads
      new Movement().start();
   }
}
