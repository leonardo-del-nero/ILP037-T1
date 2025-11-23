package br.com.project.userService.strategy;

import br.com.project.userService.dto.UserDTO;

public interface NotificationStrategy {
    void notificar(UserDTO usuario, String mensagem);
}