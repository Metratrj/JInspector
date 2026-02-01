package xyz.metratrj.jbyteinspector.examples.animals;

public class Main {
    public static void main(String[] args) {
        Person p1 = new Person("Johannes", "Grimm");
        Person p2 = new Person("Anna", "Heske");
        Person p3 = new Person("Johannes", "Grimm");

        Katze k1 = new Katze("Fluufy");
        Katze k2 = new Katze("Astroid Destroyer");
        Esel e1 = new Esel("Iaa");
        Esel e2 = new Esel("Timbo");

        p1.addHaustier(k1);
        p1.addHaustier(e2);
        p2.addHaustier(k2);
        p3.addHaustier(e1);

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);

        System.out.println(p1.equals(p2));
        System.out.println(p1.equals(p3));
    }
}
