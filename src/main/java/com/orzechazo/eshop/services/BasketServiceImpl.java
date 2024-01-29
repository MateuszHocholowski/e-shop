package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.mappers.BasketMapper;
import com.orzechazo.eshop.mappers.ProductMapper;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BasketServiceImpl implements BasketService{
    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final BasketMapper basketMapper = BasketMapper.INSTANCE;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public BasketServiceImpl(BasketRepository basketRepository, ProductRepository productRepository) {
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
    }
    @Override
    public BasketDto getBasketDtoByBasketId(String basketId) {
        return basketMapper.basketToBasketDto(getBasketByBasketId(basketId));
    }

    @Override
    public BasketDto createBasket(BasketDto basketDto) {
        Basket newBasket = basketMapper.basketDtoToBasket(basketDto);
        Basket.createBasketId(newBasket);
        return saveBasketAndReturnDto(newBasket);
    }

    @Override
    public BasketDto updateBasket(BasketDto basketDto) {
        Basket currentBasket = getBasketByBasketId(basketDto.getBasketId());
        Basket updateBasket = basketMapper.basketDtoToBasket(basketDto);
        updateBasket.setId(currentBasket.getId());
        updateBasket.setProducts(currentBasket.getProducts());
        return saveBasketAndReturnDto(updateBasket);
    }

    private BasketDto saveBasketAndReturnDto(Basket basket) {
        return basketMapper.basketToBasketDto(basketRepository.save(basket));
    }

    @Override
    public void deleteBasket(String basketId) {
        basketRepository.deleteByBasketId(basketId);
    }
    private Basket getBasketByBasketId(String basketId) {
        return basketRepository.findByBasketId(basketId).orElseThrow(() -> new ResourceNotFoundException(
                "Basket: " + basketId + " doesn't exist in database"));
    }

    @Override
    public BasketDto addProductToBasket(String productName, String basketId, int amount) {
        Basket currentBasket = getBasketByBasketId(basketId);
        Product productToAdd = getProductByName(productName);
        return updateBasketProducts(currentBasket, productToAdd, true, amount);
    }

    @Override
    public BasketDto subtractProductFromBasket(String productName, String basketId, int amount) {
        Basket currentBasket = getBasketByBasketId(basketId);
        Product productToSubtract = getProductByName(productName);
        return updateBasketProducts(currentBasket, productToSubtract, false, amount);
    }

    @Override
    public BasketDto removeProductFromBasket(String productName, String basketId) {
        Optional<Integer> productAmount = Optional.of(getBasketDtoByBasketId(basketId)
                .getProductNamesMap().get(productName));
        return subtractProductFromBasket(productName,basketId, productAmount.orElse(0));
    }

    @Override
    public Map<ProductDto, Integer> fetchAllProductsFromBasket(String basketId) {
        Basket currentBasket = getBasketByBasketId(basketId);
        return currentBasket.getProducts().entrySet().stream()
                .map(entry -> Map.entry(productMapper.productToProductDto(entry.getKey()),entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    private BasketDto updateBasketProducts(Basket basketToUpdate, Product productToUpdate,
                                           boolean isAddition, int amount) {
        Map<Product, Integer> currentProducts = basketToUpdate.getProducts();
        int finalAmount = isAddition ? Math.min(amount, productToUpdate.getAmount()) : (amount * -1);

        currentProducts.computeIfPresent(productToUpdate, (k,v) -> v + finalAmount);
        currentProducts.putIfAbsent(productToUpdate, finalAmount);

        if (currentProducts.get(productToUpdate) <= 0) {
            currentProducts.remove(productToUpdate);
        }

        basketToUpdate.setProducts(currentProducts);
        basketToUpdate.setTotalPrice(countTotalPrice(currentProducts));

        return saveBasketAndReturnDto(basketToUpdate);
    }
    private BigDecimal countTotalPrice(Map<Product, Integer> basketProducts) {
        Optional<BigDecimal> totalPriceOptional = basketProducts.entrySet().stream()
                .map(entry -> entry.getKey().getGrossPrice()
                        .multiply(new BigDecimal(entry.getValue().toString())))
                .reduce(BigDecimal::add);
        return totalPriceOptional.orElse(new BigDecimal("0"));
    }
    private Product getProductByName(String productName) {
        return productRepository.findByName(productName)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + productName
                        + " doesn't exist in database."));
    }
}
