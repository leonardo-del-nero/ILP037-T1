package br.com.project.userService.service;

import java.util.List;

import org.springframework.stereotype.Service;

// Importações dos novos padrões
import br.com.project.userService.adapter.AuditService;
import br.com.project.userService.strategy.NotificationStrategy;

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
    private final UserFactory userFactory; // Padrão Criacional (Factory) já existente
    
    // Novos Padrões Injetados
    private final AuditService auditService;              // Padrão Estrutural (Adapter)
    private final NotificationStrategy notificationStrategy; // Padrão Comportamental (Strategy)

    public UserDTO create(UserDTO dto) {
        UserEntity entity = userFactory.createEntityFromDTO(dto);
        UserEntity result = repository.save(entity);
        UserDTO resultDTO = userFactory.createDTOFromEntity(result);
        
        // Usa o Adapter para registrar auditoria
        auditService.log("CRIAR_USUARIO", "ID criado: " + result.getId());
        
        // Usa o Strategy para notificar
        notificationStrategy.notificar(resultDTO, "Bem-vindo ao sistema!");
        
        return resultDTO;
    }

    public UserDTO update(long id, UserDTO source) {
        UserEntity target = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        target.setUsername(source.getUsername());
        target.setRoles(source.getRoles());
        
        UserEntity result = repository.save(target);
        
        // Usa o Adapter
        auditService.log("ATUALIZAR_USUARIO", "ID atualizado: " + id);
        
        return userFactory.createDTOFromEntity(result);
    }

    public void delete(long id) {
        UserEntity entity = repository.findById(id).orElseThrow(RecordNotFoundException::new);
        repository.delete(entity);
        
        // Usa o Adapter
        auditService.log("DELETAR_USUARIO", "ID removido: " + id);
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