package cfg;

import java.util.*;

public class Block
{
   private static int count = 0;
   private final String label;
   private List<Block> predecessors;
   private List<Block> successors;
   private List<llvm.Instruction> llvm;
   private List<arm.Instruction> arm;
   private List<String> gen;
   private List<String> kill;
   private List<String> liveOut;
   private boolean liveOutChanged;

   public Block()
   {
      this.label = "LU" + String.valueOf(count++);
      this.predecessors = new ArrayList<>();
      this.successors = new ArrayList<>();
      this.llvm = new ArrayList<>();
      this.arm = new ArrayList<>();
      this.gen = new ArrayList<>();
      this.kill = new ArrayList<>();
      this.liveOut = new ArrayList<>();
      this.liveOutChanged = false;
   }

   public Block(String label)
   {
      this.label = label;
      this.predecessors = new ArrayList<>();
      this.successors = new ArrayList<>();
      this.llvm = new ArrayList<>();
      this.arm = new ArrayList<>();
      this.gen = new ArrayList<>();
      this.kill = new ArrayList<>();
      this.liveOut = new ArrayList<>();
      this.liveOutChanged = false;
   }

   public String getLabel() {
      return label;
   }

   public List<Block> getPredecessors() {
      return predecessors;
   }

   public void addPredecessor(Block e, CFG cfg) {
      if (cfg.contains(e)) {
         predecessors.add(e);
      }
   }

   public List<Block> getSuccessors() {
      return successors;
   }

   public void addSuccessor(Block e) {
      successors.add(e);
   }

   public List<llvm.Instruction> getLLVM() {
      return llvm;
   }

   public List<arm.Instruction> getARM() {
      return arm;
   }

   public void addInstruction(llvm.Instruction i)
   {
      llvm.add(i);
   }

   public void addArmInstruction(arm.Instruction i) { arm.add(i);}

   public void addArmInstruction(int index, arm.Instruction i) {
      arm.add(index, i);
   }

   public List<String> getGen() {
      return gen;
   }

   public void addGen(String s) {
      if (!gen.contains(s)) {
         gen.add(s);
      }
   }

   public List<String> getKill() {
      return kill;
   }

   public void addKill(String s) {
      if (!kill.contains(s)) {
         kill.add(s);
      }
   }

   public List<String> getLiveOut() {
      return liveOut;
   }

   public void addLiveOut(String s) {
      if (!liveOut.contains(s)) {
         liveOut.add(s);
      }
   }

   public void removeLiveOut(String s) {
      liveOut.remove(s);
   }

   public boolean getLiveOutChanged() {
      return liveOutChanged;
   }

   public void setLiveOutChanged(boolean b) {
      liveOutChanged = b;
   }

   public void printSets()
   {
      System.out.println(label + ":");

      // print gen set
      System.out.print("\tgen: ");
      for (String s : gen) {
         System.out.print(s + " ");
      }
      System.out.println();

      // print kill set
      System.out.print("\tkill: ");
      for (String s : kill) {
         System.out.print(s + " ");
      }
      System.out.println();

      // print live out set
      System.out.print("\tlive out: ");
      for (String s : liveOut) {
         System.out.print(s + " ");
      }
      System.out.println();
   }

   public String toLLVM()
   {
      if (llvm.isEmpty()) {
         return "";
      }

      String ret = label + ":\n";

      for (llvm.Instruction current : llvm) {
         ret = ret.concat("\t" + current.toLLVM());
      }

      return ret;
   }

   public String toARM(LinkedHashMap<String, Integer> stack, boolean isExit, int allocs, Boolean regAlloc)
   {
      String ret = "." + label + ":\n";

      if (!regAlloc) {
         // populate list of ARM instructions
         for (llvm.Instruction current : llvm) {
            current.toARM(arm, stack);
         }
      }

      // get list of ARM instructions
      for (arm.Instruction current : arm) {
         ret = ret.concat("\t\t" + current.toARM());
      }

      // block has no instructions
      if (arm.isEmpty()) {
         return "";
      }

      return ret;
   }
}
