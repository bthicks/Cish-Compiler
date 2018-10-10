package ast;

import cfg.Block;
import llvm.ImmediateValue;
import llvm.Value;

import java.util.LinkedHashMap;
import java.util.List;

public class LvalueId
   implements Lvalue
{
   private final int lineNum;
   private final String id;

   public LvalueId(int lineNum, String id)
   {
      this.lineNum = lineNum;
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
         return null;
      }
   }

   private void typeError() {
      System.out.println(lineNum + ": Type error in lvalue id");
      System.exit(1);
   }

   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      Type ty;

      if (local.containsKey(id)) {
         ty = local.get(id);
      }
      else {
         ty = global.get(id);
      }

      return new ImmediateValue("%" + id, ty.toLLVM());
   }
}
