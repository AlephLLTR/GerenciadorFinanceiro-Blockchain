(ns financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [financeiro.handler :refer :all]
            [financeiro.db :as db]))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(facts "Dá um 'Olá, mundo!' na rota raíz."
        (fact "Estado da resposta é 200"
              (let [response (app (mock/request :get "/"))]
                (:status response) => 200))

        (fact "O texto do corpo é 'Olá, mundo!'"
              (let [response (app (mock/request :get "/"))]
                (:body response) => "Olá, mundo!")))
#_{:clj-kondo/ignore [:unresolved-symbol]}
 (facts "Rota inválida inexistente."
        (fact "O código de erro é 404"
              (let [response (app (mock/request :get "/invalid"))]
                (:status response) => 404))

        (fact "O texto de corpo é 'Recurso não encontrado.")
        (let [response (app (mock/request :get "/invalid"))]
          (:body response) => "Recurso não encontrado."))

(facts "O Saldo inicial é 0."
       #_{:clj-kondo/ignore [:unresolved-symbol]}
       (against-background [(json/generate-string {:saldo 0})
                            => "{\"saldo\":0}"
                            (db/saldo) => 0])

       (let [response (app (mock/request :get "/saldo"))]
         #_{:clj-kondo/ignore [:unresolved-symbol]}
         (fact "O formato é 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         #_{:clj-kondo/ignore [:unresolved-symbol]}
         (fact "Estado de resposta é 200."
               (:status response) => 200)

         #_{:clj-kondo/ignore [:unresolved-symbol]}
         (fact "O texto do corpo é um JSON cuja chave é saldo e o valor é 0." 
               (:body response) => "{\"saldo\":0}")))


(facts "Registra uma receita no valor de 10."
       #_{:clj-kondo/ignore [:unresolved-symbol]}
       (against-background (db/registrar {:valor 10
                                          :tipo "receita"})
                           => {:id 1 :valor 10 :tipo "receita"})
       (let [response
             (app (-> (mock/request :post "/transacoes")
                      (mock/json-body {:valor 10 :tipo "receita"})))]

         #_{:clj-kondo/ignore [:unresolved-symbol]}
         (fact "O Estado da resposta é 201"
               (:status response) => 201)

         #_{:clj-kondo/ignore [:unresolved-symbol]}
         (fact "O texto do corpo é um JSON com o conteúdo enviado e um ID."
               (:body response) =>
               "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")))

