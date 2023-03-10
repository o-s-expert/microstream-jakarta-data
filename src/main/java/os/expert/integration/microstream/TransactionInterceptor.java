package os.expert.integration.microstream;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import one.microstream.storage.types.StorageManager;

@Transaction
@Interceptor
@ApplicationScoped
class TransactionInterceptor {

    @Inject
    private StorageManager manager;

    @AroundInvoke
    public Object execute(InvocationContext invocationContext) throws Exception {
        Object proceed = invocationContext.proceed();
        manager.storeRoot();
        return proceed;
    }
}
