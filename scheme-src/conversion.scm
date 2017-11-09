(define-alias StdLanguages com.intellij.lang.StdLanguages)
(define-alias PsiElement com.intellij.psi.PsiElement)

(define-syntax type-match
  (syntax-rules (else)
    ((type-match var (else result1 result2 ...))
     (begin result1 result2 ...))
    ((type-match var ((type new-var))) #f)
    ((type-match var ((type new-var)) clause1 clause2 ...)
     (or (instance? var type) (type-match var clause1 clause2 ...)))
    ((type-match var ((type new-var) result1 result2 ...))
     (if (instance? var type)
         (let ((new-var :: type (as type var))) result1 result2 ...)))
    ((type-match var ((type new-var) result1 result2 ...)
                 clause1 clause2 ...)
     (if (instance? var type)
         (let ((new-var :: type (as type var))) result1 result2 ...)
         (type-match var clause1 clause2 ...)))))

(define psi-to-text
  (lambda (element :: PsiElement)
    (cond
      ((eq? element #!null) "")
      ((not (eq? (element:get-language) StdLanguages:.JAVA)) "")
      (else
        (string-append "; Element " ((element:get-class):get-simple-name) " not supported\n")))))
