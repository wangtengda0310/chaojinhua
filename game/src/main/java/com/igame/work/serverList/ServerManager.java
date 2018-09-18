package com.igame.work.serverList;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    public List<ServerInfo> servers = new ArrayList<>();

    public boolean exists(int serverId) {
        return servers.stream().anyMatch(serverInfo -> serverInfo.getServerId().equals(String.valueOf(serverId)));
    }
}
