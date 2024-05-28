package com.github.sudoitir.ulid.transformer;

import com.github.sudoitir.ulid.ULID;
import com.github.sudoitir.ulid.hibernate.ULIDType;

public class ToStringTransformer implements ValueTransformer {

    public String transform(ULID ulid) {
        return ulid.toString();
    }

    public ULID parse(Object value) {
        return ULID.parseULID((String) value);
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
