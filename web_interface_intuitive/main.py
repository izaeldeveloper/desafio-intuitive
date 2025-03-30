from flask import Flask, request, jsonify
import pandas as pd
from flask_cors import CORS

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})

arquivo_csv = "operadoras_cadastradas.csv"

df = pd.read_csv(arquivo_csv, sep=';', dtype=str)

df = df.fillna('')


def buscar_operadoras(razao):
    df['relevancia'] = df.apply(lambda row: sum(
        razao.lower() in str(val).lower() for val in row), axis=1)
    resultados = df[df['relevancia'] > 0]

    resultados = resultados.where(pd.notnull(resultados), None)

    resultados = resultados.sort_values(by='Razao_Social', ascending=True)
    return resultados.drop(columns='relevancia').to_dict(orient='records')


@app.route("/buscar", methods=["GET"])
def buscar():
    termo = request.args.get("razao", "")
    if not termo:
        return jsonify({"erro": "O parâmetro 'razao' é obrigatório"}), 400
    resultados = buscar_operadoras(termo)

    if not resultados:
        return jsonify({"erro": "Nenhuma operadora encontrada."}), 404
    return jsonify(resultados)


@app.route("/operadoras", methods=['GET'])
def operadoras():

    return jsonify(df.to_dict(orient='records'))


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
