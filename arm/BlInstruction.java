package arm;

import java.util.ArrayList;
import java.util.List;

public class BlInstruction
   extends AbstractInstruction
{
   private final String label;
   private List<String> targets;
   private List<String> sources;

   public BlInstruction(String label)
   {
      this.label = label;
      this.sources = new ArrayList<>();
      this.targets = new ArrayList<>();

      sources.add("r0");
      sources.add("r1");
      sources.add("r2");
      sources.add("r3");
      targets.add("r0");
      targets.add("r1");
      targets.add("r2");
      targets.add("r3");
   }

   public String toARM()
   {
      return "bl\t" + label + "\n";
   }

   public List<String> getTargets()
   {
      return targets;
   }

   public List<String> getSources()
   {
      return sources;
   }
}
