package llvm;

import arm.Instruction;
import arm.LdrInstruction;
import arm.MovtInstruction;
import arm.MovwInstruction;

import java.util.LinkedHashMap;
import java.util.List;

public class LoadInstruction
   extends AbstractInstruction
{
   private final String result;
   private final String ty;
   private final String pointer;

   public LoadInstruction(String result, String ty, String pointer)
   {
      this.result = result;
      this.ty = ty;
      this.pointer = pointer;
   }

   public String toLLVM()
   {
      return "\t" + result + " = load " + ty + "* " + pointer + "\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack) {
      Integer offset;
      String id;

      id = pointer.replace("%", "");
      id = id.replace("_P_", "");

      offset = stack.get(id);

      if (pointer.startsWith("@")) {
         id = pointer.replace("@", "");
         RegisterValue t1 = new RegisterValue(null);

         arm.add(new MovwInstruction(t1.getLabel(), "#:lower16:".concat(id)));
         arm.add(new MovtInstruction(t1.getLabel(), "#:upper16:".concat(id)));
         arm.add(new LdrInstruction(result, t1.getLabel()));
      }
      else if (offset == null) {
         arm.add(new LdrInstruction(result, pointer));
      }
      else {
         arm.add(new LdrInstruction(result, "#-" + offset.toString()));
      }
   }
}
