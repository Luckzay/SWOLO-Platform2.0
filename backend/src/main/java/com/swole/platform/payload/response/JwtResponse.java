package com.swole.platform.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    @JsonProperty("userId")
    private Long id;
    private String employeeId;
    private String name;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String employeeId, String name, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.employeeId = employeeId;
        this.name = name;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}