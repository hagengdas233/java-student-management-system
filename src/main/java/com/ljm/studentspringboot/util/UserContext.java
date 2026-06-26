package com.ljm.studentspringboot.util;

public class UserContext {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUser(Long userId, String username) {
        USER_HOLDER.set(new UserInfo(userId, username));
    }

    public static Long getUserId() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo == null ? null : userInfo.userId();
    }

    public static String getUsername() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo == null ? null : userInfo.username();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }

    private record UserInfo(Long userId, String username) {
    }
}
