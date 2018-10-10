package ast;

import cfg.Block;
import cfg.CFG;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BlockStatement
   extends AbstractStatement {
   private final List<Statement> statements;

   public BlockStatement(int lineNum, List<Statement> statements) {
      super(lineNum);
      this.statements = statements;
   }

   public static BlockStatement emptyBlock() {
      return new BlockStatement(-1, new ArrayList<>());
   }

   public boolean typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                            LinkedHashMap<String, LinkedHashMap<String, Type>>structs, Type retType)
   {
      boolean ret = false;

      for (Statement statement : statements) {
         if (ret) {
            typeError();
         }
         else if (statement.typeCheck(global, local, structs, retType)) {
            ret = true;
         }
      }
      return ret;
   }

   private void typeError()
   {
      System.out.println(super.getLineNum() + ": Unreachable code in block statement");
      System.exit(1);
   }

   public Block toBlock(Block current, CFG cfg, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      for (Statement statement : statements)
      {
         current = statement.toBlock(current, cfg, global, local, structs);
      }
      return current;
   }
}
