package ast;

import cfg.Block;
import llvm.Value;

import java.util.LinkedHashMap;
import java.util.List;

public interface Lvalue
{
   Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs);

   Value toLLVM(Block current, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params);
}
