package ast;

import cfg.Block;
import cfg.CFG;
import llvm.BranchInstruction;
import llvm.ReturnVoidInstruction;

import java.util.LinkedHashMap;

public class ReturnEmptyStatement
   extends AbstractStatement
{
   public ReturnEmptyStatement(int lineNum)
   {
      super(lineNum);
   }

   public boolean typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs, Type retType)
   {
      if (!(retType instanceof VoidType)) {
         typeError();
      }
      return true;
   }

   public void typeError() {
      System.out.println(super.getLineNum() + ": Type error in return empty statement");
      System.exit(1);
   }

   public Block toBlock(Block block, CFG cfg, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      Block exit = cfg.getExit();
      block.addInstruction(new BranchInstruction(exit.getLabel()));
      block.addSuccessor(exit);
      exit.addPredecessor(block, cfg);

      if (exit.getLLVM().isEmpty()) {
         exit.addInstruction(new ReturnVoidInstruction());
      }

      return new Block();
   }
}
