package az.company.bestpractices.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("İstifadəçi daxil olmayıb");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }

        throw new IllegalStateException("İstifadəçi məlumatı müəyyən edilə bilmədi");
    }

    // Spesifik rol yoxlanışı (Məsələn: hasRole("ROLE_ADMIN"))
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        // Spring Security-də rollar adətən "ROLE_" prefiksi ilə saxlanılır
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleWithPrefix));
    }

    // İmtiyazlı rolları yoxlamaq üçün (Admin və ya Super Admin)
    public static boolean isPrivileged() {
        return hasRole("ADMIN") || hasRole("SUPER_ADMIN");
    }
}