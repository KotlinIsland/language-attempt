1. compile error results with start/end
1. value tracking
   ```
   var a = true
   if (a == false) { }
   ```
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