package ir.sudoit.ulid.hibernate.generator;

import ir.sudoit.ulid.ULID;
import jakarta.persistence.Id;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.lang.reflect.Field;

public class UlidGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        Class<?> entityClass = object.getClass();

        Field idField = null;
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                idField = field;
                break;
            }
        }

        if (idField != null) {
            Class<?> idType = idField.getType();
            return generateValue(idType);
        } else {
            throw new HibernateException("No ID field found in the entity class");
        }
    }

    public Object generateValue(Class<?> type) {
        if (type == null) {
            return null;
        }
        if (ULID.class.isAssignableFrom(type)) {
            return ULID.randomULID();
        }
        if (String.class.isAssignableFrom(type)) {
            return ULID.randomULID().toString();
        }
        if (byte[].class.isAssignableFrom(type)) {
            return ULID.randomULID().toBytes();
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }
}
