package project.PingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingService {
    String ipAddress = String.valueOf(InetAddress.getLocalHost());
    //this executes the system's ping command and captures the respective output.
    //google's public ip
    String testip = "8.8.8.8";
    //ping is tested 4 times
    ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "4", testip);

    public PingService() throws UnknownHostException {
    }

    int pingAnalysis() {
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Ping exit code: " + exitCode);
            if (exitCode == 0) {
                System.out.println(ipAddress + " is reachable.");
            } else {
                System.out.println(ipAddress + " is NOT reachable.");
            }
            return exitCode;
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception occurred: " + e.getMessage());
            return -1;
        }
    }
}
