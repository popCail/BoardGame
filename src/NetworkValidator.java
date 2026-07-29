/**
 * 联网房间地址校验（IP:端口）。
 */
public final class NetworkValidator {

    private static final String IPV4_PATTERN =
            "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)"
                    + "(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}";

    private static final String PORT_PATTERN = "[0-9]{4}";

    private NetworkValidator() {
    }

    /**
     * 解析并校验 "ip:port" 地址。
     *
     * @return 长度为 2 的数组 [ip, port]；校验失败返回 null
     */
    public static String[] parseAddress(String input) {
        if (input == null || !input.contains(":")) {
            return null;
        }
        String[] parts = input.split(":", 2);
        if (parts.length != 2) {
            return null;
        }
        if (!isValidIpv4(parts[0]) || !isValidPort(parts[1])) {
            return null;
        }
        return parts;
    }

    public static boolean isValidIpv4(String ip) {
        return ip != null && ip.matches(IPV4_PATTERN);
    }

    public static boolean isValidPort(String port) {
        return port != null && port.matches(PORT_PATTERN);
    }
}
