package com.github.sudoitir.ulid.transformer;

import com.github.sudoitir.ulid.ULID;

import java.io.Serializable;

public interface ValueTransformer {
    Serializable transform(ULID ulid);

    ULID parse(Object value);
}
