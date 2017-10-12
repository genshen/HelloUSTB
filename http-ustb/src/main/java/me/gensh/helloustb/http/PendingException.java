package me.gensh.helloustb.http;

import java.io.IOException;

/**
 * Created by gensh on 2017/10/12.
 */

public class PendingException extends IOException {

    public PendingException(String message) {
        super(message);
    }
}
