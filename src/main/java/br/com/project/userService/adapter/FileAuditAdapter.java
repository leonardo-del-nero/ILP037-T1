package br.com.project.userService.adapter;

import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Component
public class FileAuditAdapter implements AuditService {

    private static final String FILE_NAME = "audit.log";

    @Override
    public void log(String operacao, String detalhes) {
        // Try-with-resources garante que o arquivo fecha sozinho
        try (FileWriter fileWriter = new FileWriter(FILE_NAME, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            
            printWriter.printf("[%s] OP: %s | DETALHES: %s%n", 
                LocalDateTime.now(), operacao, detalhes);
                
            System.out.println("LOG SALVO NO ARQUIVO: " + FILE_NAME); // Só pra te avisar no console
            
        } catch (IOException e) {
            System.err.println("Erro ao escrever no log de auditoria: " + e.getMessage());
        }
    }
}