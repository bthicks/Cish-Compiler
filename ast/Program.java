package ast;

import cfg.Block;
import cfg.CFG;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Program
{
   private final List<TypeDeclaration> types;
   private final List<Declaration> decls;
   private final List<Function> funcs;
   private LinkedHashMap<String, Type> global;
   private LinkedHashMap<String, LinkedHashMap<String, Type>> structs;
   private List<CFG> cfg;

   public Program(List<TypeDeclaration> types, List<Declaration> decls,
      List<Function> funcs)
   {
      this.types = types;
      this.decls = decls;
      this.funcs = funcs;
      this.global = new LinkedHashMap<>();
      this.structs = new LinkedHashMap<>();
   }

   public void typeCheck()
   {
      //System.out.println("Typechecking program...");

      for (TypeDeclaration type : types) {
         structs = type.typeCheck(structs);
      }

      for (Declaration decl : decls) {
         global = decl.typeCheck(global); // TODO: ?
      }

      for (Function func : funcs) {
         global = func.typeCheck(global, structs);
      }

      if (!global.containsKey("main")) {
         typeError();
      }

      if (!(((FunctionType) global.get("main")).getRetType() instanceof IntType)) {
         typeError();
      }

      //System.out.println("Typecheck passed");
   }

   private void typeError() {
      System.out.println("Missing 'main' function");
      System.exit(1);
   }

   public List<CFG> toCFG() {
      cfg = new ArrayList<>();

      for (Function func : funcs) {
         cfg.add(func.toCFG(global, structs));
      }

      // print cfg
      /*for (CFG current : cfg) {
         current.printCFG();
      }*/

      return cfg;
   }

   public void toLLVM(String arg) {
      String fname = arg + ".ll";
      File prev = new File(fname);
      prev.delete();
      File file = new File(fname);

      try (FileWriter fw = new FileWriter(file, true)) {
         fw.write("target triple=\"i686\"\n");

         for (TypeDeclaration type : types) {
            fw.write(type.toLLVM() + "\n");
         }

         fw.write("\n");

         for (Declaration decl : decls) {
            fw.write(decl.globalToLLVM() + "\n");
         }

         for (Function func : funcs) {
            fw.write(func.toLLVM() + "\n");
         }

         fw.write("declare i8* @malloc(i32)\n" +
                 "declare void @free(i8*)\n" +
                 "declare i32 @printf(i8*, ...)\n" +
                 "declare i32 @scanf(i8*, ...)\n" +
                 "@.println = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1\n" +
                 "@.print = private unnamed_addr constant [4 x i8] c\"%d \\00\", align 1\n" +
                 "@.read = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1");
      }
      catch (IOException e) {
         System.err.println("IOException: " + e.getMessage());
      }
   }

   public void toARM(String arg, Boolean regAlloc) {
      String fname = arg + ".s";
      File prev = new File(fname);
      prev.delete();
      File file = new File(fname);

      try (FileWriter fw = new FileWriter(file, true)) {
         fw.write("\t\t.arch armv7-a\n");

         for (Declaration decl : decls) {
            fw.write(decl.globalToARM() + "\n");
         }

         fw.write("\t\t.text\n");

         for (Function func : funcs) {
            fw.write("\t\t.align 2\n");
            fw.write("\t\t.global " + func.getName() + "\n");
            fw.write(func.toARM(regAlloc) + "\n");
         }

         fw.write("\t\t.section\t\t.rodata\n" +
                 "\t\t.align 2\n" +
                 ".PRINTLN_FMT:\n" +
                 "\t\t.asciz\t\"%ld\\n\"\n" +
                 "\t\t.align\t2\n" +
                 ".PRINT_FMT:\n" +
                 "\t\t.asciz\t\"%ld \"\n" +
                 "\t\t.align\t2\n" +
                 ".READ_FMT:\n" +
                 "\t\t.asciz\t\"%ld\"\n" +
                 "\t\t.comm\t.read_scratch,4,4\n" +
                 "\t\t.global\t__aeabi_idiv\n");
      }
      catch (IOException e) {
         System.err.println("IOException: " + e.getMessage());
      }
   }

   public void LVA() {
      for (CFG current : cfg) {
         current.LVA();
      }
   }
}
