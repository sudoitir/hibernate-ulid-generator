package ir.sudoit.ulid.hibernate.generator;

import ir.sudoit.ulid.ULID;
import jakarta.persistence.Id;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UlidGenerator implements IdentifierGenerator {

    private final ConcurrentMap<Class<?>, Field> idFieldCache = new ConcurrentHashMap<>();

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        Class<?> entityClass = object.getClass();
        Field idField = getIdField(entityClass);

        if (idField != null) {
            return generateValue(idField.getType());
        } else {
            throw new HibernateException("No ID field found in the entity class " + entityClass.getName());
        }
    }

    private Field getIdField(Class<?> entityClass) {
        return idFieldCache.computeIfAbsent(entityClass, this::findIdField);
    }

    private Field findIdField(Class<?> entityClass) {
        // Check superclasses first
        Field idField = findIdFieldInClassHierarchy(entityClass.getSuperclass());

        // Check current class if not found in superclasses
        if (idField == null) {
            idField = findIdFieldInClass(entityClass);
        }

        return idField;
    }

    private Field findIdFieldInClassHierarchy(Class<?> cls) {
        while (cls != null) {
            Field idField = findIdFieldInClass(cls);
            if (idField != null) {
                return idField;
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    private Field findIdFieldInClass(Class<?> cls) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        return null;
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
