# Should really move to something else to manage the build.

# Provided code assumes Java 8
JAVAC=javac
JAVA=java
ANTLRJAR=/Users/brianhicks/Desktop/csc431/lib/antlr-4.7.1-complete.jar
JSONJAR=/Users/brianhicks/Desktop/csc431/lib/javax.json-1.0.4.jar

CLASSPATH_MOD=$(ANTLRJAR):$(JSONJAR)

FILES=MiniCompiler.java MiniToJsonVisitor.java MiniToAstDeclarationsVisitor.java MiniToAstExpressionVisitor.java MiniToAstFunctionVisitor.java MiniToAstProgramVisitor.java MiniToAstStatementVisitor.java MiniToAstTypeDeclarationVisitor.java MiniToAstTypeVisitor.java

GENERATED=MiniBaseVisitor.java MiniLexer.java MiniLexer.tokens Mini.tokens MiniVisitor.java MiniParser.java MiniBaseListener.java MiniListener.java

all : MiniCompiler.class

MiniCompiler.class : antlr.generated ${FILES} *.java ast/*.java *.java cfg/*.java *.java llvm/*.java *.java arm/*.java
	$(JAVAC) -cp ${CLASSPATH}:$(CLASSPATH_MOD) *.java ast/*.java *.java cfg/*.java *.java llvm/*.java *.java arm/*.java

antlr.generated : Mini.g4
	$(JAVA) -cp ${CLASSPATH}:$(CLASSPATH_MOD) org.antlr.v4.Tool -visitor Mini.g4
	touch antlr.generated

clean:
	\rm -rf *generated* ${GENERATED} *.class ast/*.class *.class cfg/*.class *.class llvm/*.class *.class arm/*.class
