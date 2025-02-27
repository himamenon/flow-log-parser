import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class FlowLogParser {

    public static void main(String[] args) throws IOException {
        String flowLogFile = "flow_logs.txt"; // Path to the flow log file
        String lookupFile = "tag_lookup.csv"; // Path to the lookup CSV file
        
        // Parse the tag lookup CSV
        Map<String, Map<String, String>> tagLookup = parseTagLookup(lookupFile);
        
        // Parse the flow log data
        List<FlowLogEntry> flowLogs = parseFlowLogs(flowLogFile);
        
        // Count tag occurrences and port/protocol combinations
        Map<String, Integer> tagCounts = new HashMap<>();
        Map<String, Integer> portProtocolCounts = new HashMap<>();
        
        for (FlowLogEntry log : flowLogs) {
            String key = log.dstPort + "," + log.protocol.toLowerCase();
            String tag = tagLookup.getOrDefault(key, Collections.singletonMap("default", "Untagged"))
                    .getOrDefault("tag", "Untagged");
            
            // Update tag counts
            tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
            
            // Update port/protocol combination counts
            String portProtocolKey = log.dstPort + "," + log.protocol.toLowerCase();
            portProtocolCounts.put(portProtocolKey, portProtocolCounts.getOrDefault(portProtocolKey, 0) + 1);
        }
        
        // Output the counts
        System.out.println("Tag Counts:");
        tagCounts.forEach((tag, count) -> System.out.println(tag + "," + count));
        
        System.out.println("\nPort/Protocol Combination Counts:");
        portProtocolCounts.forEach((combination, count) -> {
            String[] parts = combination.split(",");
            System.out.println(parts[0] + "," + parts[1] + "," + count);
        });
    }

    // Parse the tag lookup CSV into a Map of port/protocol -> tag
    private static Map<String, Map<String, String>> parseTagLookup(String lookupFile) throws IOException {
        Map<String, Map<String, String>> tagLookup = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(lookupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length == 3) {
                    String port = columns[0].trim();
                    String protocol = columns[1].trim().toLowerCase();
                    String tag = columns[2].trim();
                    String key = port + "," + protocol;
                    tagLookup.putIfAbsent(key, new HashMap<>());
                    tagLookup.get(key).put("tag", tag);
                }
            }
        }
        return tagLookup;
    }

    // Parse the flow log file into FlowLogEntry objects
    private static List<FlowLogEntry> parseFlowLogs(String flowLogFile) throws IOException {
        List<FlowLogEntry> flowLogs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(flowLogFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\\s+");
                if (columns.length >= 11) {
                    String dstIp = columns[3];
                    String srcIp = columns[4];
                    int dstPort = Integer.parseInt(columns[5]);
                    String protocol = getProtocol(Integer.parseInt(columns[7]));
                    flowLogs.add(new FlowLogEntry(dstIp, srcIp, dstPort, protocol));
                }
            }
        }
        return flowLogs;
    }

    // Map protocol number to protocol name
    private static String getProtocol(int protocolId) {
        switch (protocolId) {
            case 6: return "tcp";
            case 17: return "udp";
            case 1: return "icmp";
            default: return "unknown";
        }
    }
}

// Data structure for flow log entry
class FlowLogEntry {
    String dstIp;
    String srcIp;
    int dstPort;
    String protocol;

    public FlowLogEntry(String dstIp, String srcIp, int dstPort, String protocol) {
        this.dstIp = dstIp;
        this.srcIp = srcIp;
        this.dstPort = dstPort;
        this.protocol = protocol;
    }
}
