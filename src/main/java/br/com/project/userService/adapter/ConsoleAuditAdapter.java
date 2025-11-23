package br.com.project.userService.adapter;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ConsoleAuditAdapter implements AuditService {
    
    @Override
    public void log(String operacao, String detalhes) {
        // Adapta a chamada para a saída do console com timestamp
        System.out.println(String.format("[%s] AUDITORIA - Op: %s | Info: %s", 
            LocalDateTime.now(), operacao, detalhes));
    }
}