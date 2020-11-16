package com.daicy.redis.protocal;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Nested replies.
 * <p/>
 * User: sam
 * Date: 7/29/11
 * Time: 10:23 AM
 */
public class MultiBulkReply implements Reply<List<Reply>> {
    public static final char MARKER = ReplyConstants.ASTERISK;
    private final List<Reply> replies;


    public MultiBulkReply(List<Reply> replies) {
        this.replies = replies;
    }

    @Override
    public List<Reply> data() {
        return replies;
    }


    public List<String> asStringList(Charset charset) {
        if (replies == null) return null;
        List<String> strings = new ArrayList<String>(replies.size());
        for (Reply reply : replies) {
            if (reply instanceof BulkReply) {
                strings.add(((BulkReply) reply).asString(charset));
            } else if (reply instanceof IntegerReply) {
                strings.add(data().toString());
            } else {
                throw new IllegalArgumentException("Could not convert " + reply + " to a string");
            }
        }
        return strings;
    }
}
