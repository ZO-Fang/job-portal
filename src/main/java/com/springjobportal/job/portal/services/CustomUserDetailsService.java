package com.springjobportal.job.portal.services;

import com.springjobportal.job.portal.entity.Users;
import com.springjobportal.job.portal.repository.UsersRepository;
import com.springjobportal.job.portal.util.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UsersRepository usersRepository;

    @Autowired
    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users uers = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("could not find this user"));
        return new CustomUserDetails(uers);
    }
}
