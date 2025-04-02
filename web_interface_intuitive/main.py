from flask import Flask, request, jsonify
import pandas as pd
from flask_cors import CORS

"""
    Aqui é onde vai ser iniciado a aplicação, usando o CORS
    para disponibilizar recursos a qualquer origem, já que pode ter
    erros decorrentes de portas e IPs. (Foi feito para testes)
"""

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})

arquivo_csv = "operadoras_cadastradas.csv"
# Esse é o arquivo csv que vai ser lido com as requisições

df = pd.read_csv(arquivo_csv, sep=';', dtype=str)
# Aqui é usado o método read_csv do pandas para ler o csv

df = df.fillna('')
# Aqui é pra retornar nada quando o dado não tiver nada e evitar erros


"""
    Aqui é um método que vai buscar as operadoras, levando a razao como
    parametro, retornando um json como resposta. Ordenadas por relevância
"""


def buscar_operadoras(razao):
    df['relevancia'] = df.apply(lambda row: sum(
        razao.lower() in str(val).lower() for val in row), axis=1)
    resultados = df[df['relevancia'] > 0]
    # Esse conjunto aqui transforma tudo do csv em minusculo, se for encontrado
    # uma string igual ou semelhante a razao pesquisada, ele soma mais um para
    # a relevancia dela, por isso que os resultados recebem uma relevancia
    # maior que 0, pois devem retornar coisas relevantes ao usuário

    resultados = resultados.where(pd.notnull(resultados), None)
    # Aqui verifica se o resultado é nulo, se for nulo o json da erro, por
    # conta dos NaN, então coloquei um "None" para não dar erro

    resultados = resultados.sort_values(by='Razao_Social', ascending=True)
    return resultados.drop(columns='relevancia').to_dict(orient='records')
    # Aqui é feita a organização dos resultados por relevância.


"""
    Aqui é criada uma rota com uma requisição GET para retornar os dados
    referentes a busca que o usuário realizou, como pode ver, o termo usado
    é a "razão", como no index.html
"""


@app.route("/buscar", methods=["GET"])
def buscar():
    termo = request.args.get("razao", "")
    if not termo:
        return jsonify({"erro": "O parâmetro 'razao' é obrigatório"}), 400
    resultados = buscar_operadoras(termo)

    if not resultados:
        return jsonify({"erro": "Nenhuma operadora encontrada."}), 404
    return jsonify(resultados)
    # Aqui transforma o resultados em um json


"""
    Aqui é criada uma rota para as operadoras que vai retornar tudo já no
    carregamento da página fazendo uma requisição GET
"""


@app.route("/operadoras", methods=['GET'])
def operadoras():

    return jsonify(df.to_dict(orient='records'))


"""
    Aqui é aonde será rodado o servidor Python (Use "python main.py"
    no terminal para rodá-lo, se possível no ambiente venv)
"""


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
