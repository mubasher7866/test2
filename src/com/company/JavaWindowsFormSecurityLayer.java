package com.company;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JavaWindowsFormSecurityLayer {

    public static String encryptPassword(String str) {

        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }

        m.update(str.getBytes(), 0, str.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }
}

