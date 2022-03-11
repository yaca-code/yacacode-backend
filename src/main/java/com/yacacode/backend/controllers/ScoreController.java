package com.yacacode.backend.controllers;

import com.yacacode.backend.services.ScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api")
@Api(value="PH Ponto")
@CrossOrigin
public class ScoreController {

    @Autowired
    private ScoreService service;

    @PostMapping("/beatTime/{id}")
    @ApiOperation(value = "Beat time")
    public ResponseEntity<String> auth(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.beatTime(id));
    }

    @GetMapping("/scoreMonth/{id}/{month}/{year}")
    @ApiOperation(value = "Score month")
    public ResponseEntity<String> scoreMonth(@PathVariable(value="id") Long id, @PathVariable(value="month") Integer month, @PathVariable(value="year") Integer year) {
        return ResponseEntity.ok().body(service.findScoreByUserIdMonth(id, month, year));
    }

    @GetMapping("/teste")
    @ApiOperation(value = "Teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok().body(service.getDate().toString());
    }
}
