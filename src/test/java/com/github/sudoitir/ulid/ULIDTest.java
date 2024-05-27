package com.github.sudoitir.ulid;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ULIDTest {

    @RepeatedTest(500)
    void testRandomULIDNotNull() {
        ULID ulid = ULID.randomULID();
        assertNotNull(ulid);
    }

    @RepeatedTest(500)
    void testRandomULIDWithTimestamp() {
        long timestamp = System.currentTimeMillis();
        ULID ulid = ULID.randomULID(timestamp);
        long mostSignificantBits = ulid.mostSignificantBits();

        long extractedTimestamp = (mostSignificantBits >>> 16);
        assertEquals(timestamp, extractedTimestamp);
    }

    @RepeatedTest(500)
    void testRandomnessAndUniqueness() {
        ULID ulid1 = ULID.randomULID();
        ULID ulid2 = ULID.randomULID();
        assertNotEquals(ulid1, ulid2);
    }

    @Test
    void testMultithreadingCreation() throws InterruptedException {
        int numberOfThreads = 100;
        try (ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads)) {
            Set<ULID> ulidSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    ULID ulid = ULID.randomULID();
                    assertNotNull(ulid);
                    ulidSet.add(ulid);
                    latch.countDown();
                });
            }
            latch.await();
            assertEquals(numberOfThreads, ulidSet.size());
        }
    }

    @Test
    void testFromBytesValidData() {
        byte[] data = new byte[16];
        new SecureRandom().nextBytes(data);
        ULID ulid = ULID.fromBytes(data);
        assertNotNull(ulid);
    }


    // Test increment method for a non-overflow scenario
    @Test
    void testIncrementNonOverflow() {
        ULID ulid = new ULID(0L, 0L);
        ULID incremented = ulid.increment();
        assertEquals(1L, incremented.leastSignificantBits());
    }

    // Test increment method for an overflow scenario
    @Test
    void testIncrementOverflow() {
        ULID ulid = new ULID(0L, 0xFFFF_FFFF_FFFF_FFFFL);
        ULID incremented = ulid.increment();
        assertEquals(1L, incremented.mostSignificantBits());
        assertEquals(0L, incremented.leastSignificantBits());
    }

    // Test nextMonotonicValue method with same timestamp
    @Test
    void testNextMonotonicValueSameTimestamp() {
        ULID ulid = new ULID(0L, 0L);
        ULID next = ULID.nextMonotonicValue(ulid, ulid.timestamp());
        assertEquals(ulid.increment(), next);
    }

    // Test nextMonotonicValue method with different timestamp
    @Test
    void testNextMonotonicValueDifferentTimestamp() {
        ULID ulid = new ULID(0L, 0L);
        long newTimestamp = ulid.timestamp() + 1;
        ULID next = ULID.nextMonotonicValue(ulid, newTimestamp);
        assertEquals(newTimestamp, next.timestamp());
    }


    @Test
    void testParseULIDValidString() {
        String ulidString = "00000000000000000000000000";
        ULID ulid = ULID.parseULID(ulidString);
        assertNotNull(ulid);
    }

    @Test
    void testFromBytesNullData() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> ULID.fromBytes(null),
                "Expected fromBytes to throw, but it didn't"
        );
        assertTrue(exception.getMessage().contains("data must not be null"));
    }

    @Test
    void testFromBytesInvalidDataLength() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ULID.fromBytes(new byte[15]), // Invalid data length
                "Expected fromBytes to throw, but it didn't"
        );
        assertTrue(exception.getMessage().contains("data must be 16 bytes in length"));
    }

    @Test
    void testParseULIDNullString() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> ULID.parseULID(null),
                "Expected parseULID to throw, but it didn't"
        );
        assertTrue(exception.getMessage().contains("ulidString must not be null"));
    }

    @Test
    void testParseULIDInvalidStringLength() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ULID.parseULID("0000000000000000000000000"),
                "Expected parseULID to throw, but it didn't"
        );
        assertTrue(exception.getMessage().contains("ulidString must be exactly 26 chars long"));
    }

    // Test toString method
    @Test
    void testToString() {
        ULID ulid = new ULID(0L, 0L);
        String ulidString = ulid.toString();
        assertNotNull(ulidString, "ULID string should not be null");
        assertEquals(26, ulidString.length(), "ULID string should be 26 characters long");
    }

    // Test toBytes method
    @Test
    void testToBytes() {
        ULID ulid = new ULID(0L, 0L);
        byte[] bytes = ulid.toBytes();
        assertNotNull(bytes, "ULID bytes should not be null");
        assertEquals(16, bytes.length, "ULID bytes should be 16 bytes long");
    }

    // Test equals method
    @Test
    void testEquals() {
        ULID ulid1 = new ULID(0L, 0L);
        ULID ulid2 = new ULID(0L, 0L);
        ULID ulid3 = new ULID(1L, 0L);
        assertEquals(ulid1, ulid2, "ULIDs with same values should be equal");
        assertNotEquals(ulid1, ulid3, "ULIDs with different values should not be equal");
    }

    // Test hashCode method
    @Test
    void testHashCode() {
        ULID ulid = new ULID(0L, 0L);
        int hashCode = ulid.hashCode();
        assertEquals(hashCode, ulid.hashCode(), "Hash code should be consistent");
    }

    // Test compareTo method
    @Test
    void testCompareTo() {
        ULID ulid1 = new ULID(0L, 0L);
        ULID ulid2 = new ULID(0L, 0L);
        ULID ulid3 = new ULID(1L, 0L);
        assertEquals(0, ulid1.compareTo(ulid2), "ULIDs with same values should be equal");
        assertTrue(ulid1.compareTo(ulid3) < 0, "ULID1 should be less than ULID3");
    }

    // Test clone method
    @Test
    void testClone() {
        ULID ulid = new ULID(0L, 0L);
        ULID clonedUlid = (ULID) ulid.copy();
        assertEquals(ulid, clonedUlid, "Cloned ULID should be equal to the original");
        assertNotSame(ulid, clonedUlid, "Cloned ULID should not be the same object as the original");
    }

    @Test
    void testPrivateConstructorAccess() {
        assertThrows(InvocationTargetException.class, () -> {
            Constructor<ULID> constructor = ULID.class.getDeclaredConstructor();
            constructor.setAccessible(true); // Make the constructor accessible
            try {
                constructor.newInstance(); // This should throw IllegalArgumentException
            } finally {
                constructor.setAccessible(false); // Set it back to private
            }
        });
    }

    @Test
    void testParseULIDThrowNull() {
        assertThrows(NullPointerException.class, () -> ULID.parseULID(null));
    }

    @Test
    void testParseULIDThrowInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> ULID.parseULID("12345"));
    }


}


