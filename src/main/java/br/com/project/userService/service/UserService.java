package br.com.project.userService.service;

import java.util.List;
import org.springframework.stereotype.Service;

// Imports dos novos padrões
import br.com.project.userService.adapter.AuditService;
import br.com.project.userService.strategy.PasswordStrategy;

import br.com.project.userService.domain.UserEntity;
import br.com.project.userService.dto.UserDTO;
import br.com.project.userService.exception.RecordNotFoundException;
import br.com.project.userService.factory.UserFactory;
import br.com.project.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserFactory userFactory;
    
    // Padrões Injetados
    private final AuditService auditService;      // Adapter (File)
    private final PasswordStrategy passwordStrategy; // Strategy (Validação)

    public UserDTO create(UserDTO dto) {
        // 1. STRATEGY: Valida a senha antes de qualquer coisa
        // Se a senha for fraca, ele estoura um erro e nem salva no banco
        passwordStrategy.validate(dto.getPassword());

        UserEntity entity = userFactory.createEntityFromDTO(dto);
        UserEntity result = repository.save(entity);
        UserDTO resultDTO = userFactory.createDTOFromEntity(result);

        // 2. ADAPTER: Grava no arquivo audit.log
        auditService.log("CREATE", "Usuario criado com sucesso. ID: " + result.getId());
        
        return resultDTO;
    }

    public UserDTO update(long id, UserDTO source) {
        // Também validamos na atualização!
        if (source.getPassword() != null && !source.getPassword().isEmpty()) {
             passwordStrategy.validate(source.getPassword());
        }

        UserEntity target = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        target.setUsername(source.getUsername());
        target.setRoles(source.getRoles());
        
        // Se a senha foi enviada, atualiza (cuidado: idealmente criptografaríamos aqui tbm, 
        // mas o foco é o padrão Strategy)
        // target.setPassword(...) 
        
        UserEntity result = repository.save(target);
        
        // Grava no arquivo
        auditService.log("UPDATE", "Usuario atualizado. ID: " + id);
        
        return userFactory.createDTOFromEntity(result);
    }

    public void delete(long id) {
        UserEntity entity = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        repository.delete(entity);
        
        // Grava no arquivo
        auditService.log("DELETE", "Usuario removido. ID: " + id);
    }

    public UserDTO findById(long id) {
        UserEntity result = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        return userFactory.createDTOFromEntity(result);
    }

    public Iterable<UserDTO> findAll() {
        List<UserEntity> entities = repository.findAll();
        return entities.stream()
                .map(userFactory::createDTOFromEntity) 
                .toList();
    }
}
