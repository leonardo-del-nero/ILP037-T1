package br.com.project.userService.strategy;

public interface PasswordStrategy {
    void validate(String password);
}