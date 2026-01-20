package com.sep490.g28.hvh.be.cache;

import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.entity.Role;
import com.sep490.g28.hvh.be.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoleCache {
    private final RoleRepository roleRepository;
    private final Map<ERole, Role> cache = new ConcurrentHashMap<>();

    public RoleCache(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    void load() {
        roleRepository.findAll()
                .forEach(r -> cache.put(r.getName(), r));
    }

    public Role getByCode(ERole roleName) {
        Role role = cache.get(roleName);
        //todo xu li exception cho nay
        if (role == null) {
            throw new IllegalStateException("ROLE_NOT_FOUND: " + roleName.name());
        }
        return role;
    }
}
