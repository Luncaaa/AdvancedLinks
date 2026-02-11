package me.lucaaa.advancedlinks.mod_common.managers;

import com.google.gson.*;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.managers.ConfigManager;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

public class ModConfigManager extends ConfigManager {
    private final AdvancedLinks plugin;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final File file;
    private JsonObject root;

    public ModConfigManager(AdvancedLinks plugin, File file) {
        super(plugin, file);

        this.plugin = plugin;
        this.file = file;

        String path = file.getAbsolutePath();
        JsonObject root;
        if (!file.exists()) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
                if (is == null) {
                    plugin.log(Level.WARNING, "Couldn't find default in JAR resources. Creating empty config: " + path);
                    this.root = new JsonObject();
                    save();
                    return;
                }

                Files.copy(is, file.toPath());
            } catch (IOException e) {
                plugin.logError(Level.WARNING, "Couldn't copy config from JAR: " + path + ": ", e);
            }
        }

        try (FileReader reader = new FileReader(file)) {
            JsonElement element = JsonParser.parseReader(reader);
            root = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        } catch (IOException e) {
            plugin.logError(Level.WARNING, "Couldn't load config: " + path + ": ", e);
            root = new JsonObject();
        }

        this.root = root;
    }

    @Override
    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            plugin.logError(Level.WARNING, "Couldn't save config: " + file.getName() + ": ", e);
        }
    }

    @Override
    public <T> void set(String path, T value) {
        JsonObject section = Objects.requireNonNull(resolve(path, true)).getAsJsonObject();
        String key = getFinalKey(path);

        switch (value) {
            case null -> section.remove(key);
            case String v -> section.addProperty(key, v);
            case Number v -> section.addProperty(key, v);
            case Boolean v -> section.addProperty(key, v);
            case Character v -> section.addProperty(key, v);
            case List<?> list -> {
                JsonArray array = new JsonArray();
                for (Object obj : list) {
                    if (obj instanceof String s) array.add(s);
                    else if (obj instanceof Number n) array.add(n);
                    else if (obj instanceof Boolean b) array.add(b);
                    else if (obj instanceof Character c) array.add(c);
                }
                section.add(key, array);
            }
            default -> {}
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrDefault(String path, T def, boolean save) {
        JsonObject section = Objects.requireNonNull(resolve(path, true)).getAsJsonObject();
        String setting = getFinalKey(path);

        if (!section.has(setting)) {
            plugin.log(Level.WARNING, "Missing setting \"" + setting + "\" (" + file.getName() + " file)! Setting to default value: " + def);
            set(path, def);
            save();
            return def;
        }

        JsonElement element = section.get(setting);

        try {
            return switch (def) {
                case String ignored -> (T) element.getAsString();
                case Integer ignored -> (T) (Integer) element.getAsInt();
                case Boolean ignored -> (T) (Boolean) element.getAsBoolean();
                case Double ignored -> (T) (Double) element.getAsDouble();
                case Long ignored -> (T) (Long) element.getAsLong();
                case List<?> defaultList -> {
                    if (!element.isJsonArray()) throw new IllegalArgumentException("Not a list");

                    JsonArray array = element.getAsJsonArray();
                    List<Object> resultList = new ArrayList<>();

                    Object firstItem = defaultList.isEmpty() ? null : defaultList.getFirst();

                    for (JsonElement item : array) {
                        switch (firstItem) {
                            case String ignored -> resultList.add(item.getAsString());
                            case Integer ignored -> resultList.add(item.getAsInt());
                            case Boolean ignored -> resultList.add(item.getAsBoolean());
                            case Double ignored -> resultList.add(item.getAsDouble());
                            case Long ignored -> resultList.add(item.getAsLong());
                            case null, default -> {
                                if (item.isJsonObject()) {
                                    // If the list item is an object, convert it to a Map automatically
                                    JsonObject obj = item.getAsJsonObject();
                                    Map<String, Object> resultMap = new LinkedHashMap<>();
                                    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                                        // Use the updated castJsonPrimitive (template is null here)
                                        resultMap.put(entry.getKey(), castJsonPrimitive(entry.getValue()));
                                    }
                                    resultList.add(resultMap);
                                } else {
                                    // Otherwise, use the smart primitive detection
                                    resultList.add(castJsonPrimitive(item));
                                }
                            }
                        }
                    }
                    yield (T) resultList;
                }
                default -> {
                    plugin.log(Level.WARNING, "Setting \"" + setting + "\" has an unsupported type! Using default.");
                    yield def;
                }
            };
        } catch (Exception e) {
            plugin.log(Level.WARNING, "Setting \"" + setting + "\" is not a \"" + def.getClass().getSimpleName() + "\" value (config.yml file)! Setting to default value: " + def);
            save();
            return def;
        }
    }

    @Override
    public Set<String> getKeys(String path) {
        JsonElement element = resolve(path, false);

        if (element != null && element.isJsonObject()) {
            return element.getAsJsonObject().keySet();
        }

        return Collections.emptySet();
    }

    @Override
    public void createSection(String path) {
        JsonObject parent = Objects.requireNonNull(resolve(path, true)).getAsJsonObject();
        String key = getFinalKey(path);
        parent.add(key, new JsonObject());
    }

    @Override
    public boolean isConfigurationSection(String path) {
        JsonElement element = resolve(path, false);
        return element != null && element.isJsonObject();
    }

    @Override
    public boolean isString(String path) {
        JsonElement element = resolve(path, false);
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    private JsonElement resolve(String path, boolean createIfMissing) {
        if (path.isEmpty()) return this.root;

        String[] parts = path.split("\\.");
        JsonElement current = this.root;

        int limit = createIfMissing ? parts.length - 1 : parts.length;

        for (int i = 0; i < limit; i++) {
            String part = parts[i];

            if (current instanceof JsonObject obj) {
                if (!obj.has(part)) {
                    if (createIfMissing) {
                        obj.add(part, new JsonObject());
                    } else {
                        return null; // Key doesn't exist and we aren't creating it
                    }
                }
                current = obj.get(part); // Move to the next level
            } else {
                return null; // Part of the path is not an object
            }
        }
        return current;
    }

    private String getFinalKey(String path) {
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }

    private Object castJsonPrimitive(JsonElement item) {
        if (item.isJsonPrimitive()) {
            JsonPrimitive primitive = item.getAsJsonPrimitive();
            if (primitive.isBoolean()) return primitive.getAsBoolean();
            if (primitive.isNumber()) {
                // Check if it's a whole number or a decimal
                double val = primitive.getAsDouble();
                if (val == (int) val) return primitive.getAsInt();
                return val;
            }
            return primitive.getAsString();
        }

        return item.toString();
    }
}