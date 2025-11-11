package br.com.project.userService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.github.dozermapper.core.Mapper;

import br.com.project.userService.domain.UserEntity;
import br.com.project.userService.dto.UserDTO;
import br.com.project.userService.exception.RecordNotFoundException;
import br.com.project.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final Mapper mapper;

    private final PasswordEncoder passwordEncoder;

    public UserDTO create(UserDTO dto) {
        UserEntity entity = mapper.map(dto, UserEntity.class);
        entity.setId(null); // Garantir que o ID seja nulo para criação

        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        UserEntity result = repository.save(entity);
        return mapper.map(result, UserDTO.class);
    }

    public UserDTO update(long id, UserDTO source) {
        UserEntity target = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        target.setUsername(source.getUsername());
        target.setRoles(source.getRoles());
        UserEntity result = repository.save(target);
        return mapper.map(result, UserDTO.class);
    }

    public void delete(long id) {
        UserEntity entity = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        repository.delete(entity);
    }

    public UserDTO findById(long id) {
        UserEntity result = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        return mapper.map(result, UserDTO.class);
    }

    public Iterable<UserDTO> findAll() {
        List<UserEntity> entities = repository.findAll();
        List<UserDTO> dtos = entities.stream()
                .map(entity -> mapper.map(entity, UserDTO.class))
                .toList();
        return dtos;
    }
}
