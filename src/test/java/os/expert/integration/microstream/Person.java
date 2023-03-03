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
}
