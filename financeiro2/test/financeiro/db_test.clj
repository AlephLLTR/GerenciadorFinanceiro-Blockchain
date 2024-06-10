(ns db-test
  (:require [midje.sweet :refer :all]
            [financeiro.db :refer :all]))

(facts "Guarda uma transação num átomo."
       (against-background  [(before :facts (limpar))]


                            (fact "A coleção de transações inicia vazia."
                                  (count (transacoes)) => 0)

                            (fact "A transação é o primeiro registro."
                                  (registrar {:valor 7 :tipo "receita"})
                                  => {:id 1 :valor 7 :tipo "receita"}
                                  (count (transacoes)) => 1)))


(facts "Calcula o saldo dada uma coleção de transações."
       (against-background [(before :facts (limpar))]

                           (fact "Saldo é positivo quando só há receita."
                                 (registrar {:valor 1 :tipo "receita"})
                                 (registrar {:valor 10 :tipo "receita"})
                                 (registrar {:valor 100 :tipo "receita"})
                                 (registrar {:valor 1000 :tipo "receita"})
                                 (saldo) => 1111)


                           (fact "Saldo é negativo quando só há despesa"
                                 (registrar {:valor 2 :tipo "despesa"})
                                 (registrar {:valor 20 :tipo "despesa"})
                                 (registrar {:valor 200 :tipo "despesa"})
                                 (registrar {:valor 2000 :tipo "despesa"})
                                 (saldo) => -2222)

                           (fact "Saldo é a soma das receitas menos a soma das despesas"
                                 (registrar {:valor 2 :tipo "despesa"})
                                 (registrar {:valor 10 :tipo "receita"})
                                 (registrar {:valor 200 :tipo "despesa"})
                                 (registrar {:valor 1000 :tipo "receita"})
                                 (saldo) => 808)))