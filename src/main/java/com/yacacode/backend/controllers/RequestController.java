package com.yacacode.backend.controllers;

import com.yacacode.backend.services.RequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api")
@Api(value="PH Ponto")
@CrossOrigin
public class RequestController {

    @Autowired
    private RequestService service;

    @GetMapping("/requestsActives")
    @ApiOperation(value = "Get Requests")
    public ResponseEntity<String> getRequestsActives() {
        return ResponseEntity.ok().body(service.getRequestsActives());
    }

    @GetMapping("/requests")
    @ApiOperation(value = "Get Requests all requests")
    public ResponseEntity<String> getAllRequests() {
        return ResponseEntity.ok().body(service.getAllRequests());
    }

    @GetMapping("/requests/{id}")
    @ApiOperation(value = "Get Requests by id")
    public ResponseEntity<String> getRequests(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.getRequestsById(id));
    }

    @PostMapping("/request/{id}")
    @ApiOperation(value = "Send Request")
    public ResponseEntity<String> auth(@PathVariable Long id, @RequestBody String requestString) {
        return ResponseEntity.ok().body(service.request(id,requestString));
    }

    @PostMapping("/aproveRequest/{id}")
    @ApiOperation(value = "Send Request")
    public ResponseEntity<String> auth(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.aproveRequest(id));
    }

}
