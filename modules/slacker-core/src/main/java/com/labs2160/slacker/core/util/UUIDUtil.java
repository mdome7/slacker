package com.labs2160.slacker.core.util;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility to generate 24-character GUIDs from
 * the original Java GUIDs.
 */
public class UUIDUtil {

    private UUIDUtil() {}

    /**
     * Generates a random base64-encoded 22-character UUID.
     * @return
     */
    public static String generateRandomUUID() {
        return uuidToBase64(UUID.randomUUID());
    }

    public static String uuidToBase64(String str) {
        return uuidToBase64(UUID.fromString(str));
    }

    public static String uuidToBase64(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Base64.encodeBase64URLSafeString(bb.array());
    }

    public static String uuidFromBase64(String str) {
        byte [] bytes = Base64.decodeBase64(str);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());
        return uuid.toString();
    }
}
