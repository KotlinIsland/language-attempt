# It type and type predicates
```ts
type Exactly<T, Bound> = T extends Bound ? Bound extends T ? T : never : never
```

uhh, cringe?
```ts
type Exactly<Bound> = It extends Bound && Bound extends It

type A = { a: unknown }
type B = A & { b: unknown }
type C = B & { c: unknown }
type D = C & { d: unknown }

function foo(t: C extends It) { // any super type of C
    
}
```
`It` acts as an implicit polymorphic type

Errors would also read much better
```ts
// TS
foo({ a:1, b:2, c:3, d:4 }) // Type 'number' is not assignable to type 'never'.
// this lang
foo({ a:1, b:2, c:3, d:4 })
// the shape of this data does not match the condition "C extends It"
// OR
// the shape of this data does not match the condition that is it a super type of C
```