package dk.tij.winterweather.ui;

import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UIManager {
    private UIManager() {}

    private static final Map<Inventory, CustomUI> open = new ConcurrentHashMap<>();

    public static void registerUI(CustomUI ui) {
        open.put(ui.inventory, ui);
    }

    public static CustomUI get(Inventory inventory) {
        return open.get(inventory);
    }
}
