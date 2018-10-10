package llvm;

import arm.Instruction;

import java.util.LinkedHashMap;
import java.util.List;

public class AndInstruction
   extends AbstractInstruction
{
   private final String result;
   private final String name;
   private final String ty;
   private final String op1;
   private final String op2;

   public AndInstruction(String result, String ty, String op1, String op2)
   {
      this.result = result;
      this.name = "and";
      this.ty = ty;
      this.op1 = op1;
      this.op2 = op2;
   }

   public String toLLVM()
   {
      return "\t" + result + " = and " + ty + " " + op1 + ", " + op2 + "\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack) {
      arm.add(new arm.AndInstruction(result, op1, op2));
   }
}
