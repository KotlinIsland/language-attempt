1. compile error results with start/end
1. value tracking
   ```
   var a = true
   if (a != true) { } // should be compile error
   ```
1. type checker
1. type tracker
1. value tracker
   ### longer goals 
1. ```kt
   class Foo {
     var a: Any? = null
     var b get() = a      
   }
   val f = Foo()
   f.a = 1
   val b: Int = f.b
   ```
1. undefined tracking
1. union values ie `if a == 1 | 2`

1. The ability to have interfaces and types exist at run time to reduce coding 
   e.g. 
   ```ts
   type SomeType = "a" | "b" | "c"
   
   const a = valuesOf SomeType //builds ["a" , "b" , "c"]
   ```