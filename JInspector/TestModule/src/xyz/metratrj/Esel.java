package xyz.metratrj;

public class Esel extends Tier{

    public Esel(String name) {
        super(name);
    }

    @Override
    public void MachLaut() {
        System.out.println("Ihhhhhh, Ahhhhhh!");
    }

    @Override
    public String toString() {
        return "Esel{} " + super.toString();
    }
}
