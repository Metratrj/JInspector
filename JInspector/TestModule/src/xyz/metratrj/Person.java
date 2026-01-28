package xyz.metratrj;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person {
    private String firstname;
    private String lastname;
    private ArrayList<Tier> haustiere;

    public Person(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.haustiere = new ArrayList<>();
    }

    public Person(String firstname, String lastname, ArrayList<Tier> haustiere) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.haustiere = haustiere;
    }

    public Person(String firstname, String lastname, Tier... haustiere) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.haustiere = new ArrayList<>(haustiere.length);
        this.haustiere.addAll(List.of(haustiere));
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", haustiere=" + List.of(haustiere) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Person person)) return false;
        return Objects.equals(getFirstname(), person.getFirstname()) && Objects.equals(getLastname(), person.getLastname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstname(), getLastname());
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public ArrayList<Tier> getHaustiere() {
        return haustiere;
    }

    public void setHaustiere(ArrayList<Tier> haustiere) {
        this.haustiere = haustiere;
    }

    public void addHaustier(Tier haustier) {
        this.haustiere.add(haustier);
    }
}
