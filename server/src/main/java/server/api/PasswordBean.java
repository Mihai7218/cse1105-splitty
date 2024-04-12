package server.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordBean {

    /**
     * function to generate a adminPassword
     * @return a adminPassword
     */
    @Bean
    public String adminPassword() {
        int password = (int)(Math.random() * 999999);
        return String.valueOf(password);
    }


}
