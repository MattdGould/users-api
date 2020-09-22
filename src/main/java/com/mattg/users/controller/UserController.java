package com.mattg.users.controller;

import com.mattg.users.dto.UserApiResponseDto;
import com.mattg.users.dto.UserFileDto;
import com.mattg.users.entites.User;
import com.mattg.users.mapper.UserFileDtoToUserEntityMapper;
import com.mattg.users.repository.UserRepository;
import com.mattg.users.service.InitialDataLoadService;
import com.mattg.users.service.UserApiResultsService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserApiResultsService userApiResultsService;

    @Autowired
    private InitialDataLoadService initialDataLoadService;

    @Autowired
    private UserFileDtoToUserEntityMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Finds all users")
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserApiResponseDto> getAllUsers() {
        LOGGER.info("entering /users");
        List<UserApiResponseDto> responseDtos = userApiResultsService.findAllUsers();
        LOGGER.info("Find all users,  Found Size: " + responseDtos.size());
        return responseDtos;
    }

    @Operation(summary = "Finds user with given id")
    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserApiResponseDto getUserWithId(@PathVariable(value = "id") String id) {
        LOGGER.info("entering /users/{id}");
        UserApiResponseDto responseDto = userApiResultsService.findUserById(id);
        LOGGER.info("Users with id " + id);
        return responseDto;
    }

    @Operation(summary = "LoadsData")
    @GetMapping(value = "/load", produces = MediaType.APPLICATION_JSON_VALUE)
    public String loadData() {
        LOGGER.info("entering /loadData");

        try {
            List<UserFileDto> userFileDtoList = initialDataLoadService.loadFromStaticString();
            List<User> userEntities = mapper.mapObjects(userFileDtoList);
            userRepository.saveAll(userEntities);
        } catch (IOException e) {
            LOGGER.error("Error loading data", e);
            return "error loading data";
        }

        return "Done";
    }
}
