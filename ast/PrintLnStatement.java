package ast;

import cfg.Block;
import cfg.CFG;
import llvm.CallInstruction;
import llvm.PrintfInstruction;
import llvm.Value;

import java.util.LinkedHashMap;

public class PrintLnStatement
   extends AbstractStatement
{
   private final Expression expression;

   public PrintLnStatement(int lineNum, Expression expression)
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
      Value v = expression.toLLVM(block, global, local, structs, cfg.getParams());
      block.addInstruction(new PrintfInstruction(true, v.getLabel()));

      return block;
   }
}
