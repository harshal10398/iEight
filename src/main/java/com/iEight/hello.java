package com.iEight;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {
    static String name = "World";

    @RequestMapping(method = RequestMethod.GET, path = "/hello", produces = MediaType.TEXT_HTML_VALUE)
    public String greeting() {
        return "<html><body><h1>Hello, " + name + "</h1><form name='update' method=post><input type=text name=name><input type=submit></form></body></html>";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/hello", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public String setName(@RequestParam(name = "name") String name) {
        hello.name = name;
        return "ok!";
    }
}
