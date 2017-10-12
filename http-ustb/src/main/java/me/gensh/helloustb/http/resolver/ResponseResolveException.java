package me.gensh.helloustb.http.resolver;

import java.io.IOException;

/**
 * Created by gensh on 2017/10/12.
 */

public class ResponseResolveException extends IOException {

    public ResponseResolveException(String message) {
        super(message);
    }
}
