/*
 *  Copyright (c) 2023 Otavio Santana
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

import java.time.Year;

public class BookBuilder {
    private String isbn;
    private String title;
    private Integer edition;
    private Year release;
    private String author;

    private boolean active;

    public BookBuilder isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public BookBuilder title(String title) {
        this.title = title;
        return this;
    }

    public BookBuilder edition(Integer edition) {
        this.edition = edition;
        return this;
    }

    public BookBuilder release(Year release) {
        this.release = release;
        return this;
    }

    public BookBuilder author(String author) {
        this.author = author;
        return this;
    }

    public BookBuilder active() {
        this.active = true;
        return this;
    }

    public Book build() {
        return new Book(isbn, title, edition, release, author, active);
    }
}