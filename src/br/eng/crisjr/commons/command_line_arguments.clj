(ns br.eng.crisjr.commons.command-line-arguments)

(defn parse [args]
  (loop [head (first args)
         tail (rest args)
         state :value
         key "tool"
         outlet {}]
    (if (nil? head)
      outlet
      (recur (first tail)
             (rest tail)
             (if (= state :key)
               :value
               :key)
             (when (= state :key)
               head)
             (if (= state :key)
               outlet
               (assoc outlet key head))))))

