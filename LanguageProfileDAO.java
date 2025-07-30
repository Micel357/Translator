package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object para perfis de idiomas.
 * Armazena frequências de caracteres para detecção automática de idiomas.
 * Complexidade das operações:
 * - insertProfile: O(1) - inserção direta
 * - getProfile: O(1) - busca por chave primária
 * - getAllProfiles: O(n) - onde n é o número de idiomas cadastrados
 */
public class LanguageProfileDAO {
    
    /**
     * Insere ou atualiza um perfil de idioma.
     * Complexidade: O(1) - operação de inserção/atualização direta
     */
    public void insertProfile(String langCode, Map<Character, Double> charFrequencies) {
        String sql = "INSERT OR REPLACE INTO language_profiles(lang_code, char_frequencies) VALUES(?,?)";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, langCode);
            pstmt.setString(2, serializeFrequencies(charFrequencies));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Recupera um perfil de idioma específico.
     * Complexidade: O(1) - busca por chave primária
     */
    public Map<Character, Double> getProfile(String langCode) {
        String sql = "SELECT char_frequencies FROM language_profiles WHERE lang_code = ?";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, langCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return deserializeFrequencies(rs.getString("char_frequencies"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new HashMap<>();
    }
    
    /**
     * Recupera todos os perfis de idiomas.
     * Complexidade: O(n) - onde n é o número de idiomas cadastrados
     */
    public Map<String, Map<Character, Double>> getAllProfiles() {
        String sql = "SELECT * FROM language_profiles";
        Map<String, Map<Character, Double>> profiles = new HashMap<>();
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String langCode = rs.getString("lang_code");
                Map<Character, Double> frequencies = deserializeFrequencies(rs.getString("char_frequencies"));
                profiles.put(langCode, frequencies);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return profiles;
    }
    
    /**
     * Serializa o mapa de frequências para string.
     * Complexidade: O(k) - onde k é o número de caracteres únicos
     */
    private String serializeFrequencies(Map<Character, Double> frequencies) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Double> entry : frequencies.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }
    
    /**
     * Deserializa a string para mapa de frequências.
     * Complexidade: O(k) - onde k é o número de caracteres únicos
     */
    private Map<Character, Double> deserializeFrequencies(String serialized) {
        Map<Character, Double> frequencies = new HashMap<>();
        if (serialized != null && !serialized.isEmpty()) {
            String[] pairs = serialized.split(";");
            for (String pair : pairs) {
                if (!pair.isEmpty()) {
                    String[] parts = pair.split(":");
                    if (parts.length == 2) {
                        char character = parts[0].charAt(0);
                        double frequency = Double.parseDouble(parts[1]);
                        frequencies.put(character, frequency);
                    }
                }
            }
        }
        return frequencies;
    }
}

