package com.mobeom.local_currency.admin;


import com.mobeom.local_currency.join.SalesVoucher;
import com.mobeom.local_currency.proxy.Box;
import com.mobeom.local_currency.sales.Sales;
import com.mobeom.local_currency.user.User;
import com.mobeom.local_currency.user.UserRepository;
import com.querydsl.core.Tuple;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
interface AdminService{
    
    Map<String,Long> localTotalChart();
    Map<String,Long> userLocalGenderChart(String localSelect);
    Map<String,Integer> userAgeTotal(String localSelect);
    Map<?,?> joinChart(LocalDate joinStartDate,LocalDate joinEndDate);
    Long storeLocalsChart(String localSelect);
    Map<String,Long> storeTypeChart();
    List<SalesVoucher> salesMonthChart();
    List<User> getAllUserList();
    Optional<User> findOneUser(String userId);
    Map<String,Integer> useLocalChart(String localName,LocalDate startDate,LocalDate endDate);
    Map<String,SalesVoucher> voucherNameChart(String voucherName,String start,String end);
    Map<String,Integer> useTotalLocalChart();
}

@Service
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AdminServiceImpl(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Map<String,Long> localTotalChart() {
       return adminRepository.localTotalChart();
    }

    @Override
    public Map<String, Long> userLocalGenderChart(String localSelect) {
        return adminRepository.userLocalGenderChart(localSelect);
    }

    @Override
    public Map<String, Integer> userAgeTotal(String localSelect) {
        return adminRepository.userAgeChart(localSelect);
    }



    @Override
    public Map<?, ?> joinChart(LocalDate joinStartDate,LocalDate joinEndDate) {
        return adminRepository.joinDateChart(joinStartDate,joinEndDate);
    }

    @Override
    public Long storeLocalsChart(String localSelect) {
        return adminRepository.storeLocalsChart(localSelect);
    }

    @Override
    public Map<String, Long> storeTypeChart() {
        return adminRepository.storeTypeLocal();
    }

    @Override
    public List<SalesVoucher> salesMonthChart() {
        return adminRepository.salesMonthChart();
    }

    @Override
    public List<User> getAllUserList() {
        return userRepository.findAll(); //페이지네이션
    }

    @Override
    public Optional<User> findOneUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public Map<String,Integer> useLocalChart(String localName,LocalDate startDate,LocalDate endDate) {

        return adminRepository.useLocalChart(localName,startDate,endDate);
    }

    @Override
    public Map<String,SalesVoucher> voucherNameChart(String voucherName,String start,String end) {
        return adminRepository.voucherNameChart(voucherName,start,end);
    }

    @Override
    public Map<String, Integer> useTotalLocalChart() {
        return adminRepository.useTotalLocalChart();
    }


}
