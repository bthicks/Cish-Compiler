package ast;

import java.util.List;

public class FunctionType
   implements Type
{
   private final Type retType;
   private final List<Declaration> params;
   private final List<Declaration> locals;

   public FunctionType(Type retType, List<Declaration> params, List<Declaration> locals)
   {
      this.retType = retType;
      this.params = params;
      this.locals = locals;
   }

   public Type typeCheck()
   {
      return new FunctionType(retType, params, locals);
   }

   public Type getRetType() {
      return retType;
   }

   public List<Declaration> getParams() {
      return params;
   }

   public List<Declaration> getLocals() {
      return locals;
   }

   public String toLLVM() {
      return "FunctionType to LLVM";
   }
}
