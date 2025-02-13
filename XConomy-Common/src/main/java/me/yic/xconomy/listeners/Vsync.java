/*
 *  This file (Vsync.java) is a part of project XConomy
 *  Copyright (C) YiC and contributors
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.yic.xconomy.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.yic.xconomy.XConomyVelocity;
import me.yic.xconomy.data.syncdata.SyncMessage;
import me.yic.xconomy.info.SyncType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.*;

public class Vsync {

    @SuppressWarnings(value = {"unused"})
    @Subscribe
    public void on(PluginMessageEvent event) {
        if (event.getSource() instanceof Player) {
            return;
        }

        if (!event.getIdentifier().getId().equals(XConomyVelocity.acb.getId())) {
            return;
        }


        ByteArrayInputStream input = new ByteArrayInputStream(event.getData());
        try {
            ObjectInputStream ios = new ObjectInputStream(input);
            ServerConnection senderServer = (ServerConnection) event.getSource();

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(output);

            String sv = ios.readUTF();
            oos.writeUTF(sv);
            String svv = sv;
            if (svv.contains(".")) {
                svv = "versions §f" + svv;
            } else {
                svv = "§fold versions";
            }
            if (!sv.equals(XConomyVelocity.syncversion)) {
                XConomyVelocity.getInstance().logger.warn("§cReceived data from " + svv + ", §cunable to synchronize, Current plugin version §f" + XConomyVelocity.syncversion);
                return;
            }

            Object ob = ios.readObject();
            if (ob instanceof SyncMessage) {
                SyncMessage sd = (SyncMessage) ob;
                if (sd.getSyncType().equals(SyncType.MESSAGE)) {
                    ProxiedPlayer p = ProxyServer.getInstance().getPlayer(sd.getUniqueId());
                    if (p == null) {
                        return;
                    }
                }else if(sd.getSyncType().equals(SyncType.MESSAGE_SEMI)) {
                    ProxiedPlayer p = ProxyServer.getInstance().getPlayer(sd.getName());
                    if (p == null) {
                        return;
                    }else{
                        sd.setRUniqueId(p.getUniqueId());
                    }
                }
            }

            oos.writeObject(ob);
            oos.flush();
            for (RegisteredServer s : XConomyVelocity.getInstance().server.getAllServers()) {
                if (!s.equals(senderServer.getServer()) && s.getPlayersConnected().size() > 0) {
                    XConomyVelocity.getInstance().server.getScheduler().buildTask(XConomyVelocity.getInstance(), () -> SendMessTaskB(s, output)).schedule();
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void SendMessTaskB(RegisteredServer s, ByteArrayOutputStream stream) {
        s.sendPluginMessage(XConomyVelocity.aca, stream.toByteArray());
    }
}
