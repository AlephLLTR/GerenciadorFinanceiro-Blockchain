(ns blockchain.blockchain
  (:import
   [java.security MessageDigest]) 
  (:gen-class))

(defn sha256 [input]
  (let [digest (MessageDigest/getInstance "SHA-256")]
    (->> (.digest digest (.getBytes input "UTF-8"))
         (map #(format "%02x" %))
         (apply str))))

(defn obter-hash [_INDEX _NONCE _DATA _hPREVIOUS]
  (sha256 (str _INDEX _NONCE _DATA _hPREVIOUS)))

(defn bloco [_INDEX _NONCE _DATA _hPREVIOUS _hCURRENT]
  {:INDEX _INDEX
   :NONCE _NONCE
   :DATA _DATA
   :HASHPREVIOUS _hPREVIOUS
   :HASHCURRENT _hCURRENT})

(defn minerar [_INDEX _DATA _hPREVIOUS _NONCE]
  (let [_hCURRENT (obter-hash _INDEX _NONCE _DATA _hPREVIOUS)]
    (if (.startsWith _hCURRENT "0000")
      (bloco _INDEX _NONCE _DATA _hPREVIOUS _hCURRENT)
      (recur _INDEX _DATA _hPREVIOUS (inc _NONCE)))))

(defn fundacao []
    (minerar 0 "Bom Dia, flor do dia!" "0000000000000000000000000000000000000000000000000000000000000000" 0)    
)

(def cadeia (atom (conj [] (fundacao))))

(defn blocos [] @cadeia)

(defn adicionar [transacao]
  (if (empty? @cadeia)
    (reset! cadeia (conj [] (fundacao))))
  (let [blkFinal (peek @cadeia)
        blkProx (inc (:INDEX blkFinal))
        blkNovo (minerar blkProx transacao (:HASHCURRENT blkFinal) 0)]
    (swap! cadeia conj blkNovo)
    ))