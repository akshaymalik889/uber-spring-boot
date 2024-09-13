package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.SignupDto;
import com.project.uber.uberApp.dto.UserDto;
import com.project.uber.uberApp.entities.Driver;
import com.project.uber.uberApp.entities.User;
import com.project.uber.uberApp.entities.enums.Role;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.project.uber.uberApp.repositories.UserRepository;
import com.project.uber.uberApp.security.JWTService;
import com.project.uber.uberApp.services.AuthService;
import com.project.uber.uberApp.services.DriverService;
import com.project.uber.uberApp.services.RiderService;
import com.project.uber.uberApp.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public String[] login(String email, String password) {

        //use authentication manager to authenticate using email password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // get user from authentication
        User user = (User) authentication.getPrincipal();

        //generate both token for this user
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String tokens[] = {accessToken, refreshToken};
        return tokens;
    }

    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto) {

        //check user already signup or not
        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);

        if(user != null)
            throw new RuntimeConflictException("Can Not Signup , User Already Exists with Email: "+signupDto.getEmail());


        //convert signup dto to User object
        User mappedUser = modelMapper.map(signupDto, User.class);

        //set roles -> by default we set role as RIDER
        mappedUser.setRoles(Set.of(Role.RIDER));

        //bcrypt password
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));

        //save user
        User savedUser = userRepository.save(mappedUser);

        // create user Related Entities
        //1. create Rider
        riderService.createNewRider(savedUser);

        //2. create wallet
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId, String vehicleId) {

        //check user exists with given userId
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("User Not Found With Id: "+userId));

        //check if user already have DRIVER role
        if(user.getRoles().contains(Role.DRIVER))
            throw new RuntimeConflictException("User With Id "+userId+" is Already  a Driver");

        //make user to Driver now

        // 1. create driver Object
        Driver newDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .vehicleId(vehicleId)
                .available(true)
                .build();

        // add new role ie Driver
        user.getRoles().add(Role.DRIVER);
        //save this user
        userRepository.save(user);
        //create driver
        Driver savedDriver = driverService.createNewDriver(newDriver);

        return modelMapper.map(savedDriver, DriverDto.class);
    }

    @Override
    public String refreshToken(String refreshToken) {

        //get User id From Token
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        //find user using  user Id
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("user not found with id: "+userId) );

        //call jwt service again to generate Access Token
        return jwtService.generateAccessToken(user);
    }
}
