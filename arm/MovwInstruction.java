package arm;

import java.util.ArrayList;
import java.util.List;

public class MovwInstruction
   extends AbstractInstruction
{
   private String r1;
   private String imm16;
   private List<String> targets;

   public MovwInstruction(String r1, String imm16)
   {
      this.r1 = r1;
      this.imm16 = imm16;
      this.targets = new ArrayList<>();

      targets.add(r1);
   }

   public String toARM()
   {
      return "movw\t" + r1 + ", " + imm16 + "\n";
   }

   public List<String> getTargets() {
      return targets;
   }

   public void allocateTarget(String real)
   {
      r1 = real;
   }
}
