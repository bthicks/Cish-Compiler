package llvm;

import arm.BInstruction;
import arm.BeqInstruction;
import arm.CmpInstruction;
import arm.Instruction;

import java.util.LinkedHashMap;
import java.util.List;

public class BranchConditionalInstruction
   extends AbstractInstruction
{
   private final String cond;
   private final String iftrue;
   private final String iffalse;

   public BranchConditionalInstruction(String cond, String iftrue, String iffalse)
   {
      this.cond = cond;
      this.iftrue = iftrue;
      this.iffalse = iffalse;
   }

   public String toLLVM()
   {
      return "\tbr i1 " + cond + ", label %" + iftrue + ", label %" + iffalse + "\n";
   }

   public void toARM(List<Instruction> arm, LinkedHashMap<String, Integer> stack) {
      arm.add(new CmpInstruction(cond, "#1"));
      arm.add(new BeqInstruction(iftrue));
      arm.add(new BInstruction(iffalse));
   }
}
