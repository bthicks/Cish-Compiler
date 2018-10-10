package ast;

import arm.AddInstruction;
import arm.PushInstruction;
import arm.SubInstruction;
import cfg.Block;
import cfg.CFG;
import llvm.AllocationInstruction;
import llvm.BranchInstruction;
import llvm.ReturnVoidInstruction;
import llvm.StoreInstruction;

import java.util.LinkedHashMap;
import java.util.List;

public class Function
{
   private final int lineNum;
   private final String name;
   private final Type retType;
   private final List<Declaration> params;
   private final List<Declaration> locals;
   private final Statement body;
   private LinkedHashMap<String, Type> local;
   private CFG cfg;
   private Block first;

   public Function(int lineNum, String name, List<Declaration> params,
      Type retType, List<Declaration> locals, Statement body)
   {
      this.lineNum = lineNum;
      this.name = name;
      this.params = params;
      this.retType = retType;
      this.locals = locals;
      this.body = body;
      this.local = new LinkedHashMap<>();
   }

   public String getName() {
      return name;
   }

   public Type getRetType()
   {
      return retType;
   }

   public LinkedHashMap<String, Type> typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      if (global.containsKey(name) || structs.containsKey(name)) {
         typeError();
      }

      global.put(name, new FunctionType(retType, params, locals));

      for (Declaration param : params) {
         local = param.typeCheck(local);
      }

      for (Declaration l : locals) {
         local = l.typeCheck(local);
      }

      if (!body.typeCheck(global, local, structs, retType) && !(retType instanceof VoidType)) {
         retError();
      }

      return global;
   }

   private void typeError()
   {
      System.out.println(lineNum + ": Type error in function");
      System.exit(1);
   }

   private void retError()
   {
      System.out.println("Program is not return equivalent");
      System.exit(1);
   }

   public CFG toCFG(LinkedHashMap<String, Type> global, LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      first = new Block();
      cfg = new CFG(name, retType, params, locals, first);
      Block exit = cfg.getExit();

      cfg.getEntry().addSuccessor(first);
      first.addPredecessor(cfg.getEntry(), cfg);
      cfg.addList(first);
      localsToLLVM();
      body.toBlock(first, cfg, global, local, structs);

      if (retType instanceof VoidType) {
         List<Block> list = cfg.getList();
         Block last = list.get(list.size() - 2);

         last.addInstruction(new BranchInstruction(exit.getLabel()));
         last.addSuccessor(exit);
         exit.addPredecessor(last, cfg);

         if (exit.getLLVM().isEmpty()) {
            exit.addInstruction(new ReturnVoidInstruction());
         }
      }

      return cfg;
   }

   public String toLLVM()
   {
      return "define " + retType.toLLVM() + " @" + name + "(" + paramsToLLVM() + ")\n" + "{\n" + cfg.toLLVM() + "}\n";
   }

   public String paramsToLLVM()
   {
      String ret = "";
      int i;

      if (params.size() == 0) {
         return  ret;
      }

      for (i = 0; i < params.size() - 1; i++) {
         ret = ret.concat(params.get(i).paramToLLVM() + ", ");
      }

      ret = ret.concat(params.get(params.size() - 1).paramToLLVM());

      return ret;
   }

   public void localsToLLVM()
   {
      if (!(retType instanceof VoidType)) {
         first.addInstruction(new AllocationInstruction("_retval_", retType.toLLVM()));
      }

      for (Declaration param : params) {
         first.addInstruction(new AllocationInstruction(param.getName(), param.getType().toLLVM()));
      }

      for (Declaration local : locals) {
         first.addInstruction(new AllocationInstruction(local.getName(), local.getType().toLLVM()));
      }

      for (Declaration param : params) {
         String ty = param.getType().toLLVM();
         String name = param.getName();
         first.addInstruction(new StoreInstruction(ty, "%_P_" + name, ty, "%" + name));
      }
   }

   public String toARM(Boolean regAlloc)
   {
      return name + ":\n" + cfg.toARM(regAlloc);
   }
}
