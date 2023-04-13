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


import jakarta.annotation.Priority;
import jakarta.data.exceptions.MappingException;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import one.microstream.concurrency.XThreads;
import one.microstream.persistence.types.Storer;
import one.microstream.storage.types.StorageManager;

import java.util.logging.Level;
import java.util.logging.Logger;

@Transaction
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
class TransactionInterceptor {

    private static final Logger LOGGER = Logger.getLogger(TransactionInterceptor.class.getName());

    @Inject
    private StorageManager manager;

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {

        LOGGER.log(Level.FINEST, "Executing a transaction at the method: " + context.getMethod());

        Object result = XThreads.executeSynchronized(() -> {
            Object proceed = proceed(context);
            final Object root = manager.root();
            final Storer storer = manager.createEagerStorer();
            final long storeId = storer.store(root);
            LOGGER.log(Level.WARNING, "Store the root: " + storeId);
            storer.commit();
            return proceed;
        });

        return result;
    }

    private static Object proceed(InvocationContext context) {
        try {
            return context.proceed();
        } catch (Exception extension) {
            throw new MappingException("There is an issue at the Microstream interceptor", extension);
        }

    }
}
