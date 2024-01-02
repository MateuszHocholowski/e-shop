package com.orzechazo.eshop.services;

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
}
