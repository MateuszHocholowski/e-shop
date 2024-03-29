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

}
