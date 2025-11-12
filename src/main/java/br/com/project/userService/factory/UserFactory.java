package br.com.project.userService.factory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.project.userService.domain.UserEntity;
import br.com.project.userService.dto.UserDTO;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do padrão Factory para criar instâncias
 * de UserEntity e UserDTO.
 *
 * Esta classe encapsula a lógica de "de-para" (mapping) e a
 * lógica de negócios específica da criação (como hash de senha).
 */
@Component
@RequiredArgsConstructor
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    /**
     * Cria uma UserEntity (JPA) a partir de um UserDTO (requisição).
     * Este método é usado principalmente na operação "create".
     *
     * @param dto O DTO vindo da camada de controller.
     * @return Uma nova UserEntity, pronta para ser salva.
     */
    public UserEntity createEntityFromDTO(UserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        // Lógica de criação encapsulada: criptografar a senha
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setRoles(dto.getRoles());
        // ID será nulo por padrão, o que é correto para o JPA (INSERT)
        return entity;
    }

    /**
     * Cria um UserDTO (resposta) a partir de uma UserEntity (JPA).
     * Este método é usado para retornar dados ao cliente.
     *
     * @param entity A entidade vinda do banco de dados.
     * @return Um DTO limpo, pronto para ser enviado como JSON.
     */
    public UserDTO createDTOFromEntity(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setRoles(entity.getRoles());
        
        // NUNCA retorne a senha (nem a hash) para o cliente.
        // O campo 'password' no DTO ficará nulo.
        dto.setPassword(null); 
        
        return dto;
    }
}