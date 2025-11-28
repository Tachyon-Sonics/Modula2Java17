MODULE CommentsInExpression;

VAR
    x, y, z: INTEGER;
    result: INTEGER;

BEGIN

    x := 10;
    y := 20;
    z := 30;

    (* Comment before a complex expression *)
    result := x (* first operand *) + y (* second operand *) * z (* third operand *);

    (* Comment in a parenthesized expression *)
    result := (x + (* inside parentheses *) y) * z;

    (* Comment in a function-like call structure *)
    result := x + (* between operators *) (y * z);

    (* Multiple comments in one expression *)
    result := (* start *) x + (* middle *) y - (* end *) z;

END CommentsInExpression.
