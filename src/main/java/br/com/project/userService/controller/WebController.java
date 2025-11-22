package br.com.project.userService.controller;

import br.com.project.userService.dto.UserDTO;
import br.com.project.userService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class WebController {

    private final UserService userService;

    public WebController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/set-tenant")
    public String setTenant(@RequestParam String tenant, HttpServletRequest request) {
        request.getSession().setAttribute("currentTenant", tenant);

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/set-tenant")) {
            return "redirect:" + referer;
        }
        return "redirect:/";
    }

    private String getTenantFromSession(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("currentTenant");
    }

    @GetMapping
    public String index(Model model, HttpServletRequest request) {
        String tenant = getTenantFromSession(request);
        model.addAttribute("currentTenant", tenant);
        return "index";
    }

    @GetMapping("/users")
    public String listUsers(Model model, HttpServletRequest request) {
        String tenant = getTenantFromSession(request);
        model.addAttribute("currentTenant", tenant);
        return "users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model, HttpServletRequest request) {
        String tenant = getTenantFromSession(request);
        model.addAttribute("currentTenant", tenant);
        return "user-create"; // template específico para criação
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        try {
            String tenant = getTenantFromSession(request);
            
            // verificar se o tenant está definido
            if (tenant == null) {
                System.out.println("ERRO: Tenant não definido na sessão");
                return "redirect:/users?error=Tenant não definido";
            }

            // buscar o usuário no tenant atual
            UserDTO user = userService.findById(id);
            
            if (user == null) {
                System.out.println("ERRO: Usuário não encontrado no tenant " + tenant);
                return "redirect:/users?error=Usuário não encontrado no tenant atual";
            }

            model.addAttribute("currentTenant", tenant);
            model.addAttribute("user", user);
            
            return "user-edit";
        } catch (Exception e) {
            System.out.println("ERRO ao buscar usuário para edição: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/users?error=Usuário não encontrado";
        }
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute UserDTO userDTO,
                           @RequestParam("rolesInput") String rolesInput,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            // primeiro tentar pegar da sessão, se não conseguir usar o header
            String tenant = getTenantFromSession(request);
            if (tenant == null) {
                // tentar pegar do header diretamente
                tenant = request.getHeader("x-tenant");
                if (tenant != null) {
                    // salvar na sessão para futuras requisições
                    request.getSession().setAttribute("currentTenant", tenant);
                }
            }
            
            if (tenant == null) {
                redirectAttributes.addAttribute("error", "Tenant não definido");
                return "redirect:/users/new";
            }

            // converter roles
            List<String> rolesList = Arrays.stream(rolesInput.split(","))
                    .map(String::trim)
                    .filter(role -> !role.isEmpty())
                    .collect(Collectors.toList());
            userDTO.setRoles(rolesList);
            
            userService.create(userDTO);
            
            redirectAttributes.addAttribute("success", "Usuário criado com sucesso!");
            return "redirect:/users";
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            redirectAttributes.addAttribute("error", "Erro ao criar usuário: " + e.getMessage());
            return "redirect:/users/new";
        }
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable("id") Long id,
                           @ModelAttribute UserDTO userDTO,
                           @RequestParam("rolesInput") String rolesInput,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            String tenant = getTenantFromSession(request);
            if (tenant == null) {
                redirectAttributes.addAttribute("error", "Tenant não definido");
                return "redirect:/users/" + id + "/edit";
            }

            // converter roles de string para List
            List<String> rolesList = Arrays.stream(rolesInput.split(","))
                    .map(String::trim)
                    .filter(role -> !role.isEmpty())
                    .collect(Collectors.toList());
            userDTO.setRoles(rolesList);
            
            userService.update(id, userDTO);
            redirectAttributes.addAttribute("success", "Usuário atualizado com sucesso!");
            return "redirect:/users";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Erro ao atualizar usuário: " + e.getMessage());
            return "redirect:/users/" + id + "/edit";
        }
    }
}