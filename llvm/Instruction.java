package llvm;

import java.util.LinkedHashMap;
import java.util.List;

public interface Instruction {
   String toLLVM();
   void toARM(List<arm.Instruction> arm, LinkedHashMap<String, Integer> stack);
}
