package com.mobeom.local_currency.recommend;

import com.mobeom.local_currency.join.IndustryStore;
import com.mobeom.local_currency.proxy.Box;
import com.mobeom.local_currency.store.LatLngVo;
import com.mobeom.local_currency.store.Store;
import lombok.AllArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.web.bind.annotation.*;


import java.util.Calendar;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/recommends")
@AllArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;
    private final Box box;

    @GetMapping("/individualUser/{id}")
    public Map<String, ?> getIndividualUserRecommend(@PathVariable String id) throws TasteException {
        box.clear();
        if(recommendService.findUserByUserIdInRating(id)){
            if(recommendService.findUserBasedRecommend(id).size() !=0){
                box.put("userBased", recommendService.findRecommendStores(recommendService.findUserBasedRecommend(id)));
            } else {box.put("noUserBased", "별점 데이터가 부족합니다. 더 많은 가맹점들을 평가해주세요.");};
        } else {box.put("noUserBased", "별점 데이터가 부족합니다. 더 많은 가맹점들을 평가해주세요.");}

        return box.get();
    }
    @GetMapping("/individualItem/{id}")
    public Map<String, ?> getIndividualItemRecommend(@PathVariable String id) throws TasteException {
        box.clear();
        if(recommendService.isPresentFavorites(id)){
            box.put("itemBased", recommendService.findRecommendStores(recommendService.findItemBasedRecommend(id)));
        } else{
            box.put("noItemBased", "즐겨찾기 데이터가 부족합니다. 더 많은 가맹점들을 즐겨찾기해주세요.");
        };
        return box.get();
    }


    @PostMapping("/all/{id}")
    public Map<String, ?> getAllRecommend(@PathVariable String id, @RequestBody LatLngVo latLng){
        box.clear();
        double lat = latLng.getLat();
        double lng = latLng.getLng();
        System.out.println(lat+lng);

        String[] industryName = {"일반휴게음식", "음료식품", "의원"};
        box.put("industryName", industryName);
        box.put("restaurant", recommendService.findStoresByIndustry("일반휴게음식", lat, lng));
        box.put("drinks", recommendService.findStoresByIndustry("음료식품", lat, lng));
        box.put("hospital", recommendService.findStoresByIndustry("의원", lat, lng));

        box.put("bestStore", recommendService.findBestStores(lat, lng));

        box.put("mostFavorites", recommendService.findMostFavoriteStores(lat, lng));
        box.put("bestRated", recommendService.findBestRatedStores(lat, lng));

        if (recommendService.fetchStoreIdByUserId(id) != null){
            System.out.println(recommendService.fetchStoreIdByUserId(id));
            String favoriteIndustry = recommendService.fetchStoreIdByUserId(id).getMainCode();
            box.put("userFavBased", recommendService.findStoresByIndustry(favoriteIndustry, lat, lng));
            box.put("userFavStore", recommendService.fetchStoreIdByUserId(id).getStoreName());
        }
        else {
             box.put("noFavorite", "줄겨찾기 데이터가 없습니다. 즐겨찾는 가맹점을 등록해보세요.");
        }
        System.out.println("유저의 즐겨찾기 가게"+box.get("userFavStore"));
        System.out.println("유저 즐겨찾기 없을 때 메세지"+box.get("noFavorite"));
        return box.get();
    }





    @GetMapping("/tag/{gender}/{birthYear}")
    public Map<String, ?> findIndustryByProps(@PathVariable String gender, @PathVariable int birthYear) {
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);
        int ageGroup = ((thisYear - birthYear + 1) / 10) * 10;
        if (ageGroup <= 10) {
            ageGroup = 10;
        }
        String userGender = "";
        if (gender.equals("M")) {
            userGender = "남성";
        } else if (gender.equals("F")) {
            userGender = "여성";
        }
        box.clear();
        box.put("byTotal", recommendService.findIndustryByTotal());
        box.put("byGenderAge", recommendService.findIndustryByGenderAndAge(gender, ageGroup));
        box.put("byGender", recommendService.findIndustryByGender(gender));
        box.put("byAge", recommendService.findIndustryByAge(ageGroup));
        box.put("userAgeGroup", ageGroup );
        box.put("userGenderKor", userGender);
        return box.get();
    }

    @GetMapping("/search/{gender}/{ageGroup}")
    public Map<String, List<GenderAge>> findIndustryByTag(@PathVariable String gender, @PathVariable int ageGroup) {
        System.out.println(gender + ageGroup);
        if (gender.equals("none") && ageGroup == 100) {
            box.clear();
            System.out.println("몇개냐" + recommendService.findIndustryByTotal().size());
            box.put("searchResult", recommendService.findIndustryByTotal());
        } else if (gender.equals("none")) {
            box.clear();
            System.out.println("나이 몇개냐" + recommendService.findIndustryByAge(ageGroup).size());
            box.put("searchResult", recommendService.findIndustryByAge(ageGroup));
        } else if (ageGroup == 100) {
            box.clear();
            System.out.println("성별 몇개냐" + recommendService.findIndustryByGender(gender).size());
            box.put("searchResult", recommendService.findIndustryByGender(gender));
        } else {
            box.clear();
            System.out.println("답있음" + recommendService.findIndustryByGenderAndAge(gender, ageGroup).size());
            box.put("searchResult", recommendService.findIndustryByGenderAndAge(gender, ageGroup));
        }
        return box.get();
    }

    @PostMapping("/storesByIndustry/{gender}/{ageGroup}/{option}")
    public Map<String, List<IndustryStore>> findStoresByIndustry(@PathVariable String gender, @PathVariable int ageGroup,
                                                                 @PathVariable int option,
                                                                 @RequestBody LatLngVo latLng) {
        double lat = latLng.getLat();
        double lng = latLng.getLng();
        System.out.println("가게 리스트 입성" + lat + lng);
        List<GenderAge> industryList = findIndustryByTag(gender, ageGroup).get("searchResult");
        if(option == 1) return recommendService.findStoresByIndustryList(industryList, lat, lng);
        else if(option == 2) return recommendService.findMostFavStoresByIndustryList(industryList, lat, lng);
        else return recommendService.findBestRatedStoresByIndustryList(industryList, lat, lng);

    }
}


//    @GetMapping("/resultStore/{gender}/{age}/{town}")
//    public void resultStores(@PathVariable String gender, @PathVariable int age, @PathVariable String town) {
//        List<List<IndustryStore>> industryList = recommendService.fetchStores(gender, age, town);
//        for (List<IndustryStore> storeList : industryList) {
//            for (IndustryStore store : storeList) {
//                System.out.println(store.getStoreName() + store.getAddress());
//            }
//        }
//    }



