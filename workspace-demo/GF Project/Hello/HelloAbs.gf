abstract HelloAbs = AbsCat [Greeting, Recipient] ** {

  flags
    startcat = Greeting ;

  cat
    Farewell ;

  fun
    Hello : Recipient -> Greeting ;
  data
    Goodbye : Recipient -> Farewell ;
    World, Parent, Friends : Recipient ;
}
