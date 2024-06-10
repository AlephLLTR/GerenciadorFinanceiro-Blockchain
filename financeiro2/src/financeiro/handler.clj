(ns financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [financeiro.db :as db]
            [financeiro.transacoes :as transacoes]
            [blockchain.blockchain :as blockchain] 
            [abstract.utils :as utils]
            ))


(defroutes app-routes
  (GET "/" [] (utils/entradas))
  (GET "/saldo" [] (utils/como-json {:saldo (db/saldo)}))
  ;; --------------Gerenciador Financeiro------------------
  (POST "/transacoes" requisicao
    (if (transacoes/valida? (:body requisicao))
      (-> (db/registrar (:body requisicao))
          (utils/como-json 201))
      (utils/como-json {:mensagem "Requisição inválida"} 422)))
  (GET "/transacoes" {filtros :params}
    (utils/como-json {:transacoes (if (empty? filtros) (db/transacoes)
                                (db/transacoes-com-filtro filtros))}))
  (GET "/receitas" [] (utils/como-json {:transacoes (db/transacoes-do-tipo "receita")}))
  (GET "/despesas" [] (utils/como-json {:transacoes (db/transacoes-do-tipo "despesa")}))
;; --------------------------------------------------------
  (POST "/blockchain" requisicao (-> (blockchain/adicionar (:body requisicao)) (utils/como-json 201)))
  (GET "/blockchain" [] (utils/como-json (blockchain/blocos)))


  (route/not-found "Recurso não encontrado"))


(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))



;; Adicionar novo bloco
;; curl -X POST http://localhost:3000/blockchain -H "Content-Type: application/json" -d '{"Your transaction data here"}'

;; Adicionar nova transação
;; curl -X POST -d '{"valor": 700, "tipo": "despesa"}' -H "Content-Type: application/json" localhost:3000/transacoes