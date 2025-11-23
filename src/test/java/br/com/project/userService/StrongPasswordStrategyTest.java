package br.com.project.userService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.project.userService.strategy.StrongPasswordStrategy;

@ExtendWith(MockitoExtension.class)
class StrongPasswordStrategyTest {

    @InjectMocks
    private StrongPasswordStrategy strategy;

    @Test
    void deveLancarErroQuandoSenhaForCurta() {
        // Cenário: Senha com menos de 8 caracteres
        String senhaCurta = "Admin1!";
        
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            strategy.validate(senhaCurta);
        });

        Assertions.assertEquals("A senha deve ter pelo menos 8 caracteres.", exception.getMessage());
    }

    @Test
    void deveAceitarSenhaForte() {
        // Cenário: Senha atende a todos os critérios
        String senhaForte = "SenhaForte123!";
        
        Assertions.assertDoesNotThrow(() -> {
            strategy.validate(senhaForte);
        });
    }
}