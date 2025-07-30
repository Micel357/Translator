package gui;

import language.LanguageDetector;
import translation.Translator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TranslatorGUI extends JFrame {

    private JTextArea sourceTextArea;
    private JTextArea targetTextArea;
    private JButton translateButton;
    private JLabel detectedLanguageLabel;

    private Translator translator;
    private LanguageDetector languageDetector;

    public TranslatorGUI() {
        super("Tradutor Java");

        translator = new Translator();
        languageDetector = new LanguageDetector();

        // Configurações da janela
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // Layout principal
        setLayout(new BorderLayout(10, 10));

        // Painel superior para entrada de texto
        JPanel topPanel = new JPanel(new BorderLayout());
        sourceTextArea = new JTextArea(10, 40);
        sourceTextArea.setLineWrap(true);
        sourceTextArea.setWrapStyleWord(true);
        JScrollPane sourceScrollPane = new JScrollPane(sourceTextArea);
        topPanel.add(new JLabel("Texto Original:"), BorderLayout.NORTH);
        topPanel.add(sourceScrollPane, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Painel central para botões e informações
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        translateButton = new JButton("Traduzir");
        detectedLanguageLabel = new JLabel("Idioma Detectado: --");
        centerPanel.add(translateButton);
        centerPanel.add(detectedLanguageLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Painel inferior para saída de texto
        JPanel bottomPanel = new JPanel(new BorderLayout());
        targetTextArea = new JTextArea(10, 40);
        targetTextArea.setLineWrap(true);
        targetTextArea.setWrapStyleWord(true);
        targetTextArea.setEditable(false); // A área de texto de destino não é editável
        JScrollPane targetScrollPane = new JScrollPane(targetTextArea);
        bottomPanel.add(new JLabel("Texto Traduzido:"), BorderLayout.NORTH);
        bottomPanel.add(targetScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Ação do botão Traduzir
        translateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourceText = sourceTextArea.getText();
                if (sourceText.isEmpty()) {
                    detectedLanguageLabel.setText("Idioma Detectado: --");
                    targetTextArea.setText("");
                    return;
                }

                String detectedLang = languageDetector.detectLanguage(sourceText);
                detectedLanguageLabel.setText("Idioma Detectado: " + detectedLang.toUpperCase());

                // Por simplicidade, vamos traduzir para o inglês se o idioma detectado não for inglês,
                // e para o português se o idioma detectado for inglês.
                String targetLang = "en";
                if (detectedLang.equals("en")) {
                    targetLang = "pt";
                } else if (detectedLang.equals("pt")) {
                    targetLang = "en";
                } else if (detectedLang.equals("es")) {
                    targetLang = "en";
                } else if (detectedLang.equals("fr")) {
                    targetLang = "en";
                }

                String translatedText = translator.translate(sourceText, detectedLang, targetLang);
                targetTextArea.setText(translatedText);
            }
        });
    }

    public static void main(String[] args) {
        // Garante que a GUI seja executada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TranslatorGUI().setVisible(true);
            }
        });
    }
}


