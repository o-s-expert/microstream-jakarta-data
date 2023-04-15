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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class DataStorageTest {

    private DataStorage data;

    @BeforeEach
    public void setUp() {
        this.data = new DataStorage();
    }

    @Test
    public void shouldPut() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data)
                .isNotNull()
                .matches(p -> p.size() == 3);

    }

    @Test
    public void shouldGet() {
        this.data.put("one", 1);
        Optional<Object> one = this.data.get("one");
        Assertions.assertThat(one)
                .isPresent()
                .get()
                .isEqualTo(1);

        Optional<Object> two = this.data.get("two");
        Assertions.assertThat(two)
                .isNotPresent();
    }

    @Test
    public void shouldRemove() {
        this.data.put("one", 1);
        Optional<Object> one = this.data.get("one");
        Assertions.assertThat(one)
                .isPresent()
                .get()
                .isEqualTo(1);

        this.data.remove("one");

        Assertions.assertThat(this.data.get("one"))
                .isNotPresent();
    }

    @Test
    public void shouldSize() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.size())
                .isEqualTo(3);
    }

    @Test
    public void shouldIsEmpty() {
        Assertions.assertThat(this.data.isEmpty())
                .isTrue();

        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.isEmpty())
                .isFalse();

    }

    @Test
    public void shouldValue() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.values())
                .hasSize(3)
                .contains(1, 2, 4);
    }


}