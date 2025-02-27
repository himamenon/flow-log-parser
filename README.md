# Flow Log Parser

This program processes AWS-style flow logs and matches flow entries to tags based on a lookup table. The lookup table maps port/protocol combinations to tags. It also generates statistics like tag counts and port/protocol combination counts.

## Assumptions

- The program **only supports AWS flow log format version 2**.
- The program **does not support custom log formats**.
- The program expects two input files:
  - A **flow log file** (e.g., `flow_logs.txt`).
  - A **lookup table CSV file** (e.g., `tag_lookup.csv`).
- The program is **case-insensitive** when matching ports and protocols.

## Requirements

- **Java 8 or higher**.

## Compilation and Execution

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/flow-log-parser.git
   cd flow-log-parser
   ```

2. **Compile the program**:
   Make sure that you have Java installed on your machine. You can check by running:
   ```bash
   java -version
   ```
   To compile the Java code, run the following in the project directory:
   ```bash
   javac FlowLogParser.java
   ```

3. **Run the program**:
   Execute the compiled Java program:
   ```bash
   java FlowLogParser
   ```

   Make sure that the input files (`flow_logs.txt` and `tag_lookup.csv`) are in the same directory, or specify the correct path in the code.

## Tests Performed

- **Basic Flow Logs**: Tested the program with various flow log entries to ensure correct tag matching.
- **Edge Cases**:
  - Flow log entries without matching ports or protocols in the lookup table (ensuring they are tagged as `Untagged`).
  - Logs with missing or malformed fields (ensuring graceful handling).
  - Empty input files to test the program’s robustness.
  - Case-insensitivity of protocol names.

## Output

The program generates two sets of statistics:
1. **Tag Counts**:
   - The count of logs tagged with each tag.
2. **Port/Protocol Combination Counts**:
   - The count of flow logs for each unique port/protocol combination.

Example Output:

```txt
Tag Counts:
sv_P2,1
sv_P1,2
sv_P4,1
email,3
Untagged,9

Port/Protocol Combination Counts:
443,tcp,1
23,tcp,1
25,tcp,1
110,tcp,1
143,tcp,1
80,tcp,1
