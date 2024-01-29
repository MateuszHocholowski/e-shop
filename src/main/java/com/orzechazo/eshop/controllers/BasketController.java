package com.orzechazo.eshop.controllers;

import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.services.BasketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/baskets")
public class BasketController {
    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }
    @GetMapping("/{basketId}")
    @ResponseStatus(HttpStatus.OK)
    public BasketDto getBasketByBasketId(@PathVariable String basketId) {
        return basketService.getBasketDtoByBasketId(basketId);
    }
    @PutMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public BasketDto createBasket(@RequestBody BasketDto basketDto) {
        return basketService.createBasket(basketDto);
    }
    @PostMapping("/update/{basketId}")
    @ResponseStatus(HttpStatus.OK)
    public BasketDto updateBasket(@PathVariable String basketId, @RequestBody BasketDto basketDto) {
        return basketService.updateBasket(basketDto);
    }
    @DeleteMapping("/delete/{basketId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBasket(@PathVariable String basketId) {
        basketService.deleteBasket(basketId);
    }
    @GetMapping("/{basketId}/products")
    @ResponseStatus(HttpStatus.OK)
    public Map<ProductDto, Integer> fetchAllProductFromBasket(@PathVariable String basketId) {
        return basketService.fetchAllProductsFromBasket(basketId);
    }

    @PostMapping("/{basketId}/products/add/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public BasketDto addProductToBasket(@PathVariable String productName, @PathVariable String basketId,
                                        @RequestParam(defaultValue = "1") String amount) {
        return basketService.addProductToBasket(productName, basketId, Integer.parseInt(amount));
    }
    @PostMapping("/{basketId}/products/subtract/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public BasketDto subtractProductFromBasket(@PathVariable String productName, @PathVariable String basketId,
                                               @RequestParam(defaultValue = "1") String amount) {
        return basketService.subtractProductFromBasket(productName, basketId, Integer.parseInt(amount));
    }
    @PostMapping("/{basketId}/products/remove/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public BasketDto removeProductFromBasket(@PathVariable String productName, @PathVariable String basketId) {
        return basketService.removeProductFromBasket(productName, basketId);
    }
}
