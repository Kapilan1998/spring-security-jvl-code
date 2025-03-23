package spring.security.basic.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.security.basic.demo.security.JwtFilter;
import spring.security.basic.demo.service.CustomUserDetailsService;

import java.util.List;

@Configuration  // adding configuration to my project
@EnableWebSecurity  // customize the spring security and modifying new things related to security
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auths ->
                        auths.requestMatchers(HttpMethod.POST,"/api/users").permitAll()     // this POST api can be access without login
                                .requestMatchers("/api/users/**").authenticated()      // can't access without login
                                .requestMatchers("/api/home").permitAll()          // can access without login
                                .anyRequest().permitAll())                              // other than above-mentioned urls, others will be allowed
                .csrf(myCsrf ->myCsrf.disable())            // disable csrf in api wise testing
//                .formLogin(form -> form.permitAll()    // allow form based login page
//                        .defaultSuccessUrl("/api/home/dashboard"))                   // after successful login it will be redirected to this page

                // Enforce stateless session management (JWT-based authentication)
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // for the api no need to create session
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);        //             // Add the JWT filter before UsernamePasswordAuthenticationFilter

        return httpSecurity.build();
    }

    /**
     * Creates and returns a UserDetailsService bean.
     * This service is responsible for retrieving user details from the database.
     * If needed, an in-memory user can be used instead (currently i commented them).
     *
     * @return an instance of CustomUserDetailsService which fetches user details from DB.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // testing by in memory user details
//        UserDetails user = User.withUsername("Edwin")
//                .password(passwordEncoder.encode("1234"))
//                .roles("USER")
//                .build();       // by this in memory user will be created
//
//        UserDetails admin = User.withUsername("Head")
//                .password(passwordEncoder.encode("admin123"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
        return  new CustomUserDetailsService();
    }

    /**
     * Creates and returns a DaoAuthenticationProvider bean.
     * This provider is responsible for:
     * - Authenticating users based on database-stored credentials.
     * - Using the configured UserDetailsService to fetch user details.
     * - Verifying passwords using a PasswordEncoder.
     *
     * @return a configured instance of DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
    }
}
