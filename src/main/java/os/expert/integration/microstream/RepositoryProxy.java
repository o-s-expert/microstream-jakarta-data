package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.PageableRepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class RepositoryProxy<T, K> implements InvocationHandler {

    private PageableRepository<T, K> repository;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RepositoryType type = RepositoryType.of(method);
        switch (type) {
            case DEFAULT:
                return method.invoke(repository, args);
            case OBJECT_METHOD:
                return method.invoke(this, args);
            case ORDER_BY:
            case FIND_BY:
            case COUNT_BY:
            case EXISTS_BY:
            case FIND_ALL:
            case DELETE_BY:
            case QUERY:
            default:
                throw new MappingException("There is not support for microstream for feature of the type: " + type);
        }
    }
}
