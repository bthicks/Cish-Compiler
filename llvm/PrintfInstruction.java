package llvm;

import arm.*;

import java.util.LinkedHashMap;
import java.util.List;

public class PrintfInstruction
   extends AbstractInstruction
{
   private final Boolean println;
   private final String arg;

   public PrintfInstruction(Boolean println, String arg)
   {
      this.println = println;
      this.arg = arg;
   }

   public String toLLVM()
   {
      if (println) {
         return "\tcall i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([4 x i8]* @.println, i32 0, i32 0), i32 " + arg + ")\n";
      }
      else {
         return "\tcall i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([4 x i8]* @.print, i32 0, i32 0), i32 " + arg + ")\n";
      }
   }

   public void toARM(List<arm.Instruction> arm, LinkedHashMap<String, Integer> stack) {
      if (!arg.startsWith("#") && !arg.startsWith("%")) {
         arm.add(new MovInstruction("r1", "#".concat(arg)));
      }
      else {
         arm.add(new MovInstruction("r1", arg));
      }
      arm.add(new MovwInstruction("r0", "#:lower16:.PRINTLN_FMT"));
      arm.add(new MovtInstruction("r0", "#:upper16:.PRINTLN_FMT"));
      arm.add(new BlInstruction("printf"));
   }
}
