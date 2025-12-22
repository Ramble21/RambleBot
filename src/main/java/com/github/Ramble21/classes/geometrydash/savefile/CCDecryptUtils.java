package com.github.Ramble21.classes.geometrydash.savefile;

import net.dv8tion.jda.api.entities.Message;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CCDecryptUtils {

    // https://wyliemaster.github.io/gddocs/#/topics/localfiles_encrypt_decrypt

    private static final byte[] KEY = new byte[] {
            (byte)0x69, (byte)0x70, (byte)0x75, (byte)0x39, (byte)0x54, (byte)0x55, (byte)0x76, (byte)0x35,
            (byte)0x34, (byte)0x79, (byte)0x76, (byte)0x5d, (byte)0x69, (byte)0x73, (byte)0x46, (byte)0x4d,
            (byte)0x68, (byte)0x35, (byte)0x40, (byte)0x3b, (byte)0x74, (byte)0x2e, (byte)0x35, (byte)0x77,
            (byte)0x33, (byte)0x34, (byte)0x45, (byte)0x32, (byte)0x52, (byte)0x79, (byte)0x40, (byte)0x7b
    };

    public static String decryptFile(Message.Attachment saveFile) throws Exception {
        InputStream stream = saveFile.getProxy().download().get();
        byte[] data = stream.readAllBytes();
        stream.close();

        if (isWindowsEncrypted(data)) {
            return decryptData(data);
        } else {
            return macDecrypt(data);
        }
    }

    public static boolean isWindowsEncrypted(byte[] data) {
        try {
            String xorResult = xor(new String(data, StandardCharsets.ISO_8859_1), 11);
            byte[] decoded = java.util.Base64.getUrlDecoder().decode(xorResult.trim());
            return decoded.length >= 2 &&
                    decoded[0] == (byte)0x1f &&
                    decoded[1] == (byte)0x8b;
        } catch (Exception e) {
            return false;
        }
    }


    public static String xor(String string, int key) {
        StringBuilder result = new StringBuilder();
        for (char c : string.toCharArray()) {
            result.append((char) (c ^ key));
        }
        return result.toString();
    }

    public static byte[] xorBytes(byte[] data, int key) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key);
        }
        return result;
    }

    public static String decryptData(byte[] data) throws Exception {
        byte[] xorResult = xorBytes(data, 11);

        String base64String = new String(xorResult, StandardCharsets.ISO_8859_1);
        String cleaned = base64String.replaceAll("\\s+", "");

        String normalized = cleaned.replace('-', '+').replace('_', '/');

        byte[] base64Decoded = java.util.Base64.getMimeDecoder().decode(normalized);

        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(base64Decoded);
        java.util.zip.GZIPInputStream gis = new java.util.zip.GZIPInputStream(bais);
        byte[] decompressed = gis.readAllBytes();
        gis.close();

        return new String(decompressed, StandardCharsets.UTF_8);
    }

    public static byte[] removePad(byte[] data) {
        int last = data[data.length - 1] & 0xFF;
        if (last < 16) {
            byte[] result = new byte[data.length - last];
            System.arraycopy(data, 0, result, 0, data.length - last);
            return result;
        }
        return data;
    }

    public static String macDecrypt(byte[] data) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/ECB/NoPadding");
        javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(KEY, "AES");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);

        byte[] decrypted = cipher.doFinal(data);
        byte[] unpadded = removePad(decrypted);

        return new String(unpadded, java.nio.charset.StandardCharsets.UTF_8);
    }
}