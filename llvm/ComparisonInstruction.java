package llvm;

import arm.*;
import arm.Instruction;

import java.util.LinkedHashMap;
import java.util.List;

public class ComparisonInstruction
   extends AbstractInstruction
{
   private final String result;
   private final String cond;
   private final String ty;
   private final String op1;
   private final String op2;

   public ComparisonInstruction(String result, String cond, String ty, String op1, String op2)
   {
      this.result = result;
      this.cond = cond;
      this.ty = ty;
      this.op1 = op1;
      this.op2 = op2;
   }

   public String toLLVM()
   {
      return "\t" + result + " = icmp " + cond + " " + ty + " " + op1 + ", " + op2 + "\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack) {
      String r1 = op1;
      String r2 = op2;

      if (!r1.contains("%")) {
         r1 = "#".concat(r1);
      }

      if (!r2.contains("%")) {
         if (r2.equals("null")) {
            r2 = "0";
         }

         int num = Integer.parseInt(r2);
         if (num > 256) {
            RegisterValue t1 = new RegisterValue(num);

            arm.add(new MovwInstruction(t1.getLabel(), "#:lower16:" + Integer.toString(t1.getValue())));
            arm.add(new MovtInstruction(t1.getLabel(), "#:upper16:" + Integer.toString(t1.getValue())));
            r2 = t1.getLabel();
         }
         else {
            r2 = "#".concat(r2);
         }
      }

      switch(cond) {
         case "eq":
            arm.add(new MovInstruction(result, "#0"));
            arm.add(new CmpInstruction(r1, r2));
            arm.add(new MoveqInstruction(result, "#1"));
            break;
         case "ne":
            arm.add(new MovInstruction(result, "#0"));
            arm.add(new CmpInstruction(r1, r2));
            arm.add(new MovneInstruction(result, "#1"));
            break;
         case "slt":
            arm.add(new MovInstruction(result, "#0"));
            arm.add(new CmpInstruction(r1, r2));
            arm.add(new MovltInstruction(result, "#1"));
            break;
         case "sgt":
            arm.add(new MovInstruction(result, "#0"));
            arm.add(new CmpInstruction(r1, r2));
            arm.add(new MovgtInstruction(result, "#1"));
            break;
         case "sle":
            arm.add(new MovInstruction(result, "#0"));
            arm.add(new CmpInstruction(r1, r2));
            arm.add(new MovleInstruction(result, "#1"));
            break;
         case "sge":
            arm.add(new MovInstruction(result, "#0"));
            arm.add(new CmpInstruction(r1, r2));
            arm.add(new MovgeInstruction(result, "#1"));
            break;
      }
   }
}
