package ir.sudoit.ulid.hibernate;

import ir.sudoit.ulid.ULID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.BytesHelper;
import org.hibernate.usertype.EnhancedUserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static java.sql.Types.VARCHAR;

public class ULIDType implements EnhancedUserType<ULID> {

    @Override
    public int getSqlType() {
        return VARCHAR;
    }

    @Override
    public Class<ULID> returnedClass() {
        return ULID.class;
    }

    @Override
    public boolean equals(ULID x, ULID y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(ULID x) {
        return x.hashCode();
    }

    @Override
    public ULID nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String columnValue = rs.getString(position);
        return rs.wasNull() ? null : ULIDType.ToStringTransformer.INSTANCE.parse(columnValue);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, ULID value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, VARCHAR);
        } else {
            st.setString(index, ULIDType.ToStringTransformer.INSTANCE.transform(value));
        }
    }

    @Override
    public ULID deepCopy(ULID value) {
        return value == null ? null : (ULID) value.clone();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(ULID value) {
        return deepCopy(value);
    }

    @Override
    public ULID assemble(Serializable cached, Object owner) {
        return deepCopy((ULID) cached);
    }

    @Override
    public String toSqlLiteral(ULID value) {
        return "'" + value.toString() + "'";
    }

    @Override
    public String toString(ULID value) throws HibernateException {
        return value.toString();
    }

    @Override
    public ULID fromStringValue(CharSequence sequence) throws HibernateException {
        return ULID.parseULID(sequence.toString());
    }

    public interface ValueTransformer {
        Serializable transform(ULID ulid);

        ULID parse(Object value);
    }

    public static class PassThroughTransformer implements ULIDType.ValueTransformer {
        public static final ULIDType.PassThroughTransformer INSTANCE = new ULIDType.PassThroughTransformer();

        public ULID transform(ULID ulid) {
            return ulid;
        }

        public ULID parse(Object value) {
            return (ULID) value;
        }

        private PassThroughTransformer() {
        }
    }

    public static class ToStringTransformer implements ULIDType.ValueTransformer {
        public static final ULIDType.ToStringTransformer INSTANCE = new ULIDType.ToStringTransformer();

        public String transform(ULID ulid) {
            return ulid.toString();
        }

        public ULID parse(Object value) {
            return ULID.parseULID((String) value);
        }

        private ToStringTransformer() {
        }
    }

    public static class ToBytesTransformer implements ULIDType.ValueTransformer {
        public static final ULIDType.ToBytesTransformer INSTANCE = new ULIDType.ToBytesTransformer();

        public byte[] transform(ULID ulid) {
            byte[] bytes = new byte[16];
            BytesHelper.fromLong(ulid.mostSignificantBits(), bytes, 0);
            BytesHelper.fromLong(ulid.leastSignificantBits(), bytes, 8);
            return bytes;
        }

        public ULID parse(Object value) {
            byte[] bytea = (byte[]) value;
            return new ULID(BytesHelper.asLong(bytea, 0), BytesHelper.asLong(bytea, 8));
        }

        private ToBytesTransformer() {
        }
    }

}