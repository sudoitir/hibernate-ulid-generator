package com.github.sudoitir.ulid.transformer;

import com.github.sudoitir.ulid.ULID;
import com.github.sudoitir.ulid.hibernate.ULIDType;

public class PassThroughTransformer implements ValueTransformer {

    public ULID transform(ULID ulid) {
        return ulid;
    }

    public ULID parse(Object value) {
        return (ULID) value;
    }

    private static class SingletonHelper {
        private static final PassThroughTransformer INSTANCE = new PassThroughTransformer();
    }

    public static PassThroughTransformer getInstance() {
        return PassThroughTransformer.SingletonHelper.INSTANCE;
    }

    private PassThroughTransformer() {
    }
}
