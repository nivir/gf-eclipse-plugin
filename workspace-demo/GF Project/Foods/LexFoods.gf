-- Lexicon Interface
-- (a resource which contains only oper TYPES)
interface LexFoods =
  open Syntax in {

  param
    Case = Nom | Acc ;

  oper
    wine_N : N ;
    pizza_N : N ;
    cheese_N : N ;
    fish_N : N ;
    fresh_A : A ;
    warm_A : A ;
    italian_A : A ;
    expensive_A : A ;
    delicious_A : A ;
    boring_A : A ;
} ;
