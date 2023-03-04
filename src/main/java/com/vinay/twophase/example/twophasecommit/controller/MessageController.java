package com.vinay.twophase.example.twophasecommit.controller;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.vinay.twophase.example.twophasecommit.Constants.DESTINATION;

@RestController
@AllArgsConstructor
public class MessageController {

    JmsTemplate jmsTemplate;

    JdbcTemplate jdbcTemplate;

    @GetMapping("/getMessage")
    public Collection<Map<String, String>> getMessages() {
        return jdbcTemplate.query("select * from MESSAGE", (rs, rowNum) -> {
            var map = new HashMap<String, String>();
            map.put("ID", rs.getString("ID"));
            map.put("Message", rs.getString("MESSAGE"));
            return map;
        });
    }

    @PostMapping("/postMessage")
    @Transactional
    public void writeMessage(@RequestBody Map<String, String> payload, @RequestParam Optional <Boolean> rollback) {
        String message = payload.get("message");
        jdbcTemplate.update("INSERT INTO MESSAGE(ID, MESSAGE) VALUES (?, ?)"
                , UUID.randomUUID().toString(), payload.get("message"));
        jmsTemplate.convertAndSend(DESTINATION, message);

        if (rollback.orElse(false)) {
            throw new RuntimeException("Message writing failed");
        }
    }

}
