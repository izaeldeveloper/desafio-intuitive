# Sobre o Teste

Então, eu utilizei Python e VueJs como pediu, mas para evitar excesso de diretórios em um projeto simples, eu optei por usar o CDN do Vue.

1. Criei um CSS simples com o style.css e importei no HTML.
2. Utilizei o CSV requisitado, mudando o nome para "operadoras_cadastradas" para ficar mais legível.
3. A pesquisa retorna um GET em formato de tabela pelo buscador, escolhi a coluna de Razão Social, mas é escalável para pesquisar a partir de qualquer coluna.
4. Utilizei as bibliotecas pandas, para fazer leitura do csv, e o Flask para trabalhar com requisições na web.