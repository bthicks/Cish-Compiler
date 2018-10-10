package arm;

import java.util.ArrayList;
import java.util.List;

public class MulInstruction
   extends AbstractInstruction
{
   private String r1;
   private String r2;
   private String r3;
   private List<String> targets;
   private List<String> sources;

   public MulInstruction(String r1, String r2, String r3)
   {
      this.r1 = r1;
      this.r2 = r2;
      this.r3 = r3;
      this.targets = new ArrayList<>();
      this.sources = new ArrayList<>();

      targets.add(r1);
      sources.add(r2);
      sources.add(r3);
   }

   public String toARM()
   {
      return "mul\t" + r1 + ", " + r2 + ", " + r3 + "\n";
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
      else if (virtual.equals(r3)) {
         r3 = real;
      }
   }
}
