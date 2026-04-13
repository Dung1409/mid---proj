package com.soa.menuservice.controller;

import com.soa.menuservice.entity.MenuItem;
import com.soa.menuservice.repository.MenuItemRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
public class MenuController {
    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @PostConstruct
    public void seedDefaultMenu() {
        if (menuItemRepository.count() == 0) {
            menuItemRepository.saveAll(List.of(
                    new MenuItem(1, "Pho Bo", BigDecimal.valueOf(45000)),
                    new MenuItem(2, "Com Ga", BigDecimal.valueOf(40000)),
                    new MenuItem(3, "Banh Mi", BigDecimal.valueOf(25000))));
        }
    }

    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getMenuItems() {
        List<Map<String, Object>> items = menuItemRepository.findAll().stream()
                .map(item -> Map.<String, Object>of(
                        "id", item.getId(),
                        "name", item.getName(),
                        "price", item.getPrice()))
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Not found items"));
        }

        return ResponseEntity.ok(Map.of("items", items));
    }
}