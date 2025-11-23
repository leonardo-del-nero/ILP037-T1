package br.com.project.userService.adapter;

public interface AuditService {
    void log(String operacao, String detalhes);
}