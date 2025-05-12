package com.travel.constant;

/** A common status for a post, used in guide post.
 *  When the guide decide a request from a user on a guide tour post,
 *  It'll turn into close. */
public enum PostStatus {
    ACTIVE,
    RESERVED,
    DELETED,
    BLOCKED
}
