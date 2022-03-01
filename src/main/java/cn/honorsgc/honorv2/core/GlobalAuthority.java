package cn.honorsgc.honorv2.core;

import org.springframework.security.core.GrantedAuthority;

public enum GlobalAuthority implements GrantedAuthority {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    SUPER_ADMIN("ROLE_SUPER");
    private final String role;

    GlobalAuthority(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

    @Override
    public String toString() {
        return this.role;
    }
}
