package arm;

public class BInstruction
   extends AbstractInstruction
{
   private final String label;

   public BInstruction(String label)
   {
      this.label = label;
   }

   public String toARM()
   {
      return "b\t." + label + "\n";
   }
}
