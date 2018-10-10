package ast;

public class IntType
   implements Type
{
    public Type typeCheck() {
        return new IntType();
    }

    public String toLLVM()
    {
        return "i32";
    }
}
