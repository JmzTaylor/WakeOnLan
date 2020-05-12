package com.jmzsoft.wol;

public interface Constants {
    // URL of server assumes port forwarding on router if domain is used.
    // Can use IP instead for local network server
    // Sometimes this requires http or https to be added depending on your setup.
    // Shutdown and wake strips http or https from this string.
    String URL = "";
    // MAC Address of server
    String MAC = "";
    // Port for magic packet to be sent.  Default is port 9
    int PORT = 9;
    // User to login in as to shutdown server
    String USER = "root";
    // User password
    String PASSWORD = "";
    // Command to shutdown server
    String COMMAND = "shutdown -h now";
    // Port for SSH
    int SSH_PORT = 22;
    // Disable host checking for SSH
    Boolean DISABLE_HOST_CHECKING = true;
}
