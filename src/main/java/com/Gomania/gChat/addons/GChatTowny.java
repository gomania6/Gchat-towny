package com.Gomania.gChat.addons;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Nation;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GChatTowny extends JavaPlugin {

    private TownyAPI towny;
    private FileConfiguration config;
    private File configFile;

    @Override
    public void onEnable() {
        // Проверяем gChat
        if (Bukkit.getPluginManager().getPlugin("gChat") == null) {
            getLogger().severe("gChat не найден! Отключаем аддон...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Проверяем Towny
        towny = TownyAPI.getInstance();
        if (towny == null) {
            getLogger().severe("Towny не найден! Отключаем аддон...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        loadConfig();
        registerPlaceholders();
        getLogger().info("gChat-Towny успешно включен!");
    }

    private void loadConfig() {
        File gchatFolder = new File(getDataFolder().getParentFile(), "gChat");
        if (!gchatFolder.exists()) gchatFolder.mkdirs();

        configFile = new File(gchatFolder, "towny-gChat.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                config = YamlConfiguration.loadConfiguration(configFile);

                config.set("tag_formats.town", "&f[&a%s&f] ");
                config.set("tag_formats.nation", "&f[&#FF5656%s&f] ");
                config.set("tag_formats.both", "&f[&c%s&f | &a%s&f] ");
                config.set("use_hex_colors", true);
                config.set("prefer_tag", true);
                config.set("debug", false);

                config.save(configFile);
                getLogger().info("Создан файл конфигурации towny-gChat.yml");
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Не удалось создать конфиг", e);
            }
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
            getLogger().info("Конфигурация towny-gChat.yml загружена");
        }
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new GChatTownyExpansion(this).register();
            getLogger().info("PlaceholderAPI интеграция включена");
        } else {
            getLogger().warning("PlaceholderAPI не найден. Towny теги работать не будут.");
        }
    }

    private String resolveTownName(Town town) {
        if (town == null) return "";
        boolean preferTag = config.getBoolean("prefer_tag", true);
        if (preferTag && town.hasTag() && town.getTag() != null && !town.getTag().isEmpty()) {
            return town.getTag();
        }
        return town.getName();
    }

    private String resolveNationName(Nation nation) {
        if (nation == null) return "";
        boolean preferTag = config.getBoolean("prefer_tag", true);
        if (preferTag && nation.hasTag() && nation.getTag() != null && !nation.getTag().isEmpty()) {
            return nation.getTag();
        }
        return nation.getName();
    }

    public String getTownyTag(Player player) {
        if (player == null) return "";

        Resident res = towny.getResident(player.getUniqueId());
        if (res == null || !res.hasTown()) return "";

        try {
            Town town = res.getTown();
            Nation nation = town.hasNation() ? town.getNation() : null;

            String townName = resolveTownName(town);

            if (nation != null) {
                String nationName = resolveNationName(nation);
                String format = config.getString("tag_formats.both", "&f[&c%s&f | &a%s&f] ");
                return applyColors(String.format(format, nationName, townName));
            } else {
                String format = config.getString("tag_formats.town", "&f[&a%s&f] ");
                return applyColors(String.format(format, townName));
            }

        } catch (Exception e) {
            if (config.getBoolean("debug", false)) e.printStackTrace();
        }

        return "";
    }

    public String getTownTag(Player player) {
        if (player == null) return "";
        try {
            Resident res = towny.getResident(player.getUniqueId());
            if (res != null && res.hasTown()) {
                Town town = res.getTown();
                String townName = resolveTownName(town);
                return applyColors(String.format(config.getString("tag_formats.town", "&f[&a%s&f] "), townName));
            }
        } catch (Exception ignored) {}
        return "";
    }

    public String getNationTag(Player player) {
        if (player == null) return "";
        try {
            Resident res = towny.getResident(player.getUniqueId());
            if (res != null && res.hasTown()) {
                Town town = res.getTown();
                if (town.hasNation()) {
                    Nation nation = town.getNation();
                    String nationName = resolveNationName(nation);
                    return applyColors(String.format(config.getString("tag_formats.nation", "&f[&#FF5656%s&f] "), nationName));
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    private String applyColors(String text) {
        if (text == null) return "";

        // Стандартные цвета
        text = text.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3")
                .replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7")
                .replace("&8", "§8").replace("&9", "§9").replace("&a", "§a").replace("&b", "§b")
                .replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f")
                .replace("&k", "§k").replace("&l", "§l").replace("&m", "§m").replace("&n", "§n")
                .replace("&o", "§o").replace("&r", "§r");

        // HEX
        if (config.getBoolean("use_hex_colors", true)) {
            Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
            Matcher matcher = pattern.matcher(text);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String hex = matcher.group(1);
                StringBuilder repl = new StringBuilder("§x");
                for (char c : hex.toCharArray()) repl.append("§").append(c);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(repl.toString()));
            }
            matcher.appendTail(sb);
            text = sb.toString();
        }

        return text;
    }

    // ================= PlaceholderAPI Expansion =================
    public static class GChatTownyExpansion extends PlaceholderExpansion {

        private final GChatTowny plugin;

        public GChatTownyExpansion(GChatTowny plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean canRegister() {
            return true;
        }

        @Override
        public String getIdentifier() {
            return "gchat";
        }

        @Override
        public String getAuthor() {
            return "Gomania";
        }

        @Override
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            if (player == null) return "";

            switch (identifier.toLowerCase()) {
                case "towny":
                    return plugin.getTownyTag(player);
                case "townytown":
                    return plugin.getTownTag(player);
                case "townynation":
                    return plugin.getNationTag(player);
            }

            return null;
        }
    }
}
