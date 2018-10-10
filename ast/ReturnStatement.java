package ast;

import cfg.Block;
import cfg.CFG;
import llvm.*;

import java.util.LinkedHashMap;

public class ReturnStatement
   extends AbstractStatement
{
   private final Expression expression;

   public ReturnStatement(int lineNum, Expression expression)
   {
      super(lineNum);
      this.expression = expression;
   }

   public boolean typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                            LinkedHashMap<String, LinkedHashMap<String, Type>> structs, Type retType)
   {
      Type actual = expression.typeCheck(global, local, structs);

      if (retType instanceof StructType && actual instanceof VoidType) {
         return true;
      }
      else if (retType.getClass() != actual.getClass()) {
         typeError();
      }
      return true;
   }

   public void typeError() {
      System.out.println(super.getLineNum() + ": Type error in return statement");
      System.exit(1);
   }

   public Block toBlock(Block block, CFG cfg, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      Block exit = cfg.getExit();
      Value v = expression.toLLVM(block, global, local, structs, cfg.getParams());

      if (v == null) {
         v = new ImmediateValue("null", cfg.getRetType().toLLVM());
      }

      RegisterValue ret = new RegisterValue(v.getType());

      block.addInstruction(new StoreInstruction(v.getType(), v.getLabel(), ret.getType(), "%_retval_"));
      block.addInstruction(new BranchInstruction(exit.getLabel()));
      block.addSuccessor(exit);
      exit.addPredecessor(block, cfg);

      if (exit.getLLVM().isEmpty()) {
         exit.addInstruction(new LoadInstruction(ret.getLabel(), v.getType(), "%_retval_"));
         exit.addInstruction(new ReturnInstruction(ret.getType(), ret.getLabel()));
      }

      return new Block();
   }
}
