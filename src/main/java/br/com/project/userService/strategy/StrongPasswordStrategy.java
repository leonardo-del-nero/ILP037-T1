package br.com.project.userService.strategy;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class StrongPasswordStrategy implements PasswordStrategy {

    @Override
    public void validate(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        // Verifica se tem pelo menos um caracter especial ou número (Simples e funcional)
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?0-9].*")) {
            throw new IllegalArgumentException("A senha deve conter pelo menos um número ou caracter especial.");
        }
    }
}