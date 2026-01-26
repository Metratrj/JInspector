public class Programm {
    public static void main(String[] args) {
        Person p1 = new Person("Johannes", "Grimm");
        Person p2 = new Person("Anna", "Heske");
        Person p3 = new Person("Johannes", "Grimm");

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);

        System.out.println(p1.equals(p2));
        System.out.println(p1.equals(p3));
    }
}
