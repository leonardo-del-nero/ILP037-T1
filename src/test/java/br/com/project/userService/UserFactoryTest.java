package br.com.project.userService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.project.userService.domain.UserEntity;
import br.com.project.userService.dto.UserDTO;
import br.com.project.userService.factory.UserFactory;

@ExtendWith(MockitoExtension.class)
class UserFactoryTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserFactory userFactory;

    @Test
    void deveCriarEntidadeComSenhaCriptografada() {
        // Dados de entrada simulados
        UserDTO dto = new UserDTO();
        dto.setUsername("aluno");
        dto.setPassword("senha123");
        dto.setRoles(List.of("ADMIN"));

        // Mock: Quando o encoder for chamado, retorna um hash falso
        when(passwordEncoder.encode("senha123")).thenReturn("HASH_SECRETO_XYZ");

        // Execução
        UserEntity entity = userFactory.createEntityFromDTO(dto);

        // Validação: A entidade tem o hash, não a senha limpa?
        assertEquals("HASH_SECRETO_XYZ", entity.getPassword());
        assertEquals("aluno", entity.getUsername());
    }

    @Test
        void deveOcultarSenhaNoDTO() {
            // Cenário: Objeto vindo do banco
            UserEntity entity = new UserEntity();
            entity.setId(1L); // <--- CORREÇÃO: O ID não pode ser nulo!
            entity.setUsername("aluno");
            entity.setPassword("HASH_REAL");

            // Execução
            UserDTO dto = userFactory.createDTOFromEntity(entity);

            // Validação: A senha deve ser nula para não vazar na API
            assertNull(dto.getPassword());
        }
}