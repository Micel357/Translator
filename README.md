Documentação Técnica do Projeto Tradutor Java
.............................................
Micael Da Silva Vasconcelos
.............................................

-Introdução

Este documento detalha a arquitetura, implementação e análise de complexidade do projeto de um tradutor Java com detecção automática de idiomas e persistência de dados em SQLite. O projeto foi desenvolvido com foco em demonstrar proficiência em algoritmos, estruturas de dados e gerenciamento de banco de dados, características de um aluno de Análise e Desenvolvimento de Sistemas no terceiro período.

-Arquitetura do Projeto

O projeto segue uma arquitetura modular, dividida em pacotes para melhor organização e separação de responsabilidades:

•
db: Contém as classes relacionadas à interação com o banco de dados SQLite.

•
language: Contém a lógica para detecção automática de idiomas.

•
translation: Contém o motor de tradução e o gerenciamento de cache.

•
gui: Contém a interface gráfica do usuário (GUI) desenvolvida com Swing.

- Banco de Dados SQLite

O SQLite foi escolhido como o sistema de gerenciamento de banco de dados (SGBD) devido à sua natureza leve, embarcada e sem servidor, ideal para aplicações que não exigem um servidor de banco de dados dedicado. Ele armazena os dados em um único arquivo no sistema de arquivos.

 -Comandos SQL e Teoria

O projeto utiliza duas tabelas principais:

•
translations: Armazena as traduções realizadas, servindo como um histórico e cache persistente.

•
language_profiles: Armazena perfis de frequência de caracteres para cada idioma, utilizados na detecção automática.

Tabela translations

SQL


CREATE TABLE IF NOT EXISTS translations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_text TEXT NOT NULL,
    source_lang TEXT NOT NULL,
    target_text TEXT NOT NULL,
    target_lang TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);


•
id: Chave primária auto-incrementável para identificação única de cada tradução.

•
source_text: O texto original a ser traduzido.

•
source_lang: O código do idioma original (ex: "en", "pt").

•
target_text: O texto traduzido.

•
target_lang: O código do idioma para o qual o texto foi traduzido.

•
timestamp: Data e hora da tradução, com valor padrão sendo o momento da inserção.

Tabela language_profiles

SQL


CREATE TABLE IF NOT EXISTS language_profiles (
    lang_code TEXT PRIMARY KEY,
    char_frequencies TEXT NOT NULL
);


•
lang_code: Chave primária que representa o código do idioma (ex: "en", "pt").

•
char_frequencies: Uma representação em texto (serializada) do mapa de frequências de caracteres para aquele idioma.

-Operações CRUD e Análise de Complexidade (Big O)

A classe TranslationDAO gerencia as operações CRUD para a tabela translations:

•
insert(sourceText, sourceLang, targetText, targetLang):

•
Descrição: Insere uma nova tradução na tabela translations.

•
Complexidade (Big O): O(1).

•
Justificativa: Operações de inserção em tabelas de banco de dados com chaves primárias auto-incrementáveis são geralmente consideradas de tempo constante, pois envolvem a adição de um novo registro no final da estrutura de dados subjacente (como um heap ou árvore B otimizada para inserções).



•
findTranslation(sourceText, sourceLang, targetLang):

•
Descrição: Busca uma tradução específica com base no texto original, idioma de origem e idioma de destino.

•
Complexidade (Big O): O(log n).

•
Justificativa: Assumindo que o banco de dados possui índices nas colunas source_text, source_lang e target_lang (o que é uma prática recomendada para otimização de consultas), a busca por um registro específico é logarítmica em relação ao número de registros (n) na tabela. O SQLite cria índices automaticamente para chaves primárias e pode ser configurado para índices em outras colunas.



•
getAllTranslations():

•
Descrição: Recupera todas as traduções armazenadas no banco de dados.

•
Complexidade (Big O): O(n).

•
Justificativa: Esta operação envolve a varredura completa da tabela translations para retornar todos os registros. Portanto, a complexidade é linear em relação ao número de registros (n).



A classe LanguageProfileDAO gerencia as operações CRUD para a tabela language_profiles:

•
insertProfile(langCode, charFrequencies):

