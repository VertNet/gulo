package gulo.tap;

import backtype.hadoop.pail.PailStructure;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.util.Collections;
import java.util.List;

public abstract class ThriftPailStructure<T extends Comparable> implements PailStructure<T> {

    public T deserialize(byte[] record) {
        T ret = createThriftObject();
        try {
            getDeserializer().deserialize((TBase)ret, record);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public byte[] serialize(T obj) {
        try {
            return getSerializer().serialize((TBase)obj);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract T createThriftObject();

    private transient TSerializer ser;

    private TSerializer getSerializer() {
        if(ser==null) ser = new TSerializer();
        return ser;
    }

    private transient TDeserializer des;

    private TDeserializer getDeserializer() {
        if(des==null) des = new TDeserializer();
        return des;
    }

    public boolean isValidTarget(String... dirs) {
        return true;
    }

    public List<String> getTarget(T object) {
        return Collections.EMPTY_LIST;
    }
}
