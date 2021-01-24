1. how do we know this type is right 
    ```
        // CRINGE(how do we know this is the right type?)
         IfStatement(startParsing(l) as Expression<BooleanType>, parseBlock(l))
    ```
    Solution: ckecking the type is for later in the compile process

1. `/*eww*/buildList { while (l.hasNext()) add(startParsing(l)) }`
1. type checker
1. type tracker
1. value tracker  