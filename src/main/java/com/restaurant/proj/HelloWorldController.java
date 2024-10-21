/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.proj;

/**
 *
 * @author Nidhi
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    // This method will handle GET requests to "/hello"
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World!";
    }
}