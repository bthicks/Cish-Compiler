package ast;

import cfg.Block;
import cfg.CFG;
import llvm.BranchConditionalInstruction;
import llvm.RegisterValue;
import llvm.TruncateInstruction;
import llvm.Value;

import java.util.LinkedHashMap;

public class WhileStatement
   extends AbstractStatement
{
   private final Expression guard;
   private final Statement body;

   public WhileStatement(int lineNum, Expression guard, Statement body)
   {
      super(lineNum);
      this.guard = guard;
      this.body = body;
   }

   public boolean typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                            LinkedHashMap<String, LinkedHashMap<String, Type>> structs, Type retType)
   {
      if (!(guard.typeCheck(global, local, structs) instanceof BoolType)) {
         typeError();
      }

      body.typeCheck(global, local, structs, retType);
      return false;
   }

   private void typeError() {
      System.out.println(super.getLineNum() + ": Type error in while statement");
      System.exit(1);
   }

   public Block toBlock(Block block, CFG cfg, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      Value v = guard.toLLVM(block, global, local, structs, cfg.getParams());
      RegisterValue r = new RegisterValue("i1");
      Block thenNode = new Block();
      Block joinNode = new Block();
      thenNode.addPredecessor(block, cfg);
      thenNode.addPredecessor(thenNode, cfg);
      joinNode.addPredecessor(block, cfg);

      block.addInstruction(new TruncateInstruction(r.getLabel(), "i32", v.getLabel(), "i1"));
      block.addInstruction(new BranchConditionalInstruction(r.getLabel(), thenNode.getLabel(), joinNode.getLabel()));
      block.addSuccessor(thenNode);
      block.addSuccessor(joinNode);
      cfg.addList(thenNode);

      Block thenEnd = body.toBlock(thenNode, cfg, global, local, structs);
      thenNode.addPredecessor(thenEnd, cfg);
      joinNode.addPredecessor(thenEnd, cfg);
      Value v2 = guard.toLLVM(thenEnd, global, local, structs, cfg.getParams());
      RegisterValue r2 = new RegisterValue("i1");

      thenEnd.addInstruction(new TruncateInstruction(r2.getLabel(), "i32", v2.getLabel(), "i1"));
      thenEnd.addInstruction(new BranchConditionalInstruction(r2.getLabel(), thenNode.getLabel(), joinNode.getLabel()));
      // TODO: should this be thenNode?
      thenEnd.addSuccessor(thenEnd);
      thenEnd.addSuccessor(joinNode);
      cfg.addList(joinNode);

      return joinNode;
   }
}
