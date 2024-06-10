(ns abstract.utils
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.string :as string]
            [clojure.data.json :as cjson]))

(defn como-json [conteudo & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/generate-string conteudo)})

(defn registrarValor []
  (println "Valor da transação: ")
  (int (read)))

(defn registrarTipo []
  (println "Tipo: 1 (Receita) ou 2 (Despesa) ")
  (let [input (int (read))]
    (cond
      (== input 1) "receita"
      (== input 2) "despesa"
      :else (registrarTipo))))

(defn registrarRotulo []
  (println "Registre um rótulo para a transação.")
  (str (read)))

(defn limparTela []
  (print (str (char 27) "[H" (char 27) "[2J"))
  (flush))


(defn tratarPedido [pedido]
  (-> (cjson/write-str pedido :escape-slash false)
      (string/replace #"INDEX:" "Bloco: ")
      (string/replace #"NONCE:" "Nonce: ")
      (string/replace #"DATA:" "Date: ")
      (string/replace #"HASHPREVIOUS:" "Hash Prévio: ")
      (string/replace #"HASHCURRENT:" "Hash: ")
      (string/replace #"[{}\[\]\"]" "")
      (string/replace #"\\" "")
      (string/replace #"," "\n")
      (string/replace #"transacoes:" "")
      (string/replace #"valor:" "Valor: ")
      (string/replace #"tipo:" "Tipo: ")
      (string/replace #"rotulo:" "Rótulo: ") 
      ) 
  )


(defn registrarTransacao [] 
  (http/post "http://localhost:3000/transacoes"
             {:headers {"Content-Type" "application/json"}
              :body (json/generate-string
                     {:valor (registrarValor) :tipo (registrarTipo) :rotulo (registrarRotulo)})}))

(defn obterTransacoes []
  (doseq [transacoes (list (:body (http/get "http://localhost:3000/transacoes")))]
    (println (tratarPedido transacoes)) 
    (println)))

(defn obterSaldo [] (println (:body {http/get "http://localhost:3000/saldo"})))

(defn registrarBlockchain []
  (do (http/post "http://localhost:3000/blockchain"
                 {:headers {"Content-Type" "application/json"}
                  :body (json/generate-string (:body (http/get "http://localhost:3000/transacoes")))})
      (println "Blockchain registrada")))

(defn obterBlockchain []
  (doseq [blocos (list (:body (http/get "http://localhost:3000/blockchain")))]
    (println (tratarPedido blocos))
    (println blocos)
    (println)))

(defn entradas [] 
  (println "#==========# GERENCIADOR FINANCEIRO E BLOCKCHAIN #==========#")
  (println) 
  (println "Opções disponíveis:") 
  (println)
  (println "1 - Visualizar Transações.")
  (println "2 - Adicionar nova transação.")
  (println "3 - Visualizar Blockchain.")
  (println "4 - Registrar transações na blockchain.")
  (println "5 - Limpar tela.") 
  (println)
  (println "Aguardando próximo comando...")
  (println "_____________________________________________________________")

  (def comando (read))
  (cond
    (= (int comando) 1) (obterTransacoes)
    (= (int comando) 2) (registrarTransacao)
    (= (int comando) 3) (obterBlockchain)
    (= (int comando) 4) (registrarBlockchain)
    (= (int comando) 5) (limparTela))
  (entradas))