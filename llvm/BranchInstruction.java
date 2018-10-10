package llvm;

import arm.Instruction;

import java.util.LinkedHashMap;
import java.util.List;

public class BranchInstruction
   extends AbstractInstruction
{
   private final String dest;

   public BranchInstruction(String dest)
   {
      this.dest = dest;
   }

   public String toLLVM()
   {
      return "\tbr label %" + dest + "\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack)
   {
      arm.add(new arm.BInstruction(dest));
   }
}
