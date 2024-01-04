package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.mappers.BasketMapper;
import com.orzechazo.eshop.repositories.BasketRepository;
import org.springframework.stereotype.Service;

@Service
public class BasketServiceImpl implements BasketService{
    private final BasketRepository basketRepository;
    private final BasketMapper basketMapper = BasketMapper.INSTANCE;

    public BasketServiceImpl(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }
    @Override
    public BasketDto getBasketByBasketId(Long basketId) {
        return basketMapper.basketToBasketDto(basketRepository.findByBasketId(basketId)
                .orElseThrow(() -> new ResourceNotFoundException("Basket: " + basketId + " doesn't exist in database")));
    }

    @Override
    public BasketDto createBasket(BasketDto basketDto) {
        Basket newBasket = basketMapper.basketDtoToBasket(basketDto);
        if (newBasket == null) {
            newBasket = new Basket();
        }
        Basket.createBasketId(newBasket);
        return saveBasketAndReturnDto(newBasket);
    }

    @Override
    public BasketDto updateBasket(BasketDto basketDto) {
        Basket updateBasket = basketMapper.basketDtoToBasket(basketDto);
        return saveBasketAndReturnDto(updateBasket);
    }

    private BasketDto saveBasketAndReturnDto(Basket basket) {
        return basketMapper.basketToBasketDto(basketRepository.save(basket));
    }

    @Override
    public void deleteBasket(Long basketId) {
        basketRepository.deleteByBasketId(basketId);
    }

}