•
Descrição: Insere ou atualiza um perfil de idioma. Utiliza INSERT OR REPLACE para garantir que um idioma tenha apenas um perfil.

•
Complexidade (Big O): O(1) para a operação de banco de dados, mais O(K) para serialização/deserialização do mapa de frequências, onde K é o número de caracteres únicos no perfil.

•
Justificativa: A operação de INSERT OR REPLACE é eficiente, e a serialização/deserialização é linear em relação ao número de entradas no mapa de frequências.



•
getProfile(langCode):

•
Descrição: Recupera o perfil de frequência de caracteres para um idioma específico.

•
Complexidade (Big O): O(1) para a busca no banco de dados (devido à chave primária lang_code), mais O(K) para deserialização.

•
Justificativa: A busca por chave primária é de tempo constante. A deserialização é linear em relação ao número de caracteres únicos no perfil.



•
getAllProfiles():

•
Descrição: Recupera todos os perfis de idioma armazenados.

•
Complexidade (Big O): O(N*K), onde N é o número de idiomas e K é o número médio de caracteres únicos por idioma.

•
Justificativa: Envolve a varredura de todos os perfis e a deserialização de cada um, resultando em uma complexidade linear em relação ao número total de caracteres em todos os perfis.



- Algoritmo de Detecção de Idiomas

A detecção de idiomas é implementada na classe LanguageDetector e baseia-se na análise de frequência de caracteres. A ideia é que cada idioma possui um padrão estatístico distinto na ocorrência de seus caracteres.

- Funcionamento


- Cálculo de Frequência de Caracteres: Para um dado texto, é calculada a frequência relativa de cada caractere (ignorando espaços e pontuações e convertendo para minúsculas). Esta etapa tem complexidade O(L), onde L é o comprimento do texto, pois cada caractere é processado uma vez.

- Perfis de Idioma: Perfis de frequência de caracteres para idiomas conhecidos (inglês, português, espanhol, francês, etc.) são armazenados no banco de dados (language_profiles). Estes perfis são carregados em memória na inicialização do LanguageDetector.

  
- Comparação de Distância: Para detectar o idioma de um texto de entrada, suas frequências de caracteres são comparadas com os perfis de idioma carregados. A métrica utilizada é a distância euclidiana entre os vetores de frequência.

- Análise de Complexidade (Big O)

•
calculateCharacterFrequencies(String text):

•
Complexidade (Big O): O(L).

•
Justificativa: O método itera sobre cada caractere do texto de entrada uma única vez para contar as ocorrências e, em seguida, itera sobre o mapa de contagens (que tem no máximo K entradas, onde K é o número de caracteres únicos possíveis) para calcular as frequências. A operação dominante é a iteração sobre o texto, tornando-o linear em relação ao comprimento do texto (L).



•
detectLanguage(String text):

•
Complexidade (Big O): O(L + N*K).

•
Justificativa: Primeiro, o método chama calculateCharacterFrequencies, que é O(L). Em seguida, ele itera sobre todos os N perfis de idioma carregados. Para cada perfil, ele calcula a distância euclidiana, que é O(K) (onde K é o número de caracteres únicos). Portanto, a complexidade total é a soma da complexidade de cálculo das frequências do texto de entrada e a complexidade de comparação com todos os perfis: O(L + N*K).



•
addOrUpdateLanguageProfile(String langCode, String sampleText):

•
Complexidade (Big O): O(L + K).

•
Justificativa: Envolve o cálculo das frequências do texto de amostra (O(L)) e a inserção/atualização do perfil no banco de dados e no cache em memória (O(K) para serialização/deserialização).


- Sistema de Tradução

O sistema de tradução é implementado na classe Translator, que integra a detecção de idiomas e um mecanismo de cache para otimizar o desempenho.

- Funcionamento


- Cache em Memória: Antes de qualquer operação de tradução, o sistema verifica se a tradução para o par (texto, idioma de origem, idioma de destino) já existe em um cache em memória (HashMap). Isso proporciona acesso O(1) para traduções frequentes.

- Cache Persistente (SQLite): Se a tradução não estiver no cache em memória, o sistema consulta a tabela translations no banco de dados SQLite. A busca no banco de dados, com índices adequados, é O(log n).


