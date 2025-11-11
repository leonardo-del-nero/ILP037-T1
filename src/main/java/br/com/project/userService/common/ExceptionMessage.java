package br.com.project.userService.common;

import java.util.Date;
import java.util.List;

public record ExceptionMessage(Date timestamp, String erro, String mensagem, List<FieldMessage> trace) {

}
