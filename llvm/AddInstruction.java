package llvm;

import arm.MovwInstruction;
import arm.MovtInstruction;

import java.util.LinkedHashMap;
import java.util.List;

public class AddInstruction
   extends AbstractInstruction
{
   private final String result;
   private final String ty;
   private final String op1;
   private final String op2;

   public AddInstruction(String result, String ty, String op1, String op2)
   {
      this.result = result;
      this.ty = ty;
      this.op1 = op1;
      this.op2 = op2;
   }

   public String toLLVM()
   {
      return "\t" + result + " = add " + ty + " " + op1 + ", " + op2 + "\n";
   }

   public void toARM(List<arm.Instruction> arm, LinkedHashMap<String, Integer> stack)
   {
      String r1 = op1;
      String r2 = op2;
      int num;

      if (!r1.contains("%")) {
         num = Integer.parseInt(r1);

         if (num > 256) {
            RegisterValue t1 = new RegisterValue(num);
            arm.add(new MovwInstruction(t1.getLabel(), "#:lower16:" + Integer.toString(t1.getValue())));
            arm.add(new MovtInstruction(t1.getLabel(), "#:upper16:" + Integer.toString(t1.getValue())));
            r1 = t1.getLabel();
         }
         else {
            RegisterValue t1;

            t1 = new RegisterValue(Integer.parseInt(r1));
            arm.add(new MovwInstruction(t1.getLabel(), "#".concat(r1)));
            r1 = t1.getLabel();
         }
      }

      if (!r2.contains("%")) {
         num = Integer.parseInt(r2);

         if (num > 256) {
            RegisterValue t2 = new RegisterValue(num);
            arm.add(new MovwInstruction(t2.getLabel(), "#:lower16:" + Integer.toString(t2.getValue())));
            arm.add(new MovtInstruction(t2.getLabel(), "#:upper16:" + Integer.toString(t2.getValue())));
            r2 = t2.getLabel();
         }
         else {
            r2 = "#".concat(r2);
         }
      }

      arm.add(new arm.AddInstruction(result, r1, r2));
   }
}
