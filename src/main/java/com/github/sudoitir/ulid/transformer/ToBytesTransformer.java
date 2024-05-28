package com.github.sudoitir.ulid.transformer;

import com.github.sudoitir.ulid.ULID;
import com.github.sudoitir.ulid.hibernate.ULIDType;
import org.hibernate.internal.util.BytesHelper;

public class ToBytesTransformer implements ValueTransformer {
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

    private static class SingletonHelper {
        private static final ToBytesTransformer INSTANCE = new ToBytesTransformer();
    }

    public static ToBytesTransformer getInstance() {
        return ToBytesTransformer.SingletonHelper.INSTANCE;
    }

    private ToBytesTransformer() {
    }
}
