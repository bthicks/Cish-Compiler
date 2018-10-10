package ast;

import cfg.Block;
import llvm.GetElementPtrInstruction;
import llvm.RegisterValue;
import llvm.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LvalueDot
   implements Lvalue
{
   private final int lineNum;
   private final Expression left;
   private final String id;

   public LvalueDot(int lineNum, Expression left, String id)
   {
      this.lineNum = lineNum;
      this.left = left;
      this.id = id;
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      Type ltype = left.typeCheck(global, local, structs);

      if (!(ltype instanceof StructType)) {
         typeError();
      }

      StructType struct = (StructType) ltype;

      if (!structs.containsKey(struct.getName())) {
         typeError();
      }

      LinkedHashMap<String, Type> fields = structs.get(struct.getName());

      if (!fields.containsKey(id)) {
         typeError();
      }

      return fields.get(id);
   }

   public void typeError() {
      System.out.println(lineNum + ": Type error in lvalue dot");
      System.exit(1);
   }


   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      Value v1 = left.toLLVM(block, global, local, structs, params);

      // structName = remove "%struct." and "*" from v1.getType()
      String ty = v1.getType().replaceFirst("\\*", "");
      String structName = ty.replace("%struct.", "");

      // get LinkedHashMap<String, Type> struct from structName in structs
      LinkedHashMap<String, Type> struct = structs.get(structName);

      // convert struct to list of keys, structFields
      List<String> structFields = new ArrayList<>(struct.keySet());

      // use index of id in structFields
      String i = Integer.toString(structFields.indexOf(id));

      // create new register for id
      RegisterValue v2 = new RegisterValue(struct.get(id).toLLVM());

      block.addInstruction(new GetElementPtrInstruction(v2.getLabel(), ty, v1.getLabel(), i));

      return v2;
   }
}
