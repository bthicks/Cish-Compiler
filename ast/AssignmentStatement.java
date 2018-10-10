package ast;

import cfg.Block;
import cfg.CFG;
import llvm.*;

import java.util.LinkedHashMap;

public class AssignmentStatement
   extends AbstractStatement
{
   private final Lvalue target;
   private final Expression source;

   public AssignmentStatement(int lineNum, Lvalue target, Expression source)
   {
      super(lineNum);
      this.target = target;
      this.source = source;
   }

   public boolean typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs, Type retType)
   {
      Type ltype = target.typeCheck(global, local, structs);
      Type rtype = source.typeCheck(global, local, structs);

      if (ltype instanceof StructType && rtype instanceof VoidType) {
         return false;
      }
      else if (ltype.getClass() == rtype.getClass()) {
         return false;
      }
      else {
         typeError();
         return false;
      }
   }

   private void typeError() {
      System.out.println(super.getLineNum() + ": Type error in assignment statement");
      System.exit(1);
   }

   public Block toBlock(Block block, CFG cfg, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      Value pointer = target.toLLVM(block, global, local, structs, cfg.getParams());
      Value value = source.toLLVM(block, global, local, structs, cfg.getParams());

      if (value == null) {
         value = new ImmediateValue("null", pointer.getType());
      }

      String id = pointer.getLabel().replace("%", "");
      if (global.containsKey(id) && !local.containsKey(id)) {
         id = "@".concat(id);
      }
      else {
         id = "%".concat(id);
      }

      if (source instanceof ReadExpression) {
         block.addInstruction(new ScanfInstruction(id));
      }
      else {
         block.addInstruction(new StoreInstruction(value.getType(), value.getLabel(), value.getType(), id));
      }

      return block;
   }
}
