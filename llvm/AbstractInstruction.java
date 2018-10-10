package llvm;

import java.util.LinkedHashMap;
import java.util.List;

public class AbstractInstruction
   implements Instruction
{
   public String toLLVM() {
      return null;
   }

   public void toARM(List<arm.Instruction> arm, LinkedHashMap<String, Integer> stack) {}
}
