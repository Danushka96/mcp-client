package lk.klass.mcp.mcp_client;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author danushka
 * 2025-07-17
 */
@Service
public class ShoppingCart {
    public record ShoppingItem(String name, int quantity) {}

    private final Map<String, ShoppingItem> shoppingList = new ConcurrentHashMap<>();

    @Tool(name = "addItem", description = "Adds an item to the shopping cart, specify the name and quantity")
    public String addItem(String name, int quantity) {
        shoppingList.compute(name.toLowerCase(), (k, existingItem) -> {
            if (existingItem == null) {
                return new ShoppingItem(name, quantity);
            } else {
                return new ShoppingItem(existingItem.name(), existingItem.quantity() + quantity);
            }
        });
        return "Added " + quantity + " " + name + " to the shopping list";
    }

    @Tool(name = "getItems", description = "Returns the current shopping list with name and quantity")
    public List<ShoppingItem> getShoppingList() {
        return new ArrayList<>(shoppingList.values());
    }
}
