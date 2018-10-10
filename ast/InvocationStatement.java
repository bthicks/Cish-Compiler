package ast;

import cfg.Block;
import cfg.CFG;

import java.util.LinkedHashMap;

public class InvocationStatement
   extends AbstractStatement
{
   private final Expression expression;

   public InvocationStatement(int lineNum, Expression expression)
   {
      super(lineNum);
      this.expression = expression;
   }

   public boolean typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                            LinkedHashMap<String, LinkedHashMap<String, Type>> structs, Type retType)
   {
      expression.typeCheck(global, local, structs);
      return false;
   }

   public Block toBlock(Block block, CFG cfg, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      expression.toLLVM(block, global, local, structs, cfg.getParams());
      return block;
   }
}
