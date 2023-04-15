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

package expert.os.integration.microstream;


import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.time.Year;
import java.util.Objects;

@Entity
public class Car {

    @Id
    private final String plate;
    @Column
    private final  String model;

    @Column
    private final  Year release;

    private Car(String plate, String model, Year release) {
        this.plate = plate;
        this.model = model;
        this.release = release;
    }

    public String plate() {
        return plate;
    }

    public String model() {
        return model;
    }

    public Year release() {
        return release;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return Objects.equals(plate, car.plate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(plate);
    }

    @Override
    public String toString() {
        return "Car{" +
                "plate='" + plate + '\'' +
                ", model='" + model + '\'' +
                ", release=" + release +
                '}';
    }

    public static Car of(String plate, String model, Year year) {
        Objects.requireNonNull(plate, "plate is required");
        Objects.requireNonNull(model, "model is required");
        Objects.requireNonNull(year, "year is required");
        return new Car(plate, model, year);
    }
}
