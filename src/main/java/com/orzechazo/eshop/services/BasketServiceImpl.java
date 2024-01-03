package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.mappers.BasketMapper;
import com.orzechazo.eshop.mappers.UserMapper;
import com.orzechazo.eshop.repositories.BasketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BasketServiceImpl implements BasketService{

    private final BasketRepository basketRepository;
    private final BasketMapper basketMapper = BasketMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public BasketServiceImpl(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }
    @Override
    public BasketDto getBasketByBasketId(Long basketId) {
        return basketMapper.basketToBasketDto(basketRepository.findByBasketId(basketId)
                .orElseThrow(() -> new ResourceNotFoundException("Basket: " + basketId + " doesn't exist in database")));
    }

    @Override
    public BasketDto getBasketByUser(UserDto userDto) {
        Basket returnedBasket = basketRepository.findByUser(userMapper.userDtoToUser(userDto)).
                orElse(basketMapper.basketDtoToBasket(createBasket(userDto)));
        BasketDto basketDto = basketMapper.basketToBasketDto(returnedBasket);
        basketDto.setUser(userDto);
        return basketDto;
    }

    @Override
    public BasketDto createBasket(UserDto userDto) {
        Basket newBasket = new Basket();
        newBasket.setUser(userMapper.userDtoToUser(userDto));
        return saveBasketAndReturnDto(newBasket);
    }

    @Override
    public BasketDto createBasket(BasketDto basketDto) {
        Basket newBasket = basketMapper.basketDtoToBasket(basketDto);
        return saveBasketAndReturnDto(newBasket);
    }

    @Override
    public BasketDto updateBasket(Long basketId, BasketDto basketDto) {
        Basket updateBasket = basketMapper.basketDtoToBasket(basketDto);
        updateBasket.setBasketId(basketId);
        return saveBasketAndReturnDto(updateBasket);
    }

    @Override
    public BasketDto updateBasket(UserDto userDto, BasketDto basketDto) {
        Basket updateBasket = basketMapper.basketDtoToBasket(basketDto);
        updateBasket.setUser(userMapper.userDtoToUser(userDto));
        return saveBasketAndReturnDto(updateBasket);
    }

    private BasketDto saveBasketAndReturnDto(Basket basket) {
        BasketDto savedDto = basketMapper.basketToBasketDto(basketRepository.save(basket));
        if (basket.getUser() != null) {
            UserDto basketUserDto = userMapper.userToUserDto(basket.getUser());
            savedDto.setUser(basketUserDto);
        }
        return savedDto;
    }

    @Override
    public void deleteBasket(Long basketId) {
        basketRepository.deleteByBasketId(basketId);
    }

    @Override
    public void deleteBasketByUser(UserDto userDto) {
        basketRepository.deleteByUser(userMapper.userDtoToUser(userDto));
    }
}
