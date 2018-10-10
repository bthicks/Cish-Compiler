package ast;

import cfg.Block;
import cfg.CFG;
import llvm.BitcastInstruction;
import llvm.CallInstruction;
import llvm.RegisterValue;
import llvm.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DeleteStatement
   extends AbstractStatement
{
   private final Expression expression;

   public DeleteStatement(int lineNum, Expression expression)
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
      Value v1 = expression.toLLVM(block, global, local, structs, cfg.getParams());
      RegisterValue v2 = new RegisterValue("i8*");

      List<String> argValues = new ArrayList<>();
      argValues.add(v2.getLabel());

      block.addInstruction(new BitcastInstruction(v2.getLabel(), v1.getType(), v1.getLabel(), v2.getType()));
      block.addInstruction(new CallInstruction("void", "void", "@free", v2.getType() + " " + v2.getLabel(), argValues));
      return block;
   }
}
