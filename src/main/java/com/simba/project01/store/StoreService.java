package com.simba.project01.store;

import com.simba.project01.user.User;
import com.simba.project01.user.UserRepository;
import com.simba.project01.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService
{
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 가게 생성
    @Transactional
    public Long create(String name, BigDecimal latitude, BigDecimal longitude, StoreCategory category,
                        Long userId, String businessNumber, String address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        BigDecimal lat = latitude.setScale(6, RoundingMode.HALF_UP);
        BigDecimal lng = longitude.setScale(6, RoundingMode.HALF_UP);

        if(storeRepository.existsByLatitudeAndLongitude(lat, lng))
            throw new IllegalArgumentException("이미 같은 장소가 등록되어 있습니다.");

        Store store = new Store();
        store.setName(name);
        store.setLatitude(lat);
        store.setLongitude(lng);
        store.setCategory(category);
        store.setUser(user);
        store.setBusinessNumber(businessNumber);
        store.setAddress(address);

        try {
            storeRepository.save(store);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 같은 장소가 등록되어 있습니다.");
        }
        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.OWNER);
        }
        return store.getId();
    }

    //가게 수정
    @Transactional
    public void update(Long storeId, Long userId, String name, BigDecimal latitude, BigDecimal longitude,
                       StoreCategory category, String businessNumber, String address) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isOwner = store.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 좌표가 들어온 경우에만 setScale 및 중복 체크 진행
        if (latitude != null && longitude != null) {
            BigDecimal lat = latitude.setScale(6, RoundingMode.HALF_UP);
            BigDecimal lng = longitude.setScale(6, RoundingMode.HALF_UP);

            boolean isSameLocation = store.getLatitude().compareTo(lat) == 0
                    && store.getLongitude().compareTo(lng) == 0;
            boolean isDuplicateLocation = storeRepository.existsByLatitudeAndLongitude(lat, lng)
                    && !isSameLocation;

            if (isDuplicateLocation) {
                throw new IllegalArgumentException("해당 위치에 이미 등록된 가게가 존재합니다.");
            }

            store.setLatitude(lat);
            store.setLongitude(lng);
        }

        store.setName(name);
        store.setCategory(category);
        store.setBusinessNumber(businessNumber);
        store.setAddress(address);
    }


    //가게 삭제
    @Transactional
    public void delete(Long storeId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isOwner = store.getUser().getId().equals(user.getId());

        if(!isAdmin && !isOwner)
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        storeRepository.deleteById(storeId);
    }

    //가게 전체 조회
    @Transactional(readOnly = true)
    public List<Store> findAll()
    {
        return storeRepository.findAll();
    }
    //가게 단건 조회
    @Transactional(readOnly = true)
    public Store findById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 가게입니다."));
        return store;
    }

    //가게 카테고리별
    @Transactional(readOnly = true)
    public List<Store> findByCategory(StoreCategory category)
    {
        return storeRepository.findByCategory(category);
    }

    //사용자별 가게 조회
    @Transactional(readOnly = true)
    public List<Store> findByUser(Long userId)
    {
        return storeRepository.findAllByUserId(userId);
    }
}
