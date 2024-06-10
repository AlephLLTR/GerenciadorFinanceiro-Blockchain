(ns financeiro.filtros-aceitacao-test
  (:require [midje.sweet :refer :all]
            [cheshire.core :as json]
            [financeiro.auxiliares :refer :all]
            [clj-http.client :as http]
            [financeiro.db :as db]))

(against-background [(before :facts
                             [(iniciar-servidor porta-padrao)
                              (db/limpar)])
                     (after :facts (parar-servidor))])

(fact "Não existem receitas" :aceitacao
      (json/parse-string (conteudo "/receitas") true)
      => {:transacoes '()})

(fact "Não existem despesas" :aceitacao
      (json/parse-string (conteudo "/despesas") true)
      => {:transacoes '()})

(fact "Não existem transacoes" :aceitacao
      (json/parse-string (conteudo "/transacoes") true)
      => {:transacoes '()})


