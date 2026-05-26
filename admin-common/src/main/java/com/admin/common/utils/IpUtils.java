package com.admin.common.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP 地址工具类
 */
public class IpUtils {

    /**
     * 本地地址
     */
    private static final String LOCALHOST = "127.0.0.1";

    /**
     * 未知地址
     */
    private static final String UNKNOWN = "unknown";

    /**
     * 获取客户端 IP 地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP
            int index = ip.indexOf(',');
            if (index > 0) {
                return ip.substring(0, index).trim();
            }
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getRemoteAddr();
        if (LOCALHOST.equals(ip)) {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                // ignore
            }
        }

        return LOCALHOST.equals(ip) ? LOCALHOST : ip;
    }

    /**
     * 验证 IP 地址是否有效
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 检查是否为内网 IP
     */
    public static boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        // 10.0.0.0 - 10.255.255.255
        // 172.16.0.0 - 172.31.255.255
        // 192.168.0.0 - 192.168.255.255
        // 127.0.0.1
        return ip.startsWith("10.") ||
               ip.startsWith("192.168.") ||
               (ip.startsWith("172.") && isValid172Ip(ip)) ||
               LOCALHOST.equals(ip);
    }

    private static boolean isValid172Ip(String ip) {
        try {
            String[] parts = ip.split("\\.");
            int second = Integer.parseInt(parts[1]);
            return second >= 16 && second <= 31;
        } catch (Exception e) {
            return false;
        }
    }
}
