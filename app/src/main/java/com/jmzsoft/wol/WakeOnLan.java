package com.jmzsoft.wol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class WakeOnLan {
    private static final String MAC_REGEX = "([0-9a-fA-F]{2}[-:]){5}[0-9a-fA-F]{2}";

    static boolean sendPacket() throws IllegalArgumentException {

        byte[] macBytes = getMacBytes();
        byte[] bytes = new byte[6 + 16 * macBytes.length];

        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(Constants.URL.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)","")), Constants.PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] getMacBytes() throws IllegalArgumentException {

        if (!Constants.MAC.matches(MAC_REGEX)) {
            throw new IllegalArgumentException("Invalid MAC address");
        }

        byte[] bytes = new byte[6];
        String[] hex = Constants.MAC.split("(:|\\-)");

        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
}
