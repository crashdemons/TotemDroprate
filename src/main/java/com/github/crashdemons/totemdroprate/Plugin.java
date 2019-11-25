/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.totemdroprate;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Evoker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class Plugin extends JavaPlugin implements Listener {

    double rate = 1.0f;

    public void reload(boolean message, CommandSender sender) {
        reloadConfig();
        rate = getConfig().getDouble("droprate");
        if (message) {
            if (sender == null) {
                getLogger().info("reloaded");
            } else {
                sender.sendMessage(ChatColor.GOLD + "TotemDroprate reloaded");
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("totemdropratereload")) return true;//not the command we expected!
        if(args.length!=0) return false;
        if (sender.hasPermission("totemdroprate.reload")) reload(true, sender);
        else sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
        
        return true;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        reload(true, null);
        this.getLogger().info("Enabled.");
    }

    public void onDeath(EntityDeathEvent evt) {
        Entity entity = evt.getEntity();
        if (entity instanceof Evoker) {
            evt.getDrops().removeIf((item) -> {
                if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
                    double roll = ThreadLocalRandom.current().nextDouble();
                    return roll >= rate;
                }
                return false;
            });
        }
    }
}