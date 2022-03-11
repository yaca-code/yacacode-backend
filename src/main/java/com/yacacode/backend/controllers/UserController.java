package com.yacacode.backend.controllers;

import com.yacacode.backend.models.User;
import com.yacacode.backend.repository.UserRepository;
import com.yacacode.backend.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/api")
@Api(value="PH Ponto")
@CrossOrigin
public class UserController {
    
    @Autowired
	UserRepository repository;

    @Autowired
    UserService service;

	@PostMapping("/auth")
	@ApiOperation(value = "Authentic a user")
	public ResponseEntity<String> auth(@RequestBody User user) {
		return ResponseEntity.ok().body(service.auth(user));
	}

    @GetMapping("/users")
	@ApiOperation(value = "Return a list of users")
	public ResponseEntity<String>  listUsers(){
		return ResponseEntity.ok().body(service.listUsers());
  	}

	@GetMapping("/userId/{id}")
	@ApiOperation(value = "Return a user by id")
	public ResponseEntity<String>  getUser(@PathVariable(value = "id") Long id){
		return ResponseEntity.ok().body(service.getUserById(id));
	}

	@GetMapping("/userName/{name}")
	@ApiOperation(value = "Return a user by name")
	public ResponseEntity<String>  getUserByName(@PathVariable(value = "name") String name){
		return ResponseEntity.ok().body(service.getUserByName(name));
	}

	@PostMapping("/user")
	@ApiOperation(value = "Save a user")
	public ResponseEntity<String> saveUser(@RequestBody User user){
		return ResponseEntity.ok().body(service.saveUser(user));
	}

	@DeleteMapping("/user/{id}")
	@ApiOperation(value = "Delete a user")
	public ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id){
		return ResponseEntity.ok().body(service.deleteUser(id));
	}

	@PostMapping("/handleExtra")
	@ApiOperation(value = "Handle Extra")
	public ResponseEntity<String> handleExtra(@RequestBody String json){
		return ResponseEntity.ok().body(service.handleExtra(json));
	}

	@PostMapping("/updateUser")
	@ApiOperation(value = "Update a user")
	public ResponseEntity<String> updateUser(@RequestBody User user){
		return ResponseEntity.ok().body(service.updateUser(user));
	}
}
