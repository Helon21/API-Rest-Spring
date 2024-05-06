package com.practice.trainingapi.web.dto.mapper;

import com.practice.trainingapi.entity.Client;
import com.practice.trainingapi.web.dto.ClientCreateDto;
import com.practice.trainingapi.web.dto.ClientResponseDto;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.reactive.function.client.ClientResponse;

@NoArgsConstructor
public class ClientMapper {

    public static Client toClient(ClientCreateDto dto) {
        return new ModelMapper().map(dto, Client.class);
    }

    public static ClientResponseDto toDto(Client client) {
        return new ModelMapper().map(client, ClientResponseDto.class);
    }
}
