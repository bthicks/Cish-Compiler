package ast;

import cfg.Block;
import cfg.CFG;
import llvm.*;

import java.util.LinkedHashMap;

public class ConditionalStatement
   extends AbstractStatement
{
   private final Expression guard;
   private final Statement thenBlock;
   private final Statement elseBlock;

   public ConditionalStatement(int lineNum, Expression guard,
      Statement thenBlock, Statement elseBlock)
   {
      super(lineNum);
      this.guard = guard;
      this.thenBlock = thenBlock;
      this.elseBlock = elseBlock;
   }

   public boolean typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                            LinkedHashMap<String, LinkedHashMap<String, Type>> structs, Type retType)
   {
      if (!(guard.typeCheck(global, local, structs) instanceof BoolType)) {
         typeError();
      }

      boolean thenRet = thenBlock.typeCheck(global, local, structs, retType);
      boolean elseRet = elseBlock.typeCheck(global, local, structs, retType);

      return (thenRet && elseRet);
   }

   public void typeError() {
      System.out.println(super.getLineNum() + ": Type error in conditional statement");
      System.exit(1);
   }

   public Block toBlock(Block block, CFG cfg, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      Value v = guard.toLLVM(block, global, local, structs, cfg.getParams());
      RegisterValue r = new RegisterValue("i1");
      Block thenNode = new Block();
      Block elseNode = new Block();
      thenNode.addPredecessor(block, cfg);
      elseNode.addPredecessor(block, cfg);
      Block joinNode = new Block();
      Block thenEnd;
      Block elseEnd;

      block.addInstruction(new TruncateInstruction(r.getLabel(), "i32", v.getLabel(), "i1"));
      block.addInstruction(new BranchConditionalInstruction(r.getLabel(), thenNode.getLabel(), elseNode.getLabel()));
      block.addSuccessor(thenNode);
      block.addSuccessor(elseNode);
      cfg.addList(thenNode);
      cfg.addList(elseNode);

      thenEnd = thenBlock.toBlock(thenNode, cfg, global, local, structs);
      elseEnd = elseBlock.toBlock(elseNode, cfg, global, local, structs);

      // Both "then" and "else" return, don't link to joinNode
      if (thenEnd.getPredecessors().isEmpty() && elseEnd.getPredecessors().isEmpty()) {
         return joinNode;
      }

      thenEnd.addInstruction(new BranchInstruction(joinNode.getLabel()));
      elseEnd.addInstruction(new BranchInstruction(joinNode.getLabel()));


      thenEnd.addSuccessor(joinNode);
      elseEnd.addSuccessor(joinNode);
      joinNode.addPredecessor(thenEnd, cfg);
      joinNode.addPredecessor(elseEnd, cfg);

      cfg.addList(joinNode);

      return joinNode;
   }
}
