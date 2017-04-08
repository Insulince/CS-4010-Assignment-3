package assignment3.byteshex;

import java.util.Arrays;
import java.io.*;

public class BytesHex {
    public static void main(String[] args) {
        new BytesHex().debug();
    }

    private void debug() {
        String testString = "Test String";
        System.out.println("The Test String: " + testString);
        byte[] testStringAsBytes = stringToByteArray(testString);
        System.out.println("As a byte Array: " + Arrays.toString(testStringAsBytes));
        String testStringAsHex = byteArrayToHexString(testStringAsBytes);
        System.out.println("As a Hex String: " + testStringAsHex);
        char[] testStringAsHexArray = testStringAsHex.toCharArray();
        System.out.println("As a Hex Array: " + Arrays.toString(testStringAsHexArray));
        System.out.println("Back to the String: " + byteArrayToString(hexArrayToByteArray(testStringAsHexArray)));
        System.out.println("\nIn One Step");
        testStringAsHex = stringToHexString(testString);
        System.out.println("String to Hex String: " + testStringAsHex);
        System.out.println("Hex String to String: " + hexStringToString(testStringAsHex));
    }

    private static byte[] stringToByteArray(String string) {
        return string.getBytes();
    }

    private static String byteArrayToString(byte[] byteArray) {
        try {
            return new String(byteArray, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";
    }

    private static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();

        for (byte byteElement : byteArray) {
            stringBuilder.append(String.format("%02X", byteElement));
        }

        return stringBuilder.toString();
    }

    private static byte[] hexArrayToByteArray(char[] hexArray) {
        byte[] byteArray = new byte[(hexArray.length) / 2];

        for (int index = 0; index < hexArray.length; index += 2) {
            byteArray[index / 2] = (byte) Integer.parseInt(new String(new char[]{hexArray[index], hexArray[index + 1]}), 16);
        }

        return byteArray;
    }

    private static byte[] hexStringToByteArray(String string) {
        byte[] byteArray = new byte[string.length() / 2];

        for (int index = 0; index < string.length(); index += 2) {
            byteArray[index / 2] = (byte) Integer.parseInt(string.substring(index, index + 2), 16);
        }

        return byteArray;
    }

    public static String stringToHexString(String string) {
        return byteArrayToHexString(stringToByteArray(string));
    }

    public static String hexStringToString(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        try {
            byte[] byteArray;
            byteArray = hexStringToByteArray(hex);
            return new String(byteArray, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] textfileToByteArray(String filePath) {
        byte[] textFile;

        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            textFile = new byte[(int) file.length()];
            fileInputStream.read(textFile);
            return textFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}