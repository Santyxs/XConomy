/*
 *  This file (SyncMessage.java) is a part of project XConomy
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
package me.yic.xconomy.data.syncdata;

import me.yic.xconomy.info.SyncType;

import java.util.UUID;

public class SyncPermission extends SyncData{

    //1 - pay 2 - paytoggle
    private final int type;
    private final Boolean value;

    public SyncPermission(String sign, UUID uuid, int type, Boolean value){
        super(sign, SyncType.PERMISSION, uuid);
        this.type = type;
        this.value = value;
    }

    public int getType(){
        return type;
    }

    public Boolean getValue(){
        return value;
    }
}
