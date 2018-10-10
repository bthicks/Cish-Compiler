package llvm;

import arm.Instruction;
import arm.MovInstruction;

import java.util.LinkedHashMap;
import java.util.List;

public class ReturnInstruction
   extends AbstractInstruction
{
   private final String ty;
   private final String value;

   public ReturnInstruction(String ty, String value)
   {
      this.ty = ty;
      this.value = value;
   }

   public String toLLVM()
   {
      return "\tret " + ty + " " + value + "\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack) {
      arm.add(new MovInstruction("r0", value));
   }
}
