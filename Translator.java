package translation;

import db.TranslationDAO;
import language.LanguageDetector;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementa o motor de tradução, utilizando cache e detecção de idioma.
 * As complexidades algorítmicas são analisadas para cada método.
 */
public class Translator {

    private TranslationDAO translationDAO;
    private LanguageDetector languageDetector;
    private Map<String, String> translationCache; // Cache em memória para traduções recentes

    public Translator() {
        this.translationDAO = new TranslationDAO();
        this.languageDetector = new LanguageDetector();
        this.translationCache = new HashMap<>();
    }

    /**
     * Traduz um texto da língua de origem para a língua alvo.
     * Primeiro verifica o cache em memória, depois o banco de dados.
     * Se não encontrar, simula uma tradução e armazena no cache e no banco.
     * Complexidade: O(1) para busca em cache/banco (assumindo índice).
     * Se a tradução não estiver presente, a complexidade da simulação é O(M) onde M é o tamanho do texto.
     */
    public String translate(String text, String sourceLang, String targetLang) {
        // 1. Verificar cache em memória
        String cacheKey = text + "_" + sourceLang + "_" + targetLang;
        if (translationCache.containsKey(cacheKey)) {
            System.out.println("Traduzido do cache em memória.");
            return translationCache.get(cacheKey);
        }

        // 2. Verificar banco de dados
        String translatedText = translationDAO.findTranslation(text, sourceLang, targetLang);
        if (translatedText != null) {
            translationCache.put(cacheKey, translatedText); // Adicionar ao cache em memória
            System.out.println("Traduzido do banco de dados.");
            return translatedText;
        }

        // 3. Simular tradução (em um cenário real, aqui haveria uma chamada a uma API de tradução)
        translatedText = simulateTranslation(text, sourceLang, targetLang);
        System.out.println("Tradução simulada.");

        // 4. Armazenar no banco de dados e no cache em memória
        translationDAO.insert(text, sourceLang, translatedText, targetLang);
        translationCache.put(cacheKey, translatedText);

        return translatedText;
    }

    /**
     * Simula uma tradução simples. Em um cenário real, esta seria uma integração com uma API de tradução.
     * Complexidade: O(M) onde M é o comprimento do texto, devido à manipulação de strings.
     */
    private String simulateTranslation(String text, String sourceLang, String targetLang) {
        // Lógica de simulação de tradução muito básica
        // Em um projeto real, aqui seria feita uma chamada a uma API de tradução (e.g., Google Translate API)
        if (sourceLang.equals("en") && targetLang.equals("pt")) {
            if (text.equalsIgnoreCase("hello")) return "olá";
            if (text.equalsIgnoreCase("world")) return "mundo";
            if (text.equalsIgnoreCase("dog")) return "cachorro";
            if (text.equalsIgnoreCase("cat")) return "gato";
            if (text.equalsIgnoreCase("house")) return "casa";
            return "[Traduzido para PT: " + text + "]";
        } else if (sourceLang.equals("pt") && targetLang.equals("en")) {
            if (text.equalsIgnoreCase("olá")) return "hello";
            if (text.equalsIgnoreCase("mundo")) return "world";
            if (text.equalsIgnoreCase("cachorro")) return "dog";
            if (text.equalsIgnoreCase("gato")) return "cat";
            if (text.equalsIgnoreCase("casa")) return "house";
            return "[Translated to EN: " + text + "]";
        } else if (sourceLang.equals("es") && targetLang.equals("en")) {
            if (text.equalsIgnoreCase("hola")) return "hello";
            return "[Translated to EN: " + text + "]";
        } else if (sourceLang.equals("fr") && targetLang.equals("en")) {
            if (text.equalsIgnoreCase("bonjour")) return "hello";
            return "[Translated to EN: " + text + "]";
        }
        return "[Sem tradução para " + sourceLang + "-" + targetLang + ": " + text + "]";
    }

    /**
     * Detecta o idioma de um texto usando o LanguageDetector.
     * Complexidade: O(L + N*K) conforme definido em LanguageDetector.
     */
    public String detectLanguage(String text) {
        return languageDetector.detectLanguage(text);
    }
}


