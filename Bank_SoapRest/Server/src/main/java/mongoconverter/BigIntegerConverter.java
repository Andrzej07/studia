package mongoconverter;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;

import java.math.BigInteger;

/**
 * Converter used to save BigInteger as String in MongoDB.
 */
public class BigIntegerConverter extends TypeConverter implements
        SimpleValueConverter {

    public BigIntegerConverter() {
        super(BigInteger.class);
    }

    @Override
    protected boolean isSupported(Class<?> c, MappedField optionalExtraInfo) {
        return BigInteger.class.isAssignableFrom(c);
    }

    @Override
    public Object encode(Object value, MappedField optionalExtraInfo) {
        if(value == null)
            return null;
        return value.toString();
    }

    @Override
    public Object decode(Class targetClass, Object fromDBObject, MappedField optionalExtraInfo) throws MappingException {
        if (fromDBObject == null) return null;
        return new BigInteger(fromDBObject.toString());
    }

}