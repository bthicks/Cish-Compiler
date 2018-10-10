package llvm;

import arm.Instruction;

import java.util.LinkedHashMap;
import java.util.List;

public class GetElementPtrInstruction
   extends AbstractInstruction
{
   private final String result;
   private final String ty;
   private final String ptrval;
   private final String index;

   public GetElementPtrInstruction(String result, String ty, String ptrval, String index)
   {
      this.result = result;
      this.ty = ty;
      this.ptrval = ptrval;
      this.index = index;
   }

   public String toLLVM()
   {
      return "\t" + result + " = getelementptr " + ty + "* " + ptrval + ", i1 0, i32 " + index + "\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack)
   {
      int i = Integer.parseInt(index);
      i = i * 4;

      arm.add(new arm.AddInstruction(result, ptrval, "#" + Integer.toString(i)));
   }
}
