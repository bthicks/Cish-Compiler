package llvm;

import arm.*;
import arm.Instruction;

import java.util.LinkedHashMap;
import java.util.List;

public class StoreInstruction
   extends AbstractInstruction
{
   private final String ty1;
   private final String value;
   private final String ty2;
   private final String pointer;

   public StoreInstruction(String ty1, String value, String ty2, String pointer)
   {
      this.ty1 = ty1;
      this.value = value;
      this.ty2 = ty2;
      this.pointer = pointer;
   }

   public String toLLVM()
   {
      return "\tstore " + ty1 + " " + value + ", " + ty2 + "* " + pointer + "\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack) {
      Integer offset;
      String id;
      String r1 = value;
      Boolean isParam = false;

      if (pointer.contains("_P_")) {
         isParam = true;
      }

      id = pointer.replace("%", "");
      id = id.replace("_P_", "");

      offset = stack.get(id);

      if (!value.startsWith("%")) {
         RegisterValue t1;

         if (value.equals("null")) {
            t1 = new RegisterValue(value);

            arm.add(new MovInstruction(t1.getLabel(), "#0"));
            r1 = t1.getLabel();
         }
         else {
            int num = Integer.parseInt(value);
            t1 = new RegisterValue(num);

            if (num > 256) {
               arm.add(new MovwInstruction(t1.getLabel(), "#:lower16:" + Integer.toString(t1.getValue())));
               arm.add(new MovtInstruction(t1.getLabel(), "#:upper16:" + Integer.toString(t1.getValue())));
            }
            else {
               arm.add(new MovInstruction(t1.getLabel(), "#".concat(value)));
            }

            r1 = t1.getLabel();
         }
      }

      if (pointer.startsWith("@")) {
         id = pointer.replace("@", "");
         RegisterValue t1 = new RegisterValue(null);

         arm.add(new MovwInstruction(t1.getLabel(), "#:lower16:".concat(id)));
         arm.add(new MovtInstruction(t1.getLabel(), "#:upper16:".concat(id)));
         arm.add(new LdrInstruction(r1, t1.getLabel()));
      }
      else if (offset == null) {
         arm.add(new StrInstruction(r1, pointer));
      }
      else {
         arm.add(new StrInstruction(r1, "#-" + offset.toString()));
      }
   }
}
