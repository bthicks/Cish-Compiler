package cfg;

import arm.*;
import ast.Declaration;
import ast.Type;
import ast.VoidType;

import java.util.*;

public class CFG
{
   private final String funcName;
   private final Type retType;
   private Block entry;
   private Block first;
   private Block exit;
   private List<Block> list;
   private List<Declaration> params;
   private List<Declaration> locals;
   private HashMap<String, Node> intfGraph;

   public CFG(String funcName, Type retType, List<Declaration> params, List<Declaration> locals, Block first)
   {
      this.funcName = funcName;
      this.retType = retType;
      this.entry = new Block("entry");
      this.first = first;
      this.exit = new Block();
      this.list = new ArrayList<>();
      this.params = params;
      this.locals = locals;
      this.intfGraph = new HashMap<>();

      list.add(entry);
      list.add(exit);
   }

   public String getFuncName() {
      return funcName;
   }

   public Type getRetType() {
      return retType;
   }

   public Block getEntry() {
      return entry;
   }

   public Block getExit() {
      return exit;
   }

   public List<Block> getList() {
      return list;
   }

   public List<Declaration> getParams() {
      return params;
   }

   public void addList(Block current)
   {
      list.add(list.size() - 1, current);
   }

   public boolean contains(Block e) {
      return list.contains(e);
   }

   public void printCFG()
   {
      System.out.println("==== " + funcName + " ====");
      for (Block current : list)
      {
         System.out.print("Block " + current.getLabel() + ": ");
         System.out.print("\n\t-->: ");
         for (Block successor : current.getSuccessors())
         {
            System.out.print(successor.getLabel() + " ");
         }
         System.out.print("\n\t<--: ");
         for (Block predecessor : current.getPredecessors()) {
            System.out.print(predecessor.getLabel() + " ");
         }
         System.out.println();
      }
   }

   public Boolean containsExit()
   {
      return list.contains(exit);
   }

   public String toLLVM()
   {
      String ret = "";

      for (Block block : list) {
         ret = ret.concat(block.toLLVM());
      }

      return ret;
   }

   public String toARM(Boolean regAlloc)
   {
      String ret = "";
      boolean isExit = false;
      int offset = 40;
      int allocs = 0;
      LinkedHashMap<String, Integer> stack = new LinkedHashMap<>();

      if (!regAlloc) {
         if (!(retType instanceof VoidType)) {
            stack.put("_retval_", offset);
            offset += 4;
            allocs++;
         }

         for (Declaration param : params) {
            stack.put(param.getName(), offset);
            offset += 4;
            allocs++;
         }

         for (Declaration local : locals) {
            stack.put(local.getName(), offset);
            offset += 4;
            allocs++;
         }
      }

      for (Block block : list) {
         if (block == exit) {
            isExit = true;
         }
         ret = ret.concat(block.toARM(stack, isExit, allocs, regAlloc));
      }

      ret = ret.concat("\t\t.size " + funcName + ", .-" + funcName);

      return ret;
   }

   public void LVA()
   {
      boolean changed = true;
      int regs = 0;

      // pre-color parameters as r0-rx
      int r = 0;
      for (Instruction instr : first.getARM()) {
         if (instr instanceof StrInstruction) {
            for (String source : instr.getSources()) {
               if (source.contains("%_P_")) {
                  instr.allocateSource(source, "r" + Integer.toString(r++));
               }
            }
         }
      }

      // reverse list to run algorithm from bottom up
      Collections.reverse(list);

      // dataflow iterative analysis
      while (changed) {
         changed = false;

         for (Block block : list) {
            LVAHelper(block);
            if (block.getLiveOutChanged()) {
               changed = true;
               block.setLiveOutChanged(false);
            }
         }
      }

      // re-reverse list
      Collections.reverse(list);

      // print gen, kill, and liveout sets
      //printSets();

      // generate interference graph
      for (Block block : list) {
         genIntfGraph(block);
      }

      // print interference graph
      //printIntfGraph();

      // color interference graph
      for (Block block : list) {
         int ret = colorInterferenceGraph(block);

         if (ret > regs) {
            regs = ret;
         }
      }

      // allocate stack space
      allocateStack(regs);

      // allocate registers
      for (Block block : list) {
         allocateRegisters(block);
      }
   }

