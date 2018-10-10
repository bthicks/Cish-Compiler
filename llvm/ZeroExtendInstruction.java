package llvm;

import arm.Instruction;
import arm.MovInstruction;

import java.util.LinkedHashMap;
import java.util.List;

public class ZeroExtendInstruction
   extends AbstractInstruction
{
   private final String result;
   private final String ty;
   private final String value;
   private final String  ty2;

   public ZeroExtendInstruction(String result, String ty, String value, String ty2)
   {
      this.result = result;
      this.ty = ty;
      this.value = value;
      this.ty2 = ty2;
   }

   public String toLLVM()
   {
      return "\t" + result + " = zext " + ty + " " + value + " to i32\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack) {
      arm.add(new MovInstruction(result, value));
   }
}
