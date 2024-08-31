package com.cringee.simplescreensharing.controllers;

import com.cringee.simplescreensharing.annotations.HxRequestOnly;
import com.cringee.simplescreensharing.dto.UserDto;
import com.cringee.simplescreensharing.models.User;
import com.cringee.simplescreensharing.services.RoleService;
import com.cringee.simplescreensharing.services.SseEmitterService;
import com.cringee.simplescreensharing.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
@Controller
@Validated
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final SseEmitterService sseEmitterService;
    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();

    public UserController(UserService userService, RoleService roleService, SseEmitterService sseEmitterService) {
        this.userService = userService;
        this.roleService = roleService;
        this.sseEmitterService = sseEmitterService;
    }

    @GetMapping("/")
    public String all(Model model, HttpServletRequest request) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "pages/all_users";
    }

    @HxRequestOnly
    @GetMapping("/user_form")
    public String teamsForm(UserDto userDto, Model model) {
        model.addAttribute("userDto", userDto);
        model.addAttribute("roles", roleService.findAll());
        return "layouts/forms/add-new-user";
    }

    @HxRequestOnly
    @GetMapping("/user_form/{id}")
    public String teamsFormById(@PathVariable Long id, Model model) {
        UserDto userDto = userService.findById(id);
        model.addAttribute("userDto", userDto);
        model.addAttribute("roles", roleService.findAll());
        return "layouts/forms/add-new-user";
    }

    @PostMapping("/")
    public String add(@Valid @ModelAttribute("userDto") UserDto userDto,
                      BindingResult result, Model model,
                      HttpServletResponse response) {
        return processRequest(userDto, result, model, null, response);
    }

    @PostMapping("/{id}")
    public String update(@Valid @ModelAttribute("userDto") UserDto userDto,
                         BindingResult result, Model model,
                         @PathVariable Long id,
                         HttpServletResponse response) {
        return processRequest(userDto, result, model, id, response);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        sseEmitterService.sendEvent(sseEmitters,
                sseEmitterService.buildData("layouts/users",
                        userService.findAll(), "users"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sseUsers")
    public SseEmitter sse(@AuthenticationPrincipal UserDetails userDetails) {
        return sseEmitterService.createSseEmitter(sseEmitters, userDetails.getUsername());
    }

    private String processRequest(UserDto userDto, BindingResult result, Model model, Long id,
                                  HttpServletResponse response) {
        if (result.hasErrors()) {
            model.addAttribute("userDto", userDto);
        } else {
            if (id == null) {
                userService.save(userDto);
            } else {
                userService.update(id, userDto);
            }
            response.addHeader("Hx-Trigger", "closeModal");
            sseEmitterService.sendEvent(sseEmitters,
                    sseEmitterService.buildData("layouts/users",
                            userService.findAll(), "users"));
        }
        return "layouts/forms/add-new-user";
    }
}
