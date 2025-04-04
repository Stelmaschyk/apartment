package com.apartment.apartment.component;

import com.apartment.apartment.model.Role;
import com.apartment.apartment.repository.role.RoleRepository;
import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        Arrays.stream(Role.RoleName.values()).forEach(roleName -> {
            roleRepository.findByRole(roleName).orElseGet(() -> {
                Role role = new Role().setRole(roleName);
                return roleRepository.save(role);
            });
        });
    }
}
