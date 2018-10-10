package llvm;

import arm.*;
import arm.Instruction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CallInstruction
   extends AbstractInstruction
{
   private final String result;
   private final String ty;
   private final String fnptrval;
   private final String args;
   private final List<String> argValues;

   public CallInstruction(String result, String ty, String fnptrval, String args, List<String> argValues)
   {
      this.result = result;
      this.ty = ty;
      this.fnptrval = fnptrval;
      this.args = args;
      this.argValues = argValues;
   }

   public String toLLVM()
   {
      if (result.equals("void")) {
         return "\tcall " + ty + " " + fnptrval + "(" + args + ")\n";
      }
      else {
         return "\t" + result + " = call " + ty + " " + fnptrval + "(" + args + ")\n";
      }
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack) {
      int r = 0;

      for (String argValue : argValues) {
         if (!argValue.startsWith("%") && !argValue.startsWith("#")) {
            if (argValue.equals("null")) {
               arm.add(new MovInstruction("r" + Integer.toString(r++), "#0"));
            }
            else {
               arm.add(new MovInstruction("r" + Integer.toString(r++), "#".concat(argValue)));
            }
         }
         else {
            arm.add(new MovInstruction("r" + Integer.toString(r++), argValue));
         }
      }

      arm.add(new BlInstruction(fnptrval.replace("@", "")));

      if (!result.equals("void")) {
         arm.add(new MovInstruction(result, "r0"));
      }
   }
}
