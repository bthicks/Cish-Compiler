package ast;

import cfg.Block;
import llvm.ImmediateValue;
import llvm.Value;

import java.util.LinkedHashMap;
import java.util.List;

public class TrueExpression
   extends AbstractExpression
{
   public TrueExpression(int lineNum)
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
      /*
      ImmediateValue v1 = new ImmediateValue("true", "i1");
      RegisterValue v2 = new RegisterValue("i1");
      RegisterValue v3 = new RegisterValue("i32");

      block.addInstruction(new LoadInstruction(v2.getLabel(), v1.getType(), v1.getLabel()));
      block.addInstruction(new ZeroExtendInstruction(v3.getLabel(), "i32", v2.getLabel(), v2.getType()));
      return v3
      */
      /*
      ImmediateValue v1 = new ImmediateValue("true", "i1");
      RegisterValue v2 = new RegisterValue("i32");

      block.addInstruction(new ZeroExtendInstruction(v2.getLabel(), v2.getType(), v1.getLabel(), v1.getType()));

      return v2;*/
      //return new ImmediateValue("true", "i1");
      return new ImmediateValue("1", "i32");
   }
}
