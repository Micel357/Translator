package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para operações de tradução no banco de dados.
 * Complexidade das operações:
 * - insert: O(1) - inserção direta
 * - findTranslation: O(log n) - busca com índice
 * - getAllTranslations: O(n) - varredura completa da tabela
 */
public class TranslationDAO {
    
    /**
     * Insere uma nova tradução no banco de dados.
     * Complexidade: O(1) - operação de inserção direta
     */
    public void insert(String sourceText, String sourceLang, String targetText, String targetLang) {
        String sql = "INSERT INTO translations(source_text, source_lang, target_text, target_lang) VALUES(?,?,?,?)";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sourceText);
            pstmt.setString(2, sourceLang);
            pstmt.setString(3, targetText);
            pstmt.setString(4, targetLang);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Busca uma tradução específica no cache.
     * Complexidade: O(log n) - assumindo índice na coluna source_text
     */
    public String findTranslation(String sourceText, String sourceLang, String targetLang) {
        String sql = "SELECT target_text FROM translations WHERE source_text = ? AND source_lang = ? AND target_lang = ?";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sourceText);
            pstmt.setString(2, sourceLang);
            pstmt.setString(3, targetLang);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("target_text");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    /**
     * Retorna todas as traduções armazenadas.
     * Complexidade: O(n) - varredura completa da tabela
     */
    public List<Translation> getAllTranslations() {
        String sql = "SELECT * FROM translations ORDER BY timestamp DESC";
        List<Translation> translations = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Translation translation = new Translation(
                    rs.getInt("id"),
                    rs.getString("source_text"),
                    rs.getString("source_lang"),
                    rs.getString("target_text"),
                    rs.getString("target_lang"),
                    rs.getString("timestamp")
                );
                translations.add(translation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return translations;
    }
    
    /**
     * Classe interna para representar uma tradução
     */
    public static class Translation {
        private int id;
        private String sourceText;
        private String sourceLang;
        private String targetText;
        private String targetLang;
        private String timestamp;
        
        public Translation(int id, String sourceText, String sourceLang, 
                          String targetText, String targetLang, String timestamp) {
            this.id = id;
            this.sourceText = sourceText;
            this.sourceLang = sourceLang;
            this.targetText = targetText;
            this.targetLang = targetLang;
            this.timestamp = timestamp;
        }
        
        // Getters
        public int getId() { return id; }
        public String getSourceText() { return sourceText; }
        public String getSourceLang() { return sourceLang; }
        public String getTargetText() { return targetText; }
        public String getTargetLang() { return targetLang; }
        public String getTimestamp() { return timestamp; }
    }
}

