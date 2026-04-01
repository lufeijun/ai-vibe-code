package com.example.demo;

import org.junit.jupiter.api.Test;

import com.example.demo.controller.HelloWorldController;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

    @Test
    void hello_ShouldReturnHelloWorld() {
        HelloWorldController controller = new HelloWorldController();
        String result = controller.hello();
        assertEquals("Hello World!", result);
    }
}
