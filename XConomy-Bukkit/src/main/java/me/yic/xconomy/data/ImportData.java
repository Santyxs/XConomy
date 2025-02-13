/*
 *  This file (ImportData.java) is a part of project XConomy
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
package me.yic.xconomy.data;

import me.yic.xconomy.CommandHandler;
import me.yic.xconomy.XConomy;
import me.yic.xconomy.depend.LoadEconomy;
import me.yic.xconomy.depend.economy.VaultCM;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class ImportData implements CommandExecutor {

    public static File importdataf;
    public static FileConfiguration importdata;

    public static boolean hasImportFile = false;

    private final XConomy plugin;

    public ImportData(XConomy main) {
        plugin = main;
    }

    @SuppressWarnings("ConstantConditions")
    public void onEnable() {

        if (!CreateFile()) {
            plugin.logger("XConomy已成功卸载", 0, null);
            return;
        }

        if (LoadEconomy.loadcm()) {
            Bukkit.getPluginCommand("xconomy").setExecutor(this);
            plugin.logger(null, 1, "Convertion mode enable");
        } else {
            plugin.logger(null, 1, "Conversion mode enable error");
            plugin.logger("XConomy已成功卸载", 0, null);
        }

        plugin.logger(null, 0, "===== YiC =====");
    }

    public void onDisable() {
        plugin.logger("XConomy已成功卸载", 0, null);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp() && XConomy.Config.IMPORTMODE) {
            commandSender.sendMessage("Data import start");
            ImportBalance();
            commandSender.sendMessage("Data import completed");
            return true;
        }
        CommandHandler.showVersion(commandSender);
        return true;
    }

    public boolean CreateFile() {
        if (XConomy.Config.IMPORTMODE) {
            File dataFolder = new File(XConomy.getInstance().getDataFolder(), "importdata");
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                XConomy.getInstance().logger("文件夹创建异常", 1, null);
                return false;
            }
            importdataf = new File(dataFolder, "data.yml");
            importdata = YamlConfiguration.loadConfiguration(importdataf);
            if (!importdataf.exists()) {
                try {
                    importdata.save(importdataf);
                } catch (IOException e) {
                    e.printStackTrace();
                    XConomy.getInstance().logger("缓存文件创建异常", 1, null);
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public void ImportBalance() {
        for (OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
            if (!importdata.contains(op.getName())) {
                importdata.createSection(op.getName() + ".balance");
                importdata.set(op.getName() + ".balance", VaultCM.getBalance(op).toString());
            }
        }
        save();
    }


    public void save() {
        try {
            importdata.save(importdataf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void isExitsFile() {
        File idataFolder = new File(XConomy.getInstance().getDataFolder(), "importdata");
        if (idataFolder.exists()) {
            importdataf = new File(idataFolder, "data.yml");
            if (importdataf.exists()) {
                hasImportFile = true;
                importdata = YamlConfiguration.loadConfiguration(importdataf);
            }
        }
    }

    public static BigDecimal getBalance(String player, double inb) {
        if (importdata.contains(player)) {
            return DataFormat.formatString(importdata.getString(player + ".balance"));
        }
        return DataFormat.formatdouble(inb);
    }
}
