package arm;

public class BneInstruction
   extends AbstractInstruction
{
   private final String label;

   public BneInstruction(String label)
   {
      this.label = label;
   }

   public String toARM()
   {
      return "bne\t" + label + "\n";
   }
}
