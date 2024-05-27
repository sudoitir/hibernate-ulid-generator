package ir.sudoit.ulid;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

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
    public void testRandomULIDNotNull() {
        ULID ulid = ULID.randomULID();
        assertNotNull(ulid);
    }

    @RepeatedTest(500)
    public void testRandomULIDWithTimestamp() {
        long timestamp = System.currentTimeMillis();
        ULID ulid = ULID.randomULID(timestamp);
        long mostSignificantBits = ulid.mostSignificantBits();

        long extractedTimestamp = (mostSignificantBits >>> 16);
        assertEquals(timestamp, extractedTimestamp);
    }

    @RepeatedTest(500)
    public void testRandomnessAndUniqueness() {
        ULID ulid1 = ULID.randomULID();
        ULID ulid2 = ULID.randomULID();
        assertNotEquals(ulid1, ulid2);
    }

    @Test
    public void testMultithreadingCreation() throws InterruptedException {
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
    public void testFromBytesValidData() {
        byte[] data = new byte[16];
        new SecureRandom().nextBytes(data);
        ULID ulid = ULID.fromBytes(data);
        assertNotNull(ulid);
    }


    // Test increment method for a non-overflow scenario
    @Test
    public void testIncrementNonOverflow() {
        ULID ulid = new ULID(0L, 0L);
        ULID incremented = ulid.increment();
        assertEquals(1L, incremented.leastSignificantBits());
    }

    // Test increment method for an overflow scenario
    @Test
    public void testIncrementOverflow() {
        ULID ulid = new ULID(0L, 0xFFFF_FFFF_FFFF_FFFFL);
        ULID incremented = ulid.increment();
        assertEquals(1L, incremented.mostSignificantBits());
        assertEquals(0L, incremented.leastSignificantBits());
    }

    // Test nextMonotonicValue method with same timestamp
    @Test
    public void testNextMonotonicValueSameTimestamp() {
        ULID ulid = new ULID(0L, 0L);
        ULID next = ULID.nextMonotonicValue(ulid, ulid.timestamp());
        assertEquals(ulid.increment(), next);
    }

    // Test nextMonotonicValue method with different timestamp
    @Test
    public void testNextMonotonicValueDifferentTimestamp() {
        ULID ulid = new ULID(0L, 0L);
        long newTimestamp = ulid.timestamp() + 1;
        ULID next = ULID.nextMonotonicValue(ulid, newTimestamp);
        assertEquals(newTimestamp, next.timestamp());
    }


    @Test
    public void testParseULIDValidString() {
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
                () -> ULID.parseULID("0000000000000000000000000"), // One char short
                "Expected parseULID to throw, but it didn't"
        );
        assertTrue(exception.getMessage().contains("ulidString must be exactly 26 chars long"));
    }

}


