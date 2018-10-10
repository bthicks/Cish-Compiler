package llvm;

import arm.*;

import java.util.LinkedHashMap;
import java.util.List;

public class ScanfInstruction
   extends AbstractInstruction
{
   private final String arg;

   public ScanfInstruction(String arg)
   {
      this.arg = arg;
   }

   public String toLLVM()
   {
      return "\tcall i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([3 x i8]* @.read, i32 0, i32 0), i32* " + arg +")\n";
   }

   public void toARM(List<arm.Instruction> arm, LinkedHashMap<String, Integer> stack) {
      Integer offset;
      String id;


      id = arg.replace("%", "");
      id = id.replace("_P_", "");
      id = id.replace("@", "");

      offset = stack.get(id);

      arm.add(new MovwInstruction("r1", "#:lower16:.read_scratch"));
      arm.add(new MovtInstruction("r1", "#:upper16:.read_scratch"));
      arm.add(new MovwInstruction("r0", "#:lower16:.READ_FMT"));
      arm.add(new MovtInstruction("r0", "#:upper16:.READ_FMT"));
      arm.add(new BlInstruction("scanf"));
      arm.add(new MovwInstruction("r0", "#:lower16:.read_scratch"));
      arm.add(new MovtInstruction("r0", "#:upper16:.read_scratch"));
      arm.add(new LdrInstruction("r0", "r0"));

      if (offset == null) {
         if (arg.startsWith("@")) {
            id = arg.replace("@", "");
            RegisterValue t1 = new RegisterValue(null);

            arm.add(new MovwInstruction(t1.getLabel(), "#:lower16:".concat(id)));
            arm.add(new MovtInstruction(t1.getLabel(), "#:upper16:".concat(id)));
            arm.add(new StrInstruction("r0", t1.getLabel()));
         }
         else {
            arm.add(new StrInstruction("r0", arg));
         }
      }
      else {
         arm.add(new StrInstruction("r0", "#-" + offset.toString()));
      }
   }
}
