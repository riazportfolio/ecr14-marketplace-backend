import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        System.out.println("super123 hash: " + encoder.encode("super123"));
        System.out.println("admin123 hash: " + encoder.encode("admin123"));
        
        // Test the existing hashes
        String superHash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIeWHbXu5u";
        String adminHash = "$2a$12$7x0hXkDrWb.zKj8cYJVPf.MRzqVhJh5cQVMzLQHyQ8SXh4Q5hCy3K";
        
        System.out.println("\nVerifying super123 against stored hash: " + encoder.matches("super123", superHash));
        System.out.println("Verifying admin123 against stored hash: " + encoder.matches("admin123", adminHash));
    }
}
