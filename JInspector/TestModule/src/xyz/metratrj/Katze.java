package xyz.metratrj;

public class Katze extends Tier{

    public Katze(String name) {
        super(name);
    }

    @Override
    public void MachLaut() {
        System.out.println("Miauuuuu!");
    }


}
