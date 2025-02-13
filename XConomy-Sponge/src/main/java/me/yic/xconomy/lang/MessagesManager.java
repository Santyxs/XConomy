/*
 *  This file (MessagesManager.java) is a part of project XConomy
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
package me.yic.xconomy.lang;

import me.yic.xconomy.XConomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MessagesManager {

    public static ConfigurationNode messageFile;
    public static ConfigurationNode langFile;
    public static ConfigurationLoader<ConfigurationNode> loader;
    public static Messages smessageList = new Messages();

    public static void loadsysmess() {
        try {
            URL url = new URL(XConomy.jarPath + "!/lang/" + XConomy.Config.LANGUAGE.toLowerCase() + ".yml");
            InputStream is = url.openStream();
            is.close();
            YAMLConfigurationLoader sysloader = YAMLConfigurationLoader.builder().setURL(url).build();
            langFile = sysloader.load();
        } catch (IOException e) {
            try {
                URL url = new URL(XConomy.jarPath + "!/lang/english.yml");
                InputStream is = url.openStream();
                is.close();
                YAMLConfigurationLoader sysloader = YAMLConfigurationLoader.builder().setURL(url).build();
                langFile = sysloader.load();
            } catch (IOException ioException) {
                XConomy.getInstance().logger(null, 1, "System languages file read error");
                ioException.printStackTrace();
            }
        }
    }


    public static void loadlangmess() {
        File mfile = new File(XConomy.getInstance().configDir.toFile(), "message.yml");
        boolean translate = false;
        loader = YAMLConfigurationLoader.builder().setFlowStyle(DumperOptions.FlowStyle.BLOCK).setIndent(2).setFile(mfile).build();

        if (!mfile.exists()) {
            loader.createEmptyNode();
            translate = true;
            XConomy.getInstance().logger("已创建一个新的语言文件", 0, null);
        }

        try {
            messageFile = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LanguagesManager.compare(XConomy.Config.LANGUAGE);
        if (translate) {
            Languages.translatorName(XConomy.Config.LANGUAGE, mfile);
        }
    }

    public static String getAuthor() {
        if (XConomy.Config.LANGUAGE.equalsIgnoreCase("Chinese")
                | XConomy.Config.LANGUAGE.equalsIgnoreCase("ChineseTW")) {
            return "伊C";
        } else {
            return "YiC";
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static String systemMessage(String message) {
        return langFile.getNode(smessageList.gettag(message)).getString();
    }

    @SuppressWarnings("ConstantConditions")
    public static String getTranslatorS() {
        String trm = langFile.getNode("translation-author").getString();
        if (!trm.equalsIgnoreCase("None")) {
            return trm;
        } else {
            return null;
        }
    }
}
