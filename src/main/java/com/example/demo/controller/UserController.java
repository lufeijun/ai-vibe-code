package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.annotation.RequirePermission;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.PermissionTreeDTO;
import com.example.demo.dto.UserQueryRequest;
import com.example.demo.dto.UserWithRolesDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.PermissionService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RoleService roleService;
    private final PermissionService permissionService;

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            user.setPassword(null);
            return ApiResponse.success("注册成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {

            System.out.println(new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword()));
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.login(request);
            String jwt = tokenProvider.generateTokenFromUsername(user.getUsername(), user.getId());
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone()
            );

            List<Role> roles = roleService.getRolesByUserId(user.getId());
            List<String> roleCodes = roles.stream().map(Role::getCode).collect(Collectors.toList());
            List<String> permissionCodes = permissionService.getPermissionCodesByUserId(user.getId());

            LoginResponse loginResponse = new LoginResponse(jwt, "Bearer", 7200L, userInfo, roleCodes, permissionCodes);
            return ApiResponse.success("登录成功", loginResponse);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/list")
    // @RequirePermission("user:center:userlist")
    public ApiResponse<IPage<UserWithRolesDTO>> list(@RequestBody UserQueryRequest request) {
        try {
            IPage<UserWithRolesDTO> page = userService.getUserList(request);
            return ApiResponse.success(page);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/permission")
    public ApiResponse<List<PermissionTreeDTO>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ApiResponse.error("无效的授权头");
            }

            String token = authHeader.substring(7);
            if (!tokenProvider.validateToken(token)) {
                return ApiResponse.error("无效的令牌");
            }

            Long userId = tokenProvider.getUserIdFromToken(token);
            List<PermissionTreeDTO> permissionTree = permissionService.getPermissionTreeByUserId(userId);

            return ApiResponse.success(permissionTree);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
