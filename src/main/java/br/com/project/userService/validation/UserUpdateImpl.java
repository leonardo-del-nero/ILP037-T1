package br.com.project.userService.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.HandlerMapping;

import br.com.project.userService.common.FieldMessage;
import br.com.project.userService.dto.UserDTO;
import br.com.project.userService.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserUpdateImpl implements ConstraintValidator<UserUpdate,UserDTO> {

    private final UserRepository repository;

	private final HttpServletRequest request;

    @SuppressWarnings("unchecked")
	@Override
    public boolean isValid(UserDTO value, ConstraintValidatorContext context) {
		
        List<FieldMessage> list = new ArrayList<FieldMessage>();

		Map<String, String> map = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long uriId = Long.parseLong(map.get("id"));
		
		//Check Code
		var result = repository.findByUsernameIgnoreCase(value.getUsername());
		if(result.isPresent() && !result.get().getId().equals(uriId)) list.add(new FieldMessage("username", "username already exists"));

		list.stream().forEach(e -> {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.message()).addPropertyNode(e.fieldName()).addConstraintViolation();
		});
		
		return list.isEmpty();
    }
}
