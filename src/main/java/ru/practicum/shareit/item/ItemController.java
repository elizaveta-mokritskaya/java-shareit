package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    private final ItemService itemService;

    @GetMapping
    public List<Item> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                            @PathVariable("itemId") Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<Item> getItemsByDescription(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam("text") String text) {
        return itemService.getItemsByDescription(text);
    }

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @RequestBody Item item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("itemId") Long itemId,
                           @RequestBody Item item) {
        return itemService.updateItem(userId, itemId, item.getName(), item.getDescription(), item.getAvailable());
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
