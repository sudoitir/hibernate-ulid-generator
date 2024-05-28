package com.github.sudoitir.ulid.transformer;

import com.github.sudoitir.ulid.ULID;

public class ToStringTransformer implements ValueTransformer {

    public String transform(ULID ulid) {
        return ulid.toString();
    }

    public ULID parse(Object value) {
        return ULID.from((String) value);
    }

    private static class SingletonHelper {
        private static final ToStringTransformer INSTANCE = new ToStringTransformer();
    }

    public static ToStringTransformer getInstance() {
        return ToStringTransformer.SingletonHelper.INSTANCE;
    }

    private ToStringTransformer() {
    }
}
