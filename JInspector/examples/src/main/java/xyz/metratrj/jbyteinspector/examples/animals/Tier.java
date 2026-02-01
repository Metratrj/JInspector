package xyz.metratrj.jbyteinspector.examples.animals;

public abstract class Tier {
    private String name;

    public Tier(String name) {
        this.name = name;
    }

    public abstract void MachLaut();

    @Override
    public String toString() {
        return "Tier{" +
                "name='" + name + '\'' +
                '}';
    }
}
