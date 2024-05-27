package com.github.sudoitir.ulid.hibernate;

import com.github.sudoitir.ulid.ULID;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ULIDTypeTest {

    // Test getSqlType method
    @Test
    void testGetSqlType() {
        ULIDType ulidType = new ULIDType();
        assertEquals(java.sql.Types.VARCHAR, ulidType.getSqlType(), "SQL type should be VARCHAR");
    }

    // Test returnedClass method
    @Test
    void testReturnedClass() {
        ULIDType ulidType = new ULIDType();
        assertEquals(ULID.class, ulidType.returnedClass(), "Returned class should be ULID");
    }

    // Test equals method
    @Test
    void testEquals() {
        ULIDType ulidType = new ULIDType();
        ULID ulid1 = new ULID(0L, 0L);
        ULID ulid2 = new ULID(0L, 0L);
        ULID ulid3 = new ULID(1L, 0L);
        assertTrue(ulidType.equals(ulid1, ulid2), "ULIDs with same values should be equal");
        assertFalse(ulidType.equals(ulid1, ulid3), "ULIDs with different values should not be equal");
    }

    // Test hashCode method
    @Test
    void testHashCode() {
        ULIDType ulidType = new ULIDType();
        ULID ulid = new ULID(0L, 0L); // Example ULID
        assertEquals(ulid.hashCode(), ulidType.hashCode(ulid), "Hash code should match ULID's hash code");
    }

    // Test nullSafeGet method
    @Test
    void testNullSafeGet() throws SQLException {
        ULIDType ulidType = new ULIDType();
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(1)).thenReturn("01ARZ3NDEKTSV4RRFFQ69G5FAV");
        when(rs.wasNull()).thenReturn(false);

        ULID result = ulidType.nullSafeGet(rs, 1, null, null);
        assertNotNull(result, "Resulting ULID should not be null");
    }

    // Test nullSafeSet method
    @Test
    void testNullSafeSet() throws SQLException {
        ULIDType ulidType = new ULIDType();
        PreparedStatement st = mock(PreparedStatement.class);
        ULID ulid = new ULID(0L, 0L); // Example ULID

        ulidType.nullSafeSet(st, ulid, 1, null);
        verify(st).setString(1, ulid.toString());
    }

    // Test deepCopy method
    @Test
    void testDeepCopy() {
        ULIDType ulidType = new ULIDType();
        ULID ulid = new ULID(0L, 0L); // Example ULID
        ULID copy = ulidType.deepCopy(ulid);
        assertEquals(ulid, copy, "Deep copy should be equal to the original");
        assertNotSame(ulid, copy, "Deep copy should not be the same object as the original");
    }

    // Test isMutable method
    @Test
    void testIsMutable() {
        ULIDType ulidType = new ULIDType();
        assertFalse(ulidType.isMutable(), "ULIDType should not be mutable");
    }

    // Test disassemble method
    @Test
    void testDisassemble() {
        ULIDType ulidType = new ULIDType();
        ULID ulid = new ULID(0L, 0L); // Example ULID
        Serializable disassembled = ulidType.disassemble(ulid);
        assertEquals(ulid, disassembled, "Disassembled ULID should be equal to the original");
    }

    // Test assemble method
    @Test
    void testAssemble() {
        ULIDType ulidType = new ULIDType();
        ULID ulid = new ULID(0L, 0L); // Example ULID
        ULID assembled = ulidType.assemble(ulid, null);
        assertEquals(ulid, assembled, "Assembled ULID should be equal to the original");
    }

    // Test toSqlLiteral method
    @Test
    void testToSqlLiteral() {
        ULIDType ulidType = new ULIDType();
        ULID ulid = new ULID(0L, 0L); // Example ULID
        String sqlLiteral = ulidType.toSqlLiteral(ulid);
        assertEquals("'" + ulid.toString() + "'", sqlLiteral, "SQL literal should be correctly formatted");
    }

    // Test toString method
    @Test
    void testToString() {
        ULIDType ulidType = new ULIDType();
        ULID ulid = new ULID(0L, 0L); // Example ULID
        String string = ulidType.toString(ulid);
        assertEquals(ulid.toString(), string, "String representation should match ULID's toString");
    }

    // Test fromStringValue method
    @Test
    void testFromStringValue() {
        ULIDType ulidType = new ULIDType();
        CharSequence sequence = "01ARZ3NDEKTSV4RRFFQ69G5FAV";
        ULID ulid = ulidType.fromStringValue(sequence);
        assertNotNull(ulid, "ULID should not be null");
    }
}