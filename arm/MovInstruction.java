package arm;

import java.util.ArrayList;
import java.util.List;

public class MovInstruction
   extends AbstractInstruction
{
   private String r1;
   private String operand2;

   public MovInstruction(String r1, String operand2)
   {
      this.r1 = r1;
      this.operand2 = operand2;
   }

   public String toARM()
   {
      return "mov\t" + r1 + ", " + operand2 + "\n";
   }

   public List<String> getTargets() {
      List<String> targets = new ArrayList<>();
      targets.add(r1);

      return targets;
   }

   public List<String> getSources() {
      List<String> sources = new ArrayList<>();

      if (operand2.startsWith("%") || operand2.startsWith("r")) {
         sources.add(operand2);
      }

      return sources;
   }

   public void allocateTarget(String real)
   {
      r1 = real;
   }

   public void allocateSource(String virtual, String real)
   {
      if (virtual.equals(operand2)) {
         operand2 = real;
      }
   }
}
