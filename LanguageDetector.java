package language;

import db.LanguageProfileDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementa um algoritmo de detecção de idioma baseado em análise de frequência de caracteres.
 * A complexidade algorítmica é analisada para cada método.
 */
public class LanguageDetector {

    private LanguageProfileDAO profileDAO;
    private Map<String, Map<Character, Double>> loadedProfiles;

    public LanguageDetector() {
        this.profileDAO = new LanguageProfileDAO();
        // Carrega todos os perfis de idioma do banco de dados na inicialização.
        // Complexidade: O(N*K) onde N é o número de idiomas e K é o número médio de caracteres únicos por idioma.
        // Isso ocorre uma vez na inicialização, tornando as detecções subsequentes mais rápidas.
        this.loadedProfiles = profileDAO.getAllProfiles();
    }

    /**
     * Calcula a frequência de caracteres em um dado texto.
     * Ignora espaços e pontuações, convertendo o texto para minúsculas.
     * Complexidade: O(L) onde L é o comprimento do texto de entrada.
     * Cada caractere é processado uma vez.
     */
    public Map<Character, Double> calculateCharacterFrequencies(String text) {
        Map<Character, Integer> charCounts = new HashMap<>();
        int totalChars = 0;

        // Modificado para incluir uma gama mais ampla de caracteres Unicode para idiomas como Português, Espanhol, Francês
        // e para remover apenas caracteres que não são letras ou números (mantendo acentos e caracteres especiais de idiomas)
        String cleanText = text.toLowerCase().replaceAll("[^\\p{L}\\p{N}]", ""); // \\p{L} para qualquer letra, \\p{N} para qualquer número

        for (char c : cleanText.toCharArray()) {
            charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
            totalChars++;
        }

        Map<Character, Double> frequencies = new HashMap<>();
        if (totalChars > 0) {
            for (Map.Entry<Character, Integer> entry : charCounts.entrySet()) {
                frequencies.put(entry.getKey(), (double) entry.getValue() / totalChars);
            }
        }
        return frequencies;
    }

    /**
     * Adiciona ou atualiza um perfil de idioma no banco de dados e na memória.
     * Complexidade: O(K) para serialização/deserialização e O(1) para inserção no banco.
     */
    public void addOrUpdateLanguageProfile(String langCode, String sampleText) {
        Map<Character, Double> frequencies = calculateCharacterFrequencies(sampleText);
        profileDAO.insertProfile(langCode, frequencies);
        loadedProfiles.put(langCode, frequencies); // Atualiza o cache em memória
    }

    /**
     * Detecta o idioma de um texto comparando suas frequências de caracteres com perfis conhecidos.
     * Utiliza a distância euclidiana para medir a similaridade.
     * Complexidade: O(L + N*K) onde L é o comprimento do texto de entrada, N é o número de perfis de idioma carregados,
     * e K é o número médio de caracteres únicos nos perfis (para a comparação de distância).
     * A parte O(L) é para calcular as frequências do texto de entrada.
     * A parte O(N*K) é para iterar sobre os perfis e calcular a distância.
     */
    public String detectLanguage(String text) {
        Map<Character, Double> textFrequencies = calculateCharacterFrequencies(text);
        String detectedLang = "unknown";
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, Map<Character, Double>> entry : loadedProfiles.entrySet()) {
            String langCode = entry.getKey();
            Map<Character, Double> profileFrequencies = entry.getValue();

            double distance = calculateEuclideanDistance(textFrequencies, profileFrequencies);

            if (distance < minDistance) {
                minDistance = distance;
                detectedLang = langCode;
            }
        }
        return detectedLang;
    }

    /**
     * Calcula a distância euclidiana entre dois mapas de frequência de caracteres.
     * Complexidade: O(K) onde K é o número de caracteres únicos (o maior entre os dois mapas).
     * Cada caractere presente em pelo menos um dos mapas é processado uma vez.
     */
    private double calculateEuclideanDistance(Map<Character, Double> freq1, Map<Character, Double> freq2) {
        Set<Character> allChars = freq1.keySet().stream()
                                    .collect(Collectors.toSet());
        allChars.addAll(freq2.keySet());

        double sumOfSquares = 0;
        for (char c : allChars) {
            double f1 = freq1.getOrDefault(c, 0.0);
            double f2 = freq2.getOrDefault(c, 0.0);
            sumOfSquares += Math.pow(f1 - f2, 2);
        }
        return Math.sqrt(sumOfSquares);
    }

    /**
     * Retorna os perfis de idioma carregados.
     * Complexidade: O(1) - retorna uma referência ao mapa já carregado.
     */
    public Map<String, Map<Character, Double>> getLoadedProfiles() {
        return loadedProfiles;
    }
}


