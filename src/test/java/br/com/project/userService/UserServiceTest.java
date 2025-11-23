package br.com.project.userService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.project.userService.adapter.AuditService;
import br.com.project.userService.domain.UserEntity;
import br.com.project.userService.dto.UserDTO;
import br.com.project.userService.factory.UserFactory;
import br.com.project.userService.repository.UserRepository;
import br.com.project.userService.service.UserService;
import br.com.project.userService.strategy.PasswordStrategy;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository repository;
    @Mock private UserFactory userFactory;
    @Mock private AuditService auditService;
    @Mock private PasswordStrategy passwordStrategy;

    @InjectMocks
    private UserService userService;

    @Test
    void deveSalvarUsuarioCorretamente() {
        // Cenário
        UserDTO dto = new UserDTO();
        dto.setPassword("SenhaForte123!");
        
        UserEntity entityMock = new UserEntity();
        entityMock.setId(1L);

        // Comportamento dos Mocks
        when(userFactory.createEntityFromDTO(dto)).thenReturn(entityMock);
        when(repository.save(entityMock)).thenReturn(entityMock);
        when(userFactory.createDTOFromEntity(entityMock)).thenReturn(dto);

        // Ação
        userService.create(dto);

        // Verificações: Os padrões foram chamados?
        verify(passwordStrategy, times(1)).validate("SenhaForte123!"); // Strategy
        verify(repository, times(1)).save(entityMock);                 // Repository
        verify(auditService, times(1)).log(any(), any());              // Adapter
    }
}