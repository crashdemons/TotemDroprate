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

    double rate = 1.0f; //storage for the current droprate (where 1.0 is 100%)

    public void reload(boolean message, CommandSender sender) { //config reloading with feedback
        reloadConfig();//reload plugin.getConfig() values from disk
        rate = getConfig().getDouble("droprate"); //get the configured droprate without the hashmap lookup
        if (message) {//feedback was requested
            if (sender == null) {//no receiver for the feedback was provided - print to console
                getLogger().info("config reloaded");
            } else {//provide the feedback to the place specified
                sender.sendMessage(ChatColor.GOLD + "TotemDroprate config reloaded");
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
        saveDefaultConfig();//save the default config to disk IF it doesn't already exist
        this.getServer().getPluginManager().registerEvents(this, this);//register bukkit events with the plugin
        reload(false, null);//reload onfig if it has not been
        this.getLogger().info("Enabled.");
    }

    public void onDeath(EntityDeathEvent evt) {
        Entity entity = evt.getEntity();
        if (entity instanceof Evoker) { //change droprate of evoker items, assuming the current rate is 100%
            evt.getDrops().removeIf((item) -> { //remove the item IF it's a totem AND it is not within the droprate
                if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
                    double roll = ThreadLocalRandom.current().nextDouble();
                    return roll >= rate; //the random drop-roll was outside of the droprate range [0,rate)
                }
                return false;//don't modify drops when the item doesn't meet our requirements
            });
        }
    }
}