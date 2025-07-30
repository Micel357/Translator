import db.DatabaseManager;
import db.LanguageProfileDAO;
import language.LanguageDetector;
import gui.TranslatorGUI;

import javax.swing.SwingUtilities; // Import adicionado
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Criar o banco de dados e as tabelas
        DatabaseManager.createNewDatabase();
        DatabaseManager.createTables();

        // Adicionar perfis de idioma de exemplo ANTES de inicializar LanguageDetector
        // Estes perfis serão carregados pelo LanguageDetector na sua inicialização
        // Usando textos mais longos para perfis mais precisos
        LanguageProfileDAO profileDAO = new LanguageProfileDAO();
        LanguageDetector tempDetector = new LanguageDetector();

        String enSample = "The quick brown fox jumps over the lazy dog. This is a common pangram used to display all letters of the alphabet. English is a West Germanic language that was first spoken in early medieval England and is now the most widely used language in the world.";
        String ptSample = "A rápida raposa marrom salta sobre o cão preguiçoso. Este é um pangrama comum usado para exibir todas as letras do alfabeto. O português é uma língua românica originária da Galiza e do norte de Portugal, e é a língua oficial de Portugal, Brasil, Angola, Moçambique, Cabo Verde, Guiné-Bissau, São Tomé e Príncipe e Timor-Leste.";
        String esSample = "El rápido zorro marrón salta sobre el perro perezoso. Este es un pangrama común utilizado para mostrar todas as letras do alfabeto. El español es una lengua romance, derivada del latín vulgar, que se habla principalmente en España y América Latina.";
        String frSample = "Le rapide renard brun saute par-dessus le chien paresseux. Ceci é um pangramme courant utilizado para exibir todas as letras do alfabeto. Le français est une langue romane parlée principalmente en France, au Canada, en Belgique, en Suisse e dans de nombreux pays africains.";

        Map<Character, Double> enFreq = tempDetector.calculateCharacterFrequencies(enSample);
        profileDAO.insertProfile("en", enFreq);

        Map<Character, Double> ptFreq = tempDetector.calculateCharacterFrequencies(ptSample);
        profileDAO.insertProfile("pt", ptFreq);

        Map<Character, Double> esFreq = tempDetector.calculateCharacterFrequencies(esSample);
        profileDAO.insertProfile("es", esFreq);

        Map<Character, Double> frFreq = tempDetector.calculateCharacterFrequencies(frSample);
        profileDAO.insertProfile("fr", frFreq);

        // Iniciar a GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TranslatorGUI().setVisible(true);
            }
        });
    }
}


