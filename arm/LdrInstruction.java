package arm;

import java.util.ArrayList;
import java.util.List;

public class LdrInstruction
   extends AbstractInstruction
{
   private String r1;
   private String offset;
   private List<String> targets;
   private List<String> sources;

   public LdrInstruction(String r1, String offset)
   {
      this.r1 = r1;
      this.offset = offset;
      this.targets = new ArrayList<>();
      this.sources = new ArrayList<>();

      targets.add(r1);

      if (offset.startsWith("%") || offset.startsWith("r")) {
         sources.add(offset);
      }
   }

   public String toARM()
   {
      if (offset.startsWith("r")) {
         return "ldr\t" + r1 + ", [" + offset + "]\n";
      }
      else {
         return "ldr\t" + r1 + ", [fp, " + offset + "]\n";
      }
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
      if (virtual.equals(offset)) {
         offset = real;
      }
   }
}
