--# -path=.:/home/john/.cabal/share/gf-3.3/lib/alltenses
concrete ClEng of Cl = VerbsEng ** open SyntaxEng, ParadigmsEng, (R = ResEng), VPConj, ExtraEng in {

param
  ClType = Violation | Satisfaction | Other ;

oper
  CCl = {s : SyntaxEng.S; ty : ClType};

lincat
  NP = SyntaxEng.NP;  

  Act     = VP; 
  Clause  = CCl;
  ClauseO = {vp : VP; cl : CCl};
  ClauseP = VP;
  ClauseF = {vp : VP; cl : CCl};

  [Act] = ListVP;

lin
  clauseO np co = {
    s  = let s1 : S = mkS presentTense simultaneousAnt positivePol (mkCl np (PassVPSlash (mkVPSlash (mkV2V (mkV "oblige") noPrep (mkPrep "to")) co.vp))) ;
             s2 : S = mkS (SyntaxEng.mkAdv if_Subj (mkS presentTense simultaneousAnt negativePol (mkCl np co.vp))) co.cl.s ;
         in case co.cl.ty of {
              Violation => s1 ;
              _         => mkS (mkConj ",") (mkListS s1 s2)
            } ;
    ty = Other
  } ;

  clauseP np cp = {
    s  = mkS presentTense simultaneousAnt positivePol (mkCl np cp) ;
    ty = Other
  } ;

  clauseF np cf = {
    s  = let s1 : S = mkS presentTense simultaneousAnt negativePol (mkCl np (mkVP shall_VV cf.vp)) ;
             s2 : S = mkS (SyntaxEng.mkAdv if_Subj (mkS presentTense simultaneousAnt positivePol (mkCl np cf.vp))) cf.cl.s ;
         in case cf.cl.ty of {
              Violation => s1 ;
              _         => mkS (mkConj ",") (mkListS s1 s2)
            } ;
    ty = Other
  } ;

  cond np a c = {
    s  = mkS if_then_Conj (mkS presentTense simultaneousAnt positivePol (mkCl np a)) c.s ;
    ty = Other
  } ;

  satisfaction = {
    s  = lin S {s="^|^"} ;
    ty = Satisfaction
  } ;

  violation    = {
    s  = lin S {s="_|_"} ;
    ty = Violation
  } ;

  O act cl = {vp = act; cl = cl} ;

  P act = mkVP may_VV act ;
  
  F act cl = {vp = act; cl = cl} ;

  atom v np = mkVP (mkV2 v) np ;

  andAct = ConjVP and_Conj ;

  BaseAct = BaseVP;
  ConsAct = ConsVP;

  npSg s = lin NP (R.mkNP s.s s.s (s.s ++ "'s") singular R.P3 R.Neutr) ;
  npPl s = lin NP (R.mkNP s.s s.s (s.s ++ "'s") plural R.P3 R.Neutr) ;
  

oper
  shall_VV = lin VV {
    s = table { 
      R.VVF R.VInf => ["shall"] ;
      R.VVF R.VPres => "shall" ;
      R.VVF R.VPPart => ["shall"] ;
      R.VVF R.VPresPart => ["shall"] ;
      R.VVF R.VPast => "shall" ;
      R.VVPastNeg => "shall not" ;
      R.VVPresNeg => "shall not"
      } ;
    typ = R.VVAux
    } ;
  may_VV = lin VV {
    s = table { 
      R.VVF R.VInf => ["may"] ;
      R.VVF R.VPres => "may" ;
      R.VVF R.VPPart => ["may"] ;
      R.VVF R.VPresPart => ["may"] ;
      R.VVF R.VPast => "may" ;
      R.VVPastNeg => "may not" ;
      R.VVPresNeg => "may not"
      } ;
    typ = R.VVAux
    } ;
  
}