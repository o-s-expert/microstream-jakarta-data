package os.expert.integration.microstream;

import jakarta.nosql.Column;
import jakarta.nosql.Id;

import java.time.LocalDate;
import java.time.Year;

public class Person {

    @Id
    private String id;

    @Column
    private String name;

    @Column
    private LocalDate birthday;


    private Person(String id, String name, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public LocalDate birthday() {
        return birthday;
    }

    public Person of(String id, String name, LocalDate birthday) {
        return new Person(id, name, birthday);
    }
}
