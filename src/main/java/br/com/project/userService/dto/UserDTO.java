package br.com.project.userService.dto;

import java.io.Serializable;
import java.util.List;

import br.com.project.userService.common.View;
import br.com.project.userService.validation.UserUpdate;
import br.com.project.userService.validation.UserCreate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@UserCreate(groups = View.Create.class)
@UserUpdate(groups = View.Update.class)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDTO implements Serializable {

    @EqualsAndHashCode.Include
    private long id;

    @NotBlank(message = "O campo Username é obrigatório", groups = {View.Create.class, View.Update.class})
    private String username;

    @NotBlank(message = "O campo password é obrigatório", groups = {View.Create.class, View.Update.class})
    private String password;

    @NotEmpty(message = "O campo roles é obrigatório", groups = {View.Create.class, View.Update.class})
    private List<String> roles;

}