   private void LVAHelper(Block block)
   {
      List<arm.Instruction> arm = block.getARM();
      List<String> kill = block.getKill();

      // create gen and kill sets
      for (Instruction current : arm) {
         for (String source : current.getSources()) {
            if (!kill.contains(source)) {
               block.addGen(source);
            }
         }
         for (String target : current.getTargets()) {
            block.addKill(target);
         }
      }

      // compute live out set
      List<String> prevLiveOut = block.getLiveOut();

      for (Block succ : block.getSuccessors()) {
         // add gen sets of successors to this block's live out set
         for (String s : succ.getGen()) {
            block.addLiveOut(s);
         }
         // add (live out - kill) sets of successors to this block's live out set
         for (String s : succ.getLiveOut()) {
            if (!succ.getKill().contains(s)) {
               block.addLiveOut(s);
            }
         }
      }

      // check if live out set changed
      if (!prevLiveOut.equals(block.getLiveOut())) {
         block.setLiveOutChanged(true);
      }
   }

   public void printSets()
   {
      System.out.println("==== " + funcName + " ====");
      for (Block block : list) {
         block.printSets();
      }
   }

   public void genIntfGraph(Block block)
   {
      List<String> liveOut = block.getLiveOut();
      List<arm.Instruction> arm = block.getARM();

      // add nodes from live set to graph
      for (String s : liveOut) {
         if (intfGraph.get(s) == null) {
            intfGraph.put(s, new Node(s));
         }
      }

      // reverse list to visit instructions from bottom->top
      Collections.reverse(arm);
      for (Instruction current : arm) {
         List<String> targets = current.getTargets();
         List<String> sources = current.getSources();

         // case of instruction w/o targets
         if (targets.isEmpty()) {
            for (String source : sources) {
               // if source not in graph, create node for it and add to graph
               if (intfGraph.get(source) == null) {
                  intfGraph.put(source, new Node(source));
               }
               // add each source to live set
               block.addLiveOut(source);
            }
            continue;
         }
         // add target to graph (checks for duplicates)
         else {
            for (String target : targets) {
               if (intfGraph.get(target) == null) {
                  intfGraph.put(target, new Node(target));
               }
            }
         }

         // remove target from live set
         for (String target : targets) {
            block.removeLiveOut(target);
         }

         // add edge from target to each element in live set
         for (String source : liveOut) {
            Node temp = intfGraph.get(source);

            // if source not in graph, create node for it and add to graph
            if (temp == null) {
               temp = new Node(source);
               intfGraph.put(source, temp);
            }

            for (String target : targets) {
               temp.addSource(intfGraph.get(target));
               intfGraph.get(target).addSource(temp);
            }
         }

         // add each source to live set
         for (String source : sources) {
            if (intfGraph.get(source) == null) {
               intfGraph.put(source, new Node(source));
            }
            block.addLiveOut(source);
         }
      }

      // re-reverse list of instructions
      Collections.reverse(arm);
   }

   public void printIntfGraph()
   {
      List<Node> intfList = new ArrayList<>(intfGraph.values());
      System.out.println(funcName + " Interference graph:");

      for (Node node : intfList) {
         System.out.print("\t" + node.getName() + ": ");

         for (Node source : node.getSources()) {
            System.out.print(source.getName() + " ");
         }
         System.out.println();
      }
   }

