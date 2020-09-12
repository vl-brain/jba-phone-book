package phonebook;

import java.util.Comparator;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Person implements Comparable<Person> {
    private final String name;
    private final String surname;

    public Person(String name) {
        this(name, null);
    }

    public Person(String name, String surname) {
        this.name = requireNonNull(name, "Name required!");
        this.surname = surname;
    }

    public static Person parse(String value) {
        final String[] parts = value.split("\\s+", 2);
        if (parts.length < 1) {
            throw new IllegalArgumentException("Can't parse Person from: " + value);
        }
        return parts.length == 2 ? new Person(parts[0], parts[1]) : new Person(parts[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return name.equals(person.name) &&
                Objects.equals(surname, person.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname);
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public int compareTo(Person other) {
        return Comparator.comparing(Person::getName)
                .thenComparing(Person::getSurname, Comparator.nullsFirst(Comparator.naturalOrder()))
                .compare(this, other);
    }

    @Override
    public String toString() {
        return surname == null || surname.isEmpty() ? name : name + " " + surname;
    }
}

