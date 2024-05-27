package ir.sudoit.ulid;

import java.io.Serial;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public final class ULID implements Comparable<ULID>, Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -3563159514112487717L;

    /*
     * The most significant 64 bits of this ULID.
     *
     * @serial
     */
    private final long mostSignificantBits;

    /*
     * The least significant 64 bits of this ULID.
     *
     * @serial
     */
    private final long leastSignificantBits;

    private static final char[] ENCODING_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
            'Y', 'Z',
    };

    private static final byte[] DECODING_CHARS = {
            // 0
            -1, -1, -1, -1, -1, -1, -1, -1,
            // 8
            -1, -1, -1, -1, -1, -1, -1, -1,
            // 16
            -1, -1, -1, -1, -1, -1, -1, -1,
            // 24
            -1, -1, -1, -1, -1, -1, -1, -1,
            // 32
            -1, -1, -1, -1, -1, -1, -1, -1,
            // 40
            -1, -1, -1, -1, -1, -1, -1, -1,
            // 48
            0, 1, 2, 3, 4, 5, 6, 7,
            // 56
            8, 9, -1, -1, -1, -1, -1, -1,
            // 64
            -1, 10, 11, 12, 13, 14, 15, 16,
            // 72
            17, 1, 18, 19, 1, 20, 21, 0,
            // 80
            22, 23, 24, 25, 26, -1, 27, 28,
            // 88
            29, 30, 31, -1, -1, -1, -1, -1,
            // 96
            -1, 10, 11, 12, 13, 14, 15, 16,
            // 104
            17, 1, 18, 19, 1, 20, 21, 0,
            // 112
            22, 23, 24, 25, 26, -1, 27, 28,
            // 120
            29, 30, 31,
    };

    private static final int MASK = 0x1F;
    private static final int MASK_BITS = 5;
    private static final long TIMESTAMP_OVERFLOW_MASK = 0xFFFF_0000_0000_0000L;
    private static final long TIMESTAMP_MSB_MASK = 0xFFFF_FFFF_FFFF_0000L;
    private static final long RANDOM_MSB_MASK = 0xFFFFL;

    /*
     * The random number generator used by this class to create
     * ULIDs. In a holder class to defer initialization until needed.
     */
    private static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    private ULID() {
        throw new IllegalArgumentException();
    }

    /**
     * @param mostSignificantBits  The most significant 64 bits of this ULID.
     * @param leastSignificantBits The least significant 64 bits of this ULID.
     */
    public ULID(long mostSignificantBits, long leastSignificantBits) {
        this.mostSignificantBits = mostSignificantBits;
        this.leastSignificantBits = leastSignificantBits;
    }

    // Constructors and Factories
    public static ULID randomULID() {
        return randomULID(System.currentTimeMillis());
    }

    public static ULID randomULID(long timestamp) {
        SecureRandom random = Holder.numberGenerator;

        checkTimestamp(timestamp);
        // could use nextBytes(byte[] bytes) instead
        long mostSignificantBits = random.nextLong();
        long leastSignificantBits = random.nextLong();
        mostSignificantBits &= 0xFFFF;
        mostSignificantBits |= (timestamp << 16);
        return new ULID(mostSignificantBits, leastSignificantBits);
    }

    public static ULID fromBytes(byte[] data) {
        Objects.requireNonNull(data, "data must not be null!");
        if (data.length != 16) {
            throw new IllegalArgumentException("data must be 16 bytes in length!");
        }
        long mostSignificantBits = 0;
        long leastSignificantBits = 0;
        for (int i = 0; i < 8; i++) {
            mostSignificantBits = (mostSignificantBits << 8) | (data[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            leastSignificantBits = (leastSignificantBits << 8) | (data[i] & 0xff);
        }
        return new ULID(mostSignificantBits, leastSignificantBits);
    }


    public ULID increment() {
        long lsb = leastSignificantBits;
        if (lsb != 0xFFFF_FFFF_FFFF_FFFFL) {
            return new ULID(mostSignificantBits, lsb + 1);
        }
        long msb = mostSignificantBits;
        if ((msb & RANDOM_MSB_MASK) != RANDOM_MSB_MASK) {
            return new ULID(msb + 1, 0);
        }
        return new ULID(msb & TIMESTAMP_MSB_MASK, 0);
    }


    /**
     * Returns the next monotonic value. If an overflow happened while incrementing the random part of the given
     * previous ULID value then the returned value will have a zero random part.
     *
     * @param previousUlid the previous ULID value.
     * @return the next monotonic value.
     */
    public ULID nextMonotonicValue(ULID previousUlid) {
        return nextMonotonicValue(previousUlid, System.currentTimeMillis());
    }

    /**
     * Returns the next monotonic value. If an overflow happened while incrementing the random part of the given
     * previous ULID value then the returned value will have a zero random part.
     *
     * @param previousUlid the previous ULID value.
     * @param timestamp    the timestamp of the next ULID value.
     * @return the next monotonic value.
     */
    public static ULID nextMonotonicValue(ULID previousUlid, long timestamp) {
        Objects.requireNonNull(previousUlid, "previousUlid must not be null!");
        if (previousUlid.timestamp() == timestamp) {
            return previousUlid.increment();
        }
        return randomULID(timestamp);
    }


    public static ULID parseULID(String ulidString) {
        Objects.requireNonNull(ulidString, "ulidString must not be null!");
        if (ulidString.length() != 26) {
            throw new IllegalArgumentException("ulidString must be exactly 26 chars long.");
        }

        String timeString = ulidString.substring(0, 10);
        long time = internalParseCrockford(timeString);
        if ((time & TIMESTAMP_OVERFLOW_MASK) != 0) {
            throw new IllegalArgumentException("ulidString must not exceed '7ZZZZZZZZZZZZZZZZZZZZZZZZZ'!");
        }
        String part1String = ulidString.substring(10, 18);
        String part2String = ulidString.substring(18);
        long part1 = internalParseCrockford(part1String);
        long part2 = internalParseCrockford(part2String);

        long most = (time << 16) | (part1 >>> 24);
        long least = part2 | (part1 << 40);
        return new ULID(most, least);
    }

    /**
     * Returns the most significant 64 bits of this ULID's 128 bit value.
     *
     * @return The most significant 64 bits of this ULID's 128 bit value
     */
    public long mostSignificantBits() {
        return mostSignificantBits;
    }

    /**
     * Returns the least significant 64 bits of this ULID's 128 bit value.
     *
     * @return The least significant 64 bits of this ULID's 128 bit value
     */
    public long leastSignificantBits() {
        return leastSignificantBits;
    }


    public long timestamp() {
        return mostSignificantBits >>> 16;
    }


    private static long internalParseCrockford(String input) {
        Objects.requireNonNull(input, "input must not be null!");
        int length = input.length();
        if (length > 12) {
            throw new IllegalArgumentException("input length must not exceed 12 but was " + length + "!");
        }

        long result = 0;
        for (int i = 0; i < length; i++) {
            char current = input.charAt(i);
            byte value = -1;
            if (current < DECODING_CHARS.length) {
                value = DECODING_CHARS[current];
            }
            if (value < 0) {
                throw new IllegalArgumentException("Illegal character '" + current + "'!");
            }
            result |= ((long) value) << ((length - 1 - i) * MASK_BITS);
        }
        return result;
    }

    /*
     * http://crockford.com/wrmg/base32.html
     */
    private static void internalWriteCrockford(char[] buffer, long value, int count, int offset) {
        for (int i = 0; i < count; i++) {
            int index = (int) ((value >>> ((count - i - 1) * MASK_BITS)) & MASK);
            buffer[offset + i] = ENCODING_CHARS[index];
        }
    }

    private static void checkTimestamp(long timestamp) {
        if ((timestamp & TIMESTAMP_OVERFLOW_MASK) != 0) {
            throw new IllegalArgumentException("ULID does not support timestamps after +10889-08-02T05:31:50.655Z!");
        }
    }

    @Override
    public String toString() {
        char[] buffer = new char[26];

        internalWriteCrockford(buffer, timestamp(), 10, 0);
        long value = ((mostSignificantBits & 0xFFFFL) << 24);
        long interim = (leastSignificantBits >>> 40);
        value = value | interim;
        internalWriteCrockford(buffer, value, 8, 10);
        internalWriteCrockford(buffer, leastSignificantBits, 8, 18);

        return new String(buffer);
    }

    public byte[] toBytes() {
        byte[] result = new byte[16];
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) ((mostSignificantBits >> ((7 - i) * 8)) & 0xFF);
        }
        for (int i = 8; i < 16; i++) {
            result[i] = (byte) ((leastSignificantBits >> ((15 - i) * 8)) & 0xFF);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ULID) obj;
        return this.mostSignificantBits == that.mostSignificantBits &&
                this.leastSignificantBits == that.leastSignificantBits;
    }

    @Override
    public int hashCode() {
        long hilo = mostSignificantBits ^ leastSignificantBits;
        return ((int) (hilo >> 32)) ^ (int) hilo;
    }

    @Override
    public int compareTo(ULID val) {
        int mostSigBits = Long.compare(this.mostSignificantBits, val.mostSignificantBits);
        return mostSigBits != 0 ? mostSigBits : Long.compare(this.leastSignificantBits, val.leastSignificantBits);
    }

    @Override
    public Object clone() {
        return new ULID(mostSignificantBits, leastSignificantBits);
    }
}
