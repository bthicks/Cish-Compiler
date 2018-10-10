package arm;

public class BeqInstruction
   extends AbstractInstruction
{
   private final String label;

   public BeqInstruction(String label)
   {
      this.label = label;
   }

   public String toARM()
   {
      return "beq\t." + label + "\n";
   }
}
