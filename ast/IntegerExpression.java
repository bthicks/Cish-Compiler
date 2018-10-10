package ast;

import cfg.Block;
import llvm.ImmediateValue;
import llvm.Value;

import java.util.LinkedHashMap;
import java.util.List;

public class IntegerExpression
   extends AbstractExpression
{
   private final String value;

   public IntegerExpression(int lineNum, String value)
   {
      super(lineNum);
      this.value = value;
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      return new IntType();
   }

   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      return new ImmediateValue(value, "i32");
   }
}
