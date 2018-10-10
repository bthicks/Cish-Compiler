package ast;

import cfg.Block;
import llvm.LoadInstruction;
import llvm.RegisterValue;
import llvm.Value;

import java.util.LinkedHashMap;
import java.util.List;

public class IdentifierExpression
   extends AbstractExpression
{
   private final String id;

   public IdentifierExpression(int lineNum, String id)
   {
      super(lineNum);
      this.id = id;
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      if (local.containsKey(id)) {
         return local.get(id);
      }
      else if (global.containsKey(id)) {
         return global.get(id);
      }
      else {
         typeError();
      }
      return null;
   }

   private void typeError() {
      System.out.println(super.getLineNum() + ": Type error in identifier expression");
      System.exit(1);
   }

   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String ,Type>> structs, List<Declaration> params)
   {
      RegisterValue target;

      if (local.containsKey(id) && local.get(id) instanceof StructType) {
         StructType struct = (StructType) local.get(id);
         target = new RegisterValue("%struct." + struct.getName() + "*");
         block.addInstruction(new LoadInstruction(target.getLabel(), target.getType(), "%" + id));
      }
      else if (global.containsKey(id) && global.get(id) instanceof StructType && !local.containsKey(id)) {
         StructType struct = (StructType) global.get(id);
         target = new RegisterValue("%struct." + struct.getName() + "*");
         block.addInstruction(new LoadInstruction(target.getLabel(), target.getType(), "@" + id));
      }
      else {
         target = new RegisterValue("i32");

         if (global.containsKey(id) && !local.containsKey(id)) {
            block.addInstruction(new LoadInstruction(target.getLabel(), "i32", "@" + id));
         }
         else {
            block.addInstruction(new LoadInstruction(target.getLabel(), "i32", "%" + id));
         }
      }

      return target;
   }
}
