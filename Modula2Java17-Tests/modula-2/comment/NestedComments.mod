MODULE NestedComments;

(* This is a simple comment *)

VAR
    x: INTEGER;

BEGIN

    (* This is a nested comment (* inner comment *) outer continues *)
    x := 42;

    (* Multi-level nesting
       (* level 2
          (* level 3 *)
       level 2 again *)
    level 1 again *)
    x := x + 1;

    (* Comment with (* nested (* double nested *) *) still in comment *)
    x := x * 2;
    
    (*
     * Multi-line
     * comment
     *)
     x := x / 4;

END NestedComments.
