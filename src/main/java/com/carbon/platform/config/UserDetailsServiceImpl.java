package com.carbon.platform.config;

import com.carbon.platform.entity.User;
import com.carbon.platform.enums.ApprovalStatus;
import com.carbon.platform.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByMobile(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email or mobile: " + username)));

        // A user is enabled if they are APPROVED or PENDING approval.
        // If they are SUSPENDED, their account is locked.
        boolean enabled = user.getStatus() != ApprovalStatus.SUSPENDED && user.getStatus() != ApprovalStatus.REJECTED;
        boolean accountNonLocked = user.getStatus() != ApprovalStatus.SUSPENDED;

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                enabled,
                true, // credentialsNonExpired
                true, // accountNonExpired
                accountNonLocked,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
