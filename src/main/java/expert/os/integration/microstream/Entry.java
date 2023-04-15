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

import java.util.Objects;

/**
 * A structure that represents the {@link java.util.Map.Entry}
 *
 * @param key   the   {@link java.util.Map.Entry#getKey()}
 * @param value {@link java.util.Map.Entry#getValue()} ()}
 */
record Entry(Object key, Object value) {

    public Entry {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");
    }

    public static Entry of(Object key, Object value) {
        return new Entry(key, value);
    }
}
