package arm;

public class BltInstruction
        extends AbstractInstruction
{
   private final String label;

   public BltInstruction(String label)
   {
      this.label = label;
   }

   public String toARM()
   {
      return "blt\t" + label + "\n";
   }
}
