package ast;

public class VoidType
   implements Type
{
    public Type typeCheck() {
        return new VoidType();
    }

    public String toLLVM()
    {
        return "void";
    }
}