   // returns highest color/register used in this block
   public int colorInterferenceGraph(Block block)
   {
      int ret = 0;
      Stack<Node> stack = new Stack<>();
      List<String> colors = new ArrayList<>();
      // populate colors for registers 0-9
      for (int i = 0; i < 10; i++) {
         colors.add(Integer.toString(i));
      }

      // sort Nodes of IntfGraph in ascending order of edge number
      LinkedList<Node> intfList = new LinkedList<>(intfGraph.values());
      Collections.sort(intfList, new SortByEdges());

      while (!intfList.isEmpty()) {
         // if list contains unconstrained node (virtual)
         //    remove it and add to stack
         // else if list contains constrained node (virtual)
         //    remove it and add to stack
         // else
         //    remove a real register

         // remove node from list
         Node node = intfList.removeFirst();

         // only remove real registers once all virtuals have been removed
         if (node.isReal() && containsVirtual(intfList)) {
            intfList.add(node);
            continue;
         }

         // remove edges between node and its sources
         List<Node> sources = node.getSources();
         for (Node source : sources) {
            source.removeSource(node);
         }

         // push node onto stack
         stack.push(node);
      }
      while (!stack.isEmpty()) {
         // pop node and add to interference graph
         Node node = stack.pop();
         intfList.add(node);

         // if pre-colored register (params), leave as is
         if (node.isReal()) {
            node.setColor(node.getName().replace("r", ""));
         }
         else {
            // get list of neighbor colors
            List<String> takenColors = new ArrayList<>();
            for (Node source : node.getSources()) {
               takenColors.add(source.getColor());
            }
            // if there's a color remaining
            for (int i = 0; i < 10; i++) {
               if (!takenColors.contains(colors.get(i))) {
                  // color node
                  node.setColor(Integer.toString(i));
                  // keep track of highest color used
                  if (i > ret) {
                     ret = i;
                  }
                  break;
               }
            }
            // if no colors remaining
            if (node.getColor() == null) {
               // spill node
               node.setColor("spilled");
            }
         }
      }

      return ret;
   }

   // helper function for colorInterferenceGraph
   private boolean containsVirtual(LinkedList<Node> intfList)
   {
      for (Node node : intfList) {
         if (!node.isReal()) {
            return true;
         }
      }

      return false;
   }

   private void allocateStack(int regs)
   {
      // allocate stack space for params, locals, and ret value (if non-void)
      int allocs = params.size() + locals.size();
      if (!(retType instanceof VoidType)) {
         allocs++;
      }

      // push/pop registers r4-rx if used
      List<String> registers = new ArrayList<>();
      //if (regs > 3) {
         //TODO: fix
         /*for (int i = 4; i <= regs; i++) {
            registers.add("r" + Integer.toString(i));
         }*/
         for (int i = 4; i <= 11; i++) {
            registers.add("r" + Integer.toString(i));
         }
      //}

      // stack setup
      if (allocs > 0) {
         first.addArmInstruction(0, new SubInstruction("sp", "sp", "#" + Integer.toString(allocs * 4)));
      }
      //if (regs > 3) {
         first.addArmInstruction(0, new PushInstruction(registers));
      //}
      first.addArmInstruction(0, new AddInstruction("fp", "sp", "#4"));
      first.addArmInstruction(0, new PushInstruction("fp", "lr"));

      // stack teardown
      if (allocs > 0) {
         exit.addArmInstruction(new AddInstruction("sp", "sp", "#" + Integer.toString(allocs * 4)));
      }
      //if (regs > 3) {
         exit.addArmInstruction(new PopInstruction(registers));
      //}
      exit.addArmInstruction(new SubInstruction("sp", "fp", "#4"));
      exit.addArmInstruction(new PopInstruction("fp", "pc"));
   }

   public void allocateRegisters(Block block)
   {
      List<arm.Instruction> arm = block.getARM();

      for (Instruction instr : arm) {
         // get targets
         for (String target : instr.getTargets()) {
            // reassign target to ("r" + intfGraph.get(target).getColor())
            if (!target.startsWith("r")) {
               instr.allocateTarget("r" + intfGraph.get(target).getColor());
            }
         }

         // get sources
         for (String source : instr.getSources()) {
            // reassign sources to ("r" + intfGraph.get(source).getColor())
            if (!source.startsWith("r")) {
               instr.allocateSource(source, "r" + intfGraph.get(source).getColor());
            }
         }
      }
   }
}
