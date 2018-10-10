package arm;

import java.util.ArrayList;
import java.util.List;

public class PushInstruction
   extends AbstractInstruction
{
   private List<String> registers;

   public PushInstruction(String rn, String rm)
   {
      registers = new ArrayList<>();
      registers.add(rn);
      registers.add(rm);
   }

   public PushInstruction(List<String> registers)
   {
      this.registers = registers;
   }

   public String toARM()
   {
      return "push\t{" + toARMHelper() + "}\n";
   }

   private String toARMHelper()
   {
      String ret = "";

      for (int i = 0; i < registers.size() - 1; i++) {
         ret = ret.concat(registers.get(i) + ", ");
      }

      ret = ret.concat(registers.get(registers.size() - 1));

      return ret;
   }
}