- Simulação de Tradução: Caso a tradução não seja encontrada em nenhum dos caches, uma função simulateTranslation é chamada. Em um cenário real, esta função seria substituída por uma chamada a uma API de tradução externa (ex: Google Translate API, DeepL API). A simulação atual tem complexidade O(M), onde M é o comprimento do texto, devido à manipulação de strings.


- Armazenamento: Após uma tradução (seja do banco de dados ou simulada), ela é armazenada tanto no cache em memória quanto na tabela translations do SQLite para futuras consultas, otimizando o desempenho e persistindo os dados.

- Análise de Complexidade (Big O)

•
translate(String text, String sourceLang, String targetLang):

•
Complexidade (Big O): O(1) no melhor caso (cache em memória), O(log n) no caso médio (cache de banco de dados), e O(L + M) no pior caso (simulação de tradução e detecção de idioma).

•
Justificativa: A complexidade varia conforme a disponibilidade da tradução. A busca em HashMap é O(1). A busca no banco de dados é O(log n) com índices. Se a tradução precisar ser simulada, a complexidade é dominada pela detecção de idioma (O(L)) e pela simulação de tradução (O(M)).



•
simulateTranslation(String text, String sourceLang, String targetLang):

•
Complexidade (Big O): O(M).

•
Justificativa: A complexidade é linear em relação ao comprimento do texto (M) devido às comparações de string e concatenações. Em um cenário real com uma API externa, esta complexidade seria dominada pelo tempo de resposta da API (considerado O(1) para fins de análise algorítmica, mas com latência de rede).



•
detectLanguage(String text):

•
Complexidade (Big O): O(L + N*K).

•
Justificativa: Este método chama o LanguageDetector.detectLanguage, cuja complexidade já foi analisada na Seção 4.2.



- Interface de Usuário (GUI)

A interface gráfica do usuário é construída utilizando Java Swing, proporcionando uma interação simples e intuitiva para o usuário.

- Componentes Principais

•
JTextArea para Texto Original: Onde o usuário insere o texto a ser traduzido.

•
JTextArea para Texto Traduzido: Exibe o resultado da tradução (não editável).

•
JButton "Traduzir": Aciona o processo de detecção de idioma e tradução.

•
JLabel para Idioma Detectado: Exibe o idioma que foi automaticamente detectado para o texto original.

- Integração e Fluxo


- O usuário digita o texto no sourceTextArea.

  
- Ao clicar no botão "Traduzir", o texto é passado para o LanguageDetector para identificar o idioma de origem.

  
- O idioma detectado é exibido no detectedLanguageLabel.


- Com o texto original e o idioma detectado, o Translator é invocado para realizar a tradução para um idioma alvo pré-definido (atualmente, traduz para inglês se o idioma detectado não for inglês, e para português se for inglês, espanhol ou francês).

  
- O texto traduzido é exibido no targetTextArea.

- Análise de Complexidade (Big O)

As operações da GUI em si (renderização de componentes, manipulação de eventos) são geralmente consideradas de tempo constante ou linear em relação ao número de componentes, e não dominam a complexidade algorítmica geral do aplicativo. A complexidade principal reside nas chamadas aos módulos de LanguageDetector e Translator, que já foram detalhadas.

- Conclusão

Este projeto demonstra a aplicação de conceitos fundamentais de Análise e Desenvolvimento de Sistemas, incluindo design de banco de dados (SQLite), implementação de operações CRUD, desenvolvimento de algoritmos (detecção de idioma por frequência de caracteres), otimização de desempenho com cache e análise de complexidade algorítmica utilizando a notação Big O. A modularidade do código e a separação de responsabilidades facilitam a manutenção e futuras expansões, como a integração com APIs de tradução reais ou a adição de mais idiomas e funcionalidades.

CONTRIBUIÇÃO

Faça um fork do projeto
Crie uma branch para sua feature (git checkout -b feature/nova-feature)
Commit suas mudanças (git commit -am 'Adiciona nova feature')
Push para a branch (git push origin feature/nova-feature)
Abra um Pull Request

SUPORTE
 
Para suporte ou dúvidas, abra uma issue no repositório do projeto.
