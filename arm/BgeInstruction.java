package arm;

public class BgeInstruction
        extends AbstractInstruction
{
   private final String label;

   public BgeInstruction(String label)
   {
      this.label = label;
   }

   public String toARM()
   {
      return "bge\t" + label + "\n";
   }
}
