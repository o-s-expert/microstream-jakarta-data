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

import java.util.Set;
import java.util.stream.Collectors;

class ClassScannerTest {

    private ClassScanner scanner;

    @BeforeEach
    public void setUp() {
        this.scanner = ClassScanner.INSTANCE;
    }

    @Test
    public void shouldReturnEntity() {
        Assertions.assertThat(this.scanner.entities()).hasSize(1)
                .contains(Book.class);
    }

    @Test
    public void shouldReturnRepository() {
        Assertions.assertThat(this.scanner.repositories()).hasSize(1)
                .contains(Library.class);
    }

}