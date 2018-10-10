package ast;

import java.util.LinkedHashMap;
import java.util.List;

public class TypeDeclaration
{
   private final int lineNum;
   private final String name;
   private final List<Declaration> fields;

   public TypeDeclaration(int lineNum, String name, List<Declaration> fields)
   {
      this.lineNum = lineNum;
      this.name = name;
      this.fields = fields;
   }

   public LinkedHashMap<String, LinkedHashMap<String, Type>> typeCheck(LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      if (structs.containsKey(name)) {
         typeError();
      }

      LinkedHashMap<String, Type> fieldsMap = new LinkedHashMap<>();

      for (Declaration field : fields) {
         fieldsMap = field.typeCheck(fieldsMap);
      }

      structs.put(name, fieldsMap);
      return  structs;
   }

   private void typeError()
   {
      System.out.println(lineNum + ": Type error in type declaration");
      System.exit(1);
   }

   public String toLLVM()
   {
      return "%struct." + name + " = type {" + toLLVMHelper() + "}";
   }

   public String toLLVMHelper()
   {
      String ret = "";
      int i;

      if (fields.size() == 0) {
         return ret;
      }

      for (i = 0; i < fields.size() - 1; i++) {
         ret = ret.concat(fields.get(i).getType().toLLVM() + ", ");
      }

      ret = ret.concat(fields.get(fields.size() - 1).getType().toLLVM());

      return ret;
   }
}
