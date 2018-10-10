package ast;

public interface Type
{
    Type typeCheck();
    String toLLVM();
}
