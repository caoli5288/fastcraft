package co.kepler.fastcraftplus;

/**
 * Permissions used by FastCraft+
 */
public enum Permission {
    USE("fastcraft.use"),
    TOGGLE("fastcraft.toggle"),
    TOGGLE_OTHER("fastcraft.toggle.other"),
    CRAFT("fastcraft.craft"),
    ADMIN_RELOAD("fastcraft.admin.reload");

    public final String permission;
    Permission(String permission) {
        this.permission = permission;
    }

    public String toString() {
        return permission;
    }
}
