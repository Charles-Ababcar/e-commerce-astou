package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.model.Client;
import com.example.demo.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public ResponseEntity<PageResponse<Client>> getAllClients(Pageable pageable) {
        Page<Client> clients = clientService.getAllClients(pageable);
        return new ResponseEntity<>(new PageResponse<>(clients), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Client>> getClientById(@PathVariable Long id) {
        ApiResponse<Client> response = clientService.getClientById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Client>> createClient(@RequestBody Client client) {
        ApiResponse<Client> response = clientService.createClient(client);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        ApiResponse<Void> response = clientService.deleteClient(id);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}
