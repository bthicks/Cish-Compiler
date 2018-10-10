package ast;

import cfg.Block;
import cfg.CFG;
import llvm.CallInstruction;
import llvm.PrintfInstruction;
import llvm.Value;

import java.util.LinkedHashMap;

public class PrintStatement
   extends AbstractStatement
{
   private final Expression expression;

   public PrintStatement(int lineNum, Expression expression)
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
      block.addInstruction(new PrintfInstruction(false, v.getLabel()));

      return block;
   }
}
