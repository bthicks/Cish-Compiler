package ast;

import cfg.Block;
import llvm.Value;

import java.util.LinkedHashMap;
import java.util.List;

public class NullExpression
   extends AbstractExpression
{
   public NullExpression(int lineNum)
   {
      super(lineNum);
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      return new VoidType();
   }

   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      //type should be type of "caller"
      return null;
   }
}
