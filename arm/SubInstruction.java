package arm;

import java.util.ArrayList;
import java.util.List;

public class SubInstruction
   extends AbstractInstruction
{
   private String r1;
   private String r2;
   private String operand2;
   private List<String> targets;
   private List<String> sources;

   public SubInstruction(String r1, String r2, String operand2)
   {
      this.r1 = r1;
      this.r2 = r2;
      this.operand2 = operand2;
      this.targets = new ArrayList<>();
      this.sources = new ArrayList<>();

      if (r1.startsWith("%") || r1.startsWith("r")) {
         targets.add(r1);
      }
      if (r2.startsWith("%") || r2.startsWith("r")) {
         sources.add(r2);
      }
      if (operand2.startsWith("%") || operand2.startsWith("r")) {
         sources.add(operand2);
      }
   }

   public String toARM()
   {
      return "sub\t" + r1 + ", " + r2 + ", " + operand2 + "\n";
   }

   public List<String> getTargets() {
      return targets;
   }

   public List<String> getSources() {
      return sources;
   }

   public void allocateTarget(String real)
   {
      r1 = real;
   }

   public void allocateSource(String virtual, String real)
   {
      if (virtual.equals(r2)) {
         r2 = real;
      }
      else if (virtual.equals(operand2)) {
         operand2 = real;
      }
   }
}
