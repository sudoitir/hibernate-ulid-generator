package com.github.sudoitir.ulid.hibernate;

import static java.sql.Types.VARCHAR;

import com.github.sudoitir.ulid.ULID;
import com.github.sudoitir.ulid.transformer.ToStringTransformer;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.EnhancedUserType;

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
    public ULID nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session,
            Object owner) throws SQLException {
        String columnValue = rs.getString(position);
        return rs.wasNull() ? null : ToStringTransformer.getInstance().parse(columnValue);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, ULID value, int index,
            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, VARCHAR);
        } else {
            st.setString(index, ToStringTransformer.getInstance().transform(value));
        }
    }

    @Override
    public ULID deepCopy(ULID value) {
        return value == null ? null : (ULID) value.copy();
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

}
