package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.commands.Categories;
import org.jetbrains.annotations.NotNull;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

public class HostInfo extends Command {
    private final SystemInfo systemInfo = new SystemInfo();
    public HostInfo() {
        this.name = "hostinfo";
        this.help = "shows system information of the host";
        this.aliases = new String[]{"hf","host"};
        this.ownerCommand = true;
        this.category = Categories.owner;
    }
    @Override
    protected void execute(@NotNull CommandEvent event) {
        HardwareAbstractionLayer hw = systemInfo.getHardware();
        OperatingSystem sw = systemInfo.getOperatingSystem();
        CentralProcessor cpu = hw.getProcessor();
        GlobalMemory ram = hw.getMemory();
        OperatingSystem.OSVersionInfo osVersion = sw.getVersionInfo();
        long[] prevTicks = cpu.getSystemCpuLoadTicks();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.error("thread was interrupted", e);
        }
        double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;

        event.replyFormatted("""
                ## Host Information:
                ```
                OS : %s (%s)
                CPU : %s
                CPU Load: %s
                RAM : %s / %s
                Java : %s
                ```
                """,
                sw.getFamily(),
                osVersion.getVersion(),
                cpu.getProcessorIdentifier().getName(),
                String.format("`%.1f%%`", cpuLoad),
                FormatUtil.formatBytes(ram.getAvailable()),
                FormatUtil.formatBytes(ram.getTotal()),
                System.getProperty("java.version")
        );
    }
}
