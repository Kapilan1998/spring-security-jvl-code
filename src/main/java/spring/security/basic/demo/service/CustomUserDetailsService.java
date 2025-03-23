package spring.security.basic.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import spring.security.basic.demo.entity.UserEntity;
import spring.security.basic.demo.repository.UserRepository;

import java.util.Collections;

@Configuration
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user details by username from the database.
     * This method is called during authentication to retrieve user information.
     *
     * @param username The username entered during login.
     * @return UserDetails object containing username, password, and authorities.
     * @throws UsernameNotFoundException if the user is not found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // fetch user from database
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(userEntity.getUsername(),userEntity.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
