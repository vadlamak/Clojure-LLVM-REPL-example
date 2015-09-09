(ns llvm-example.core-test
  (:require [clojure.test :refer :all]
            [llvm-example.core :refer :all]))

(defmacro with-llvm-module-builder [llvm-module builder & body]
  "Run a body of code with an empty LLVM module declared as ~llvm-module and builder declared as ~builder, and perform
  necessary cleanup afterwards."
  `(let [~llvm-module (LLVMModuleCreateWithName "test")]
     (try
        (let [~builder (LLVMCreateBuilder)]
          (try ~@body
            (finally (LLVMDisposeBuilder ~builder))))
        (finally (LLVMDisposeModule ~llvm-module)))))

(deftest test-example
  (with-llvm-module-builder llvm-module builder
    (let [args (into-array [(LLVMDoubleType) (LLVMDoubleType)])
          fn-type (LLVMFunctionType (LLVMDoubleType) args (count args) 0)
          llvm-fn (LLVMAddFunction llvm-module "average" fn-type)]
      (is (= "; ModuleID = 'test'\n\ndeclare double @average(double, double)\n"
             (LLVMPrintModuleToString llvm-module))))))

