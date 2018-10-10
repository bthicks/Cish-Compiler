package ast;

public class BoolType
   implements Type
{
    public Type typeCheck() {
        return new BoolType();
    }

    public String toLLVM()
    {
        return "i32";
    }
}
