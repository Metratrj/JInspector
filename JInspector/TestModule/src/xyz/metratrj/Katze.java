package xyz.metratrj;

public class Katze extends Tier {
    public int Leben;

    public Katze(String name) {
        super(name);
        this.Leben = 9;
    }

    @Override
    public void MachLaut() {
        System.out.println("Miauuuuu!");
    }

    @Override
    public String toString() {
        return "Katze{" +
                "Leben=" + Leben +
                "} " + super.toString();
    }
}
