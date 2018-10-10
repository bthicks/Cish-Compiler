package arm;

import java.util.ArrayList;
import java.util.List;

public class AbstractInstruction
   implements Instruction
{
   public String toARM() {
      return null;
   }

   public List<String> getTargets() {
      return new ArrayList<>();
   }

   public List<String> getSources() {
      return new ArrayList<>();
   }

   public void allocateTarget(String real) {}

   public void allocateSource(String virtual, String real) {}
}
