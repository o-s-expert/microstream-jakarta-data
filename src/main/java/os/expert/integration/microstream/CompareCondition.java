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

import jakarta.data.exceptions.MappingException;

import java.util.function.Predicate;


enum CompareCondition {

    COMPARABLE {
        @Override
        <T> Predicate<T> gt(Object param, FieldMetadata field) {
            return t -> ((Comparable) param).compareTo(checkTypes(field, t, param))  <= -1;
        }

        @Override
        <T> Predicate<T> lt(Object param, FieldMetadata field) {
            return t -> ((Comparable) param).compareTo(checkTypes(field, t, param))>= 1;
        }

        @Override
        <T> Predicate<T> gte(Object param, FieldMetadata field) {
            return t -> ((Comparable) param).compareTo(checkTypes(field, t, param))<= 0;
        }

        @Override
        <T> Predicate<T> lte(Object param, FieldMetadata field) {
            return t -> ((Comparable) param).compareTo(checkTypes(field, t, param))  >= 0;
        }
    };


    abstract <T> Predicate<T> gt(Object param, FieldMetadata field);

    abstract <T> Predicate<T> lt(Object param, FieldMetadata field);

    abstract <T> Predicate<T> gte(Object param, FieldMetadata field);

    abstract <T> Predicate<T> lte(Object param, FieldMetadata field);

    private static <T> Object checkTypes(FieldMetadata field, T entity, Object param) {
        Object value = field.get(entity);
        if (param.getClass().equals(value.getClass())) {
            return value;
        }
        throw new MappingException("The types are no compatible between the field: " + field.field() +
                " and the param: " + param);
    }

    static CompareCondition of(Class<?> type) {
        if (Comparable.class.isAssignableFrom(type)) {
            return COMPARABLE;
        }
        throw new UnsupportedOperationException("There is not support to the type: " + type + " to execute comparable" +
                " predicates, such as lesser, greater");
    }

}
