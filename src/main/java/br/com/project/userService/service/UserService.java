package br.com.project.userService.service;

import java.util.List;

import org.springframework.stereotype.Service;

// REMOVA a importação do PasswordEncoder
// import org.springframework.security.crypto.password.PasswordEncoder; 
// REMOVA a importação do Mapper (Dozer)
// import com.github.dozermapper.core.Mapper; 

import br.com.project.userService.domain.UserEntity;
import br.com.project.userService.dto.UserDTO;
import br.com.project.userService.exception.RecordNotFoundException;
import br.com.project.userService.repository.UserRepository;
import br.com.project.userService.factory.UserFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserFactory userFactory;


    public UserDTO create(UserDTO dto) {
        UserEntity entity = userFactory.createEntityFromDTO(dto);


        UserEntity result = repository.save(entity);
        
        return userFactory.createDTOFromEntity(result);
    }

    public UserDTO update(long id, UserDTO source) {
        UserEntity target = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        target.setUsername(source.getUsername());
        target.setRoles(source.getRoles());
        
        UserEntity result = repository.save(target);
        
        return userFactory.createDTOFromEntity(result);
    }

    public void delete(long id) {
        UserEntity entity = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        repository.delete(entity);
    }

    public UserDTO findById(long id) {
        UserEntity result = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        
        return userFactory.createDTOFromEntity(result);
    }

    public Iterable<UserDTO> findAll() {
        List<UserEntity> entities = repository.findAll();
        List<UserDTO> dtos = entities.stream()
                .map(userFactory::createDTOFromEntity) 
                .toList();
        return dtos;
    }
}