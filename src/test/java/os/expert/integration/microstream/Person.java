/*
 *  Copyright (c) 2023 Otavio
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 */

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

    public static Person of(String id, String name, LocalDate birthday) {
        return new Person(id, name, birthday);
    }
}
