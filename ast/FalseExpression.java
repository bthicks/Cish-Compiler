package ast;

import cfg.Block;
import llvm.ImmediateValue;
import llvm.Value;

import java.util.LinkedHashMap;
import java.util.List;

public class FalseExpression
   extends AbstractExpression
{
   public FalseExpression(int lineNum)
   {
      super(lineNum);
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      return new BoolType();
   }
   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      return new ImmediateValue("0", "i32");
   }
}
