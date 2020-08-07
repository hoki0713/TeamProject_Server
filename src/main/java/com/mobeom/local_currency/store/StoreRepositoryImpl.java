package com.mobeom.local_currency.store;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

interface CustomStoreRepository {
    List<Store> findAllStoreByUserDefaultAddr(String defaultAddr);
    Optional<Store> findByAll(String searchWD);
    List<Store> uiList();
    // by store_name, store_type, local_name, road_address, store_phone
}

@Repository
public class StoreRepositoryImpl extends QuerydslRepositorySupport implements CustomStoreRepository {
    @Autowired
    JPAQueryFactory queryFactory;

    public StoreRepositoryImpl() {
        super(Store.class);
    }

    @Override
    public Optional<Store> findByAll(String searchWD) {
        return Optional.empty();
    }

    @Override
    public List<Store> uiList() {
        QStore qStore = QStore.store;
        JPAQueryFactory queryFactory = new JPAQueryFactory(getEntityManager());
        return queryFactory.select(qStore).from(qStore).where(qStore.localName.like("의정부시")).limit(200).fetch();
    }
    
    @Override
    public List<Store> findAllStoreByUserDefaultAddr(String defaultAddr) {
        QStore qStore = QStore.store;
        //System.out.println(defaultAddr.chars().boxed().collect(toList()));
        String fixedAddr = defaultAddr.replace((char)160, (char)32).trim();
        //System.out.println(fixedAddr.chars().boxed().collect(toList()));
        String[] defaultAddrArr = fixedAddr.split(" ");
        //System.out.println(Arrays.asList(defaultAddrArr));
        List<Store> resultList = queryFactory.selectFrom(qStore).where(qStore.localName.like(defaultAddrArr[1])).fetch();
        return resultList;
    }
}
