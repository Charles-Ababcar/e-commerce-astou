package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Page<Client> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public ApiResponse<Client> getClientById(Long id) {
        Client client = clientRepository.findById(id).orElse(null);
        return new ApiResponse<>("Successfully retrieved client with id " + id, client, HttpStatus.UNAUTHORIZED.value());
    }

    public ApiResponse<Client> createClient(Client client) {
        Client createdClient = clientRepository.save(client);
        return new ApiResponse<>("Client créé avec succès", createdClient, HttpStatus.UNAUTHORIZED.value());
    }

    public ApiResponse<Void> deleteClient(Long id) {
        clientRepository.deleteById(id);
        return new ApiResponse<>("Successfully deleted client with id " + id, null, HttpStatus.UNAUTHORIZED.value());
    }
}
