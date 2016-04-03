package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supplies access to the plugin's language files.
 */
public class Language {
    private static final String NOT_FOUND = ChatColor.RED + "[Lang: <key>]";
    private static final String NOT_FOUND_KEY = "key";
    public final GUI gui = new GUI();
    public final Commands commands = new Commands();
    public final Items items = new Items();
    private YamlConfiguration lang;
    private Map<Material, ItemNames> itemNames;

    /**
     * Create an instance of Language
     *
     * @param language The language to use.
     */
    @SuppressWarnings("deprecation")
    public Language(String language) {
        FastCraft fastCraft = FastCraft.getInstance();

        String resPath = "lang/" + language + ".yml";
        InputStream resStream = fastCraft.getResource(resPath);
        if (resStream != null) {
            // Load from internal lang file
            InputStreamReader reader = new InputStreamReader(resStream);
            lang = YamlConfiguration.loadConfiguration(reader);
        } else {
            try {
                // Load from custom lang file
                File langFile = new File(fastCraft.getDataFolder(), resPath);
                if (!langFile.exists()) {
                    langFile.getParentFile().mkdirs();
                    FastCraft.log("Created language file: '" + langFile.getName() + "'");
                    Files.copy(fastCraft.getResource("lang/EN.yml"), langFile.toPath());
                }
                lang = YamlConfiguration.loadConfiguration(langFile);
            } catch (IOException e) {
                e.printStackTrace();
                lang = new YamlConfiguration();
            }
        }

        // Load item names
        ConfigurationSection itemSection = lang.getConfigurationSection("items");
        itemNames = new HashMap<>();
        if (itemSection != null) {
            for (String item : itemSection.getKeys(false)) {
                Material itemType = Bukkit.getUnsafe().getMaterialFromInternalName(item);
                if (itemType == null) {
                    FastCraft.err("Unknown item type: '" + item + "'");
                    continue;
                }
                ItemNames itemName;
                if (itemSection.isString(item)) {
                    // If item name was given directly
                    itemName = new ItemNames(itemSection.getString(item), null);
                } else {
                    // If item names are given based off of item data values
                    ConfigurationSection nameSection = itemSection.getConfigurationSection(item);
                    String defName = null;
                    Map<Integer, String> names = new HashMap<>();
                    for (String data : nameSection.getKeys(false)) {
                        if (data.equals("d")) {
                            // Get the default item name
                            defName = nameSection.getString(data);
                        } else {
                            // Get the item name for specific data values
                            try {
                                int num = Integer.parseInt(data);
                                names.put(num, nameSection.getString(data));
                            } catch (NumberFormatException e) {
                                FastCraft.err("Item data is not 'd' or a number: " + data);
                            }
                        }
                    }
                    itemName = new ItemNames(defName, names);
                }
                if (itemName.getDefName() == null) {
                    FastCraft.warning("Language (" + language + ") has missing default (d) for item: '" + item + "'");
                }
                itemNames.put(itemType, itemName);
            }
        }
    }

    /**
     * Useful method to convert an integer to a String.
     *
     * @param integer The ineger to convert to a String.
     * @return Returns the integer as a String.
     */
    private String s(int integer) {
        return Integer.toString(integer);
    }

    /**
     * Format a
     *
     * @param str    The String to format.
     * @param varVal The variables and values.
     * @return Returns the formatted String
     */
    private String format(String str, String... varVal) {
        assert varVal.length % 2 == 0 : "varVal must have an even number of elements";

        for (int i = 0; i < varVal.length; i += 2) {
            str = str.replace("<" + varVal[i] + ">", varVal[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private String get(String key, String... varVal) {
        String entry = lang.getString(key);
        if (entry == null) {
            return format(NOT_FOUND, NOT_FOUND_KEY, key);
        }
        return format(entry, varVal);
    }

    private List<String> getList(String key, String... varVal) {
        List<String> entry = lang.getStringList(key);
        if (entry == null) {
            return Collections.singletonList(format(NOT_FOUND, NOT_FOUND_KEY, key));
        }

        for (int i = 0; i < entry.size(); i++) {
            entry.set(i, format(entry.get(i), varVal));
        }
        return entry;
    }

    public class GUI {
        public final Ingredients ingredients = new Ingredients();
        public final Results results = new Results();
        public final Toolbar toolbar = new Toolbar();

        public String title() {
            return get("gui.title");
        }

        public class Ingredients {
            public String label() {
                return get("gui.ingredients.label");
            }

            public String item(int amount, String item) {
                return get("gui.ingredients.item", "amount", s(amount), "item", item);
            }
        }

        public class Results {
            public String label() {
                return get("gui.results.label");
            }

            public String item(ItemStack is) {
                String itemName = RecipeUtil.getItemName(is);
                return get("gui.results.item", "amount", s(is.getAmount()), "item", itemName);
            }
        }

        public class Toolbar {
            private static final String tb = "gui.toolbar.";

            public final PagePrev pagePrev = new PagePrev();
            public final PageNext pageNext = new PageNext();
            public final CraftItems craftItems = new CraftItems();
            public final CraftArmor craftArmor = new CraftArmor();
            public final CraftFireworks craftFireworks = new CraftFireworks();
            public final Refresh refresh = new Refresh();
            public final Multiplier multiplier = new Multiplier();
            public final Workbench workbench = new Workbench();

            public class PagePrev {
                public String title() {
                    return get(tb + "page-prev.title");
                }

                public List<String> description(int prev, int total, int cur) {
                    return getList(tb + "page-prev.description", "prev", s(prev), "total", s(total), "cur", s(cur));
                }
            }

            public class PageNext {
                public String title() {
                    return get(tb + "page-next.title");
                }

                public List<String> description(int prev, int total, int cur) {
                    return getList(tb + "page-next.description", "next", s(prev), "total", s(total), "cur", s(cur));
                }
            }

            public class CraftItems {
                public String title() {
                    return get(tb + "craft-items.title");
                }

                public List<String> description() {
                    return getList(tb + "craft-items.description");
                }
            }

            public class CraftArmor {
                public String title() {
                    return get(tb + "craft-armor.title");
                }

                public List<String> description() {
                    return getList(tb + "craft-armor.description");
                }
            }

            public class CraftFireworks {
                public String title() {
                    return get(tb + "craft-fireworks.title");
                }

                public List<String> description() {
                    return getList(tb + "craft-fireworks.description");
                }
            }

            public class Refresh {
                public String title() {
                    return get(tb + "refresh.title");
                }

                public List<String> description() {
                    return getList(tb + "refresh.description");
                }
            }

            public class Multiplier {
                public String title(int mult) {
                    return get(tb + "multiplier.title", "mult", s(mult));
                }

                public List<String> description(int mult) {
                    return getList(tb + "refresh.description", "mult", s(mult));
                }
            }

            public class Workbench {
                public String title() {
                    return get(tb + "workbench.title");
                }

                public List<String> description() {
                    return getList(tb + "workbench.description");
                }
            }
        }
    }

    public class Commands {

    }

    public class Items {
        @SuppressWarnings("deprecation")
        public String name(ItemStack item) {
            ItemNames names = itemNames.get(item.getType());
            if (names == null) return null;
            return names.getName(item.getData().getData());
        }
    }

    private class ItemNames {
        private final String defName;
        private final Map<Integer, String> names;

        public ItemNames(String defName, Map<Integer, String> names) {
            this.defName = defName;
            this.names = names;
        }

        public String getDefName() {
            return defName;
        }

        public String getName(int data) {
            if (names == null) return defName;
            String name = names.get(data);
            return name == null ? defName : name;
        }
    }
}
