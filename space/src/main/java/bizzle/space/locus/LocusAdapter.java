package bizzle.space.locus;

import bizzle.space.Locus;
import lombok.Getter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

@Getter
public class LocusAdapter<SOURCE, DESTINATION> {

    private final Locus<SOURCE> sourceLocus;
    private final Function<SOURCE, DESTINATION> adapterFunction;
    private final Locus<DESTINATION> adapter;
    @SuppressWarnings("unchecked")
    public LocusAdapter(Locus<SOURCE> sourceLocus, Function<SOURCE, DESTINATION> adapterFunction) {
        this.sourceLocus = sourceLocus;
        this.adapterFunction = adapterFunction;
        adapter = (Locus<DESTINATION>) Proxy.newProxyInstance(Locus.class.getClassLoader(), new Class[]{Locus.class}, new AdapterInvocationHandler());
    }

    private  final class AdapterInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            SOURCE source = (SOURCE) method.invoke(sourceLocus, args);
            return adapterFunction.apply( source);
        }
    }

}
