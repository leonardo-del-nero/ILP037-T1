package br.com.project.userService.strategy;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import br.com.project.userService.dto.UserDTO;

@Component
@Primary // Define esta como a estratégia padrão caso haja outras
public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public void notificar(UserDTO usuario, String mensagem) {
        // Lógica encapsulada de envio de e-mail
        System.out.println(">>> STRATEGY: Enviando E-mail para " + usuario.getUsername());
        System.out.println(">>> Mensagem: " + mensagem);
    }
}