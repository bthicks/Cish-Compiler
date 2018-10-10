package ast;

import cfg.Block;
import cfg.CFG;

import java.util.LinkedHashMap;

public interface Statement
{
    boolean typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                      LinkedHashMap<String, LinkedHashMap<String, Type>> structs, Type retType);

    Block toBlock(Block current, CFG cfg, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
       LinkedHashMap<String, LinkedHashMap<String, Type>> structs);
}
