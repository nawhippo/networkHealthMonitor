package project.PacketCounterService;
import java.io.EOFException;
import java.net.InetAddress;

import lombok.Getter;
import lombok.Setter;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
@Getter
@Setter
public class PacketCounterServiceDevice {


    ArrayList<String> dests;
    ArrayList<String> sources;
    int MAXSUDPSIZE = 65535;
    InetAddress addr = InetAddress.getByName("");
    PcapNetworkInterface nif = Pcaps.getDevByAddress(addr);
    PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;

    public PacketCounterServiceDevice() throws PcapNativeException, UnknownHostException, NotOpenException, EOFException, TimeoutException, InterruptedException {
        dests = new ArrayList<String>();
        sources = new ArrayList<String>();
    }

    int count = 0;
    PacketListener listener = new PacketListener() {
        @Override
        public void gotPacket(Packet packet) {
            count += 1;
            Packet.Header header = packet.getHeader();
            byte[] data = header.getRawData();
            int version = data[0];
            String source = "";
            String dest = "";
            if (version == 4) {
                source = data[12] + "." + data[13] + "." + data[14] + "." + data[15];
                dest = data[16] + "." + data[17] + "." + data[18] + "." + data[19];
            } else if (version == 6) {
                source = data[8] + "." + data[9] + "." + data[10] + "." + data[11] + "." + data[12] + "." + data[13] + "." + data[14] + "." + data[15];
                dest = data[24] + "." + data[25] + "." + data[26] + "." + data[27] + "." + data[28] + "." + data[29] + "." + data[30] + "." + data[31];
            }
            sources.add(source);
            dests.add(dest);
            return;
        }
    };

    public void StartpacketCapture() throws NotOpenException, PcapNativeException, InterruptedException {
        PcapHandle handle = nif.openLive(MAXSUDPSIZE, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
        try {
            while (true) {
                handle.loop(1, listener);
                PcapStat stats = handle.getStats();
                System.out.println("Packets Received: " + stats.getNumPacketsReceived());
                System.out.println("Packets Dropped: " + stats.getNumPacketsDropped());
            }
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NotOpenException e) {
            throw new RuntimeException(e);
        }
    }


    }


