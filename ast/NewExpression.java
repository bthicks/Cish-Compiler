package ast;

import cfg.Block;
import llvm.BitcastInstruction;
import llvm.CallInstruction;
import llvm.RegisterValue;
import llvm.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class NewExpression
   extends AbstractExpression
{
   private final String id;

   public NewExpression(int lineNum, String id)
   {
      super(lineNum);
      this.id = id;
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      if (!structs.containsKey(id)) {
         typeError();
      }

      return new StructType(super.getLineNum(), this.id);
   }

   private void typeError() {
      System.out.println(super.getLineNum() + ": Type error in new expression");
      System.exit(1);
   }

   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      List<String> argValues = new ArrayList<>();
      argValues.add("#12");

      RegisterValue r1 = new RegisterValue("i8*");
      RegisterValue r2 = new RegisterValue("%struct." + id + "*");

      block.addInstruction(new CallInstruction(r1.getLabel(), r1.getType(), "@malloc", "i32 24", argValues));
      block.addInstruction(new BitcastInstruction(r2.getLabel(), r1.getType(), r1.getLabel(), r2.getType()));

      return r2;
   }
}
