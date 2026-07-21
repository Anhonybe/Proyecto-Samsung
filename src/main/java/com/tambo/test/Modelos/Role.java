package com.tambo.test.Modelos;

public enum Role {
    SUPERVISOR("Supervisor"),
    TECNICO("Tecnico"),
    MANTENIMIENTO("Mantenimiento");
    
    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}