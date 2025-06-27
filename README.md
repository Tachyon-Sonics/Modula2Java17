# Modula2Java17
## Introduction

This is a command-line tool that translates (or "compiles") working Modula-2 sources (PIM4) into Java sources (.java files).

Do not expect miracles. I only did this tool to convert two existing Modula-2 games I wrote years ago. As such, the translator will handle any Modula-2 construct that appear in the sources of these games, but any other constructs are very likely *not* to work properly. This project does in no way attempt to achieve full compatibility or to implement any extensions like GNU m2c for instance.

The translator generates Java source code that requires at least Java 17. The translator itself also requires at least Java 17.

There are four sub-projects (these are Eclipse project, with gradle support):
- Modula2Java17: this is the Modula-2 to Java translator. It is also referred as the "compiler" as it is implemented like a compiler behind the scene.
- Modula2-Runtime: a small runtime written in Java, that provides support for some Modula-2 constructs. Both the compiler and the produced Java code require it
- Modula2-Library: a very incomplete set of standard Modula-2 libraries (both Modula-2 .def files and the corresponding Java implementations)
    - Only the minumum to run the tests is provided. This can be used as a starting point though.
- Modula2Java17-Tests: tests (JUnit 4)

## Limitations

- Imports using the `FROM <module> IMPORT ...` syntax should work well. Imports using `IMPORT <module>` are not well tested as I never used them.
- Handling of compilation errors is currently awful. If incorrect or unsupported Modula-2 code is encountered, this may result in any of the following:
    - Java code that does not compile or that is incorrect / crashes at execution
    - A strange exception in the compiler's sources
    - A meaningful error message that clearly explains the problem (yes, sometimes it happens!)
    - An unrelated error message, or an error message referring to the wrong file / line number
- There is no real standard library implementation, because the games I needed to convert used their own library that I rewrote in Java manually.
    - There is a *minimal* set of library-like definitions in the Modula2-Library subproject with Java implementations - these can be used as a starting point. Only the minimum to pass the tests have been implemented.
    - More generally, compiling a .def file (`DEFINITION MODULE`) that has no corrresponding .mod file (`IMPLEMENTATION MODULE`) will result in the creation a stub Java implementation (if it does not already exists). The idea is that you can then implement it manually in Java by completing the generated stub code.
    - This is the only way to interface Modula-2 code with Java. Inlining Java code is not supported
- There is no support for incremental compilation. You specify a .mod file that is the main MODULE, and all required dependencies will be detected and compiled
    - Although it is possible to compile an `IMPLEMENTATION MODULE`, mind that
        - It will be recompiled _again_ if you compile a `MODULE` or `IMPLEMENTATION MODULE` that uses it;
        - This may result in _different_ Java code. Indeed in some cases a global analysis is used to generate proper Java code.
- Conversion of arbitrary types to `WORD` or `BYTE` is not supported.
- Minimal support for type-casting is provided using the `VAL(<type>, <value>)` function only.
     - Conversion of arbitrary values to `ARRAY OF BYTE` arguments is provided but with extremely limited support (only basic types - no record or arrays). However, it strictly follows a 16-bit, big-endian data model.
- There is no graphical user interface. More generally, this tool is _not_ suited for the developement of new Modula-2 projects (due to all the previous points). The purpose is to convert legacy Modula-2 code to Java once, and then to basically forget the original Modula-2 code and to continue working exclusively with the generated Java code.
- The compiler assumes that module names are PascalCase, and will use the camelCase version for the corresponding instances in the Java code.
    - This can result in name clashes (and hence Java code that does not compile) if the camelCase version corresponds to another identifier. For example if you have a module `Clock` and a variable `clock`, because `clock` will be used in the Java code for the instance of the Class.
    - If the Modula-2 code use camelCase module names... well rename then to PascalCase until I handle this case. Yeah, that sucks, but remind I only did this project to resurect two old games I wrote in Modula-2...
- `ARRAY [xx .. yy]`, where `xx` and `yy` are members of an enumeration is supported. However `ARRAY <EnumType>` and more generally `ARRAY <RangeType>` are not. `ARRAY [xx .. yy]` where `xx` and `yy` are integer constants works.
- I think `BITSET` is broken. But `SET OF [0 .. 15]` should work.
- Support for compile-time constants that involes arithmetic oprations (for instance in array bounds) is very limited.
- RECORD with CASE support is very limited (and probably buggy). No C-like union is created, instead separate fields are generated for each case.
- No support for corroutines
- No support for exceptions
- The compiler generates Java source files but does not compile and / or execute them. You must do it manually using `javac` and `java` or using any Java IDE. More generally this project is not really suited for people that do not know about Java.
- Many other limitations I forgot, or even Modula-2 constructs that I was never aware of (I vaguely heard of nested modules for example - not sure if it's part of the standard, but definitely not supported)...

 
## Command-line options

The main class of the compiler / translator is `ch.pitchtech.modula.converter.Modula2JavaTranslator`.

It accepts zero or more options, and a Modula-2 source file. In general you provide only the main Modula-2 module (as a `.mod` file) and all dependences will be detected and compiled automatically.

Options:

- `-p` or `--package` <package>: name of the Java package to use for the generated Java source files. Default `org.modula2.generated`
- `-o` or `--output` <dir>: target directory in which to generate Java code. Defaults to current directory. Note that the Java package structure will be created inside of that directory
- `-pl` or `package-library`: name of the Java package to use for the Java source files that are part of the "library" (Default `org.modula2.generated.library`)
    - The "library" corresponds to any .def files that have no corresponding .mod files. The assumption is that the implementation will be done manually in Java for these definitions.
    - If the Java files do not exist yet, the compiler will generate stub files (with method bodies that throw an `UnsupportedOperationException`) so that they can be completed manually.
    - If the Java files already exist, there are not modified.
- `-ol` or `output-library`: target directory for the Java files of the "library". Default to the same as `-o`, but another value can be specified, for instance if you want to put them in a different Java project.

Note: by specifying `-ol` with the path of the "Modula2-Library/src" project and `-pl ch.pitchtech.modula.library` (the corresponding package), you can compile code using the existing standard library. Note however that it is *very* incomplete. In practice you may want to write your own version from scratch...


## Invoking the compiler prgrammatically

The compiler generates Java source files, but does not compile them. As such, most users will use a Java IDE to compile and execute the resulting Java code.

As the compiler itself is implemented in Java, it can make sense to invoke the compiler from Java code, for example as a small Java class's main method.

As an example, please refer to the `CompileGrotte` or `CompileChaosCastle` classes in package `ch.pitchtech.modula2.converter.tool` of the "Modula2Java17-Test" project. There are the two Java classes that I use to compile / recompile my two games (assuming the corresponding github's repo (https://github.com/Tachyon-Sonics/ChaosCastle) has been checked out in the same place locally). Basically the code to invoke the compiler involves:

- Creating and initializing `FileOptions` to specify where the Modula-2 sources are, and where to produce the Java sources
- Creating and initializing a `CompilerOptions` to specify compiler options
- Creating a `Compiler` with the above options and invoke the `compile` method to translate the code


## Features

### Data types

- Data types `SHORTINT`, `INTEGER` and `LONGINT` are mapped to Java `byte`, `short` and `int`
- Unsigned data types `SHORTCARD`, `CARDINAL` and `LONGCARD` are mapped to Java `short`, `int`, `long`

This is basically a 16-bit data model. It currently assumes no overflow and does not check for overflow. Mapping unsigned types to larger types allow the Java code to be natural, without requiring calls to `Integer.divideUnsigned` and similar. As long as the original Modula-2 code never overflows, the semantics are preserved. Note that unsigned numeric types `SHORTCARD`, `CARDINAL` and `LONGCARD` still use 1, 2 and 4 bytes respectively when converted to an argument of type `ARRAY OF BYTE`.

Note: This currently generates a _lot_ of type-casts int the resulting code, because in Java any operation on `byte` or `short` result to an `int`. I plan to change that so that only `int` and `long` are used for numeric types. This will make the Java code cleaner (although it will use more memory), and it would make it possible to have both a 16-bit and 32-bit model that result in compatible Java code.

Other simple types are either translated to the corresponding Java types, or to helper classes found in `ch.pitchtech.modula.runtime.Runtime`.
Modula-2 enumerated types are converted to Java enums. The generated code can be quite ackward when used in a `FOR` loop.


### Records

Record types result in Java classes (and _not_ in Java records, which are immutable). All members of a record are public fields of the Java class. However, the compiler _also_ generates getter and setter for every field.

The public fields allow the Java code to be as close as possible to the Modula-2 code, i.e. for example `item.x = item.y` instead of `item.setX(item.getY())`.
The getter and setter are required in some cases though, see later about arguments by reference.


### Nested Procedures

Nested procedures (`PROCEDURE` inside of another `PROCEDURE`) are supported. In the Java code they are flattened out. The name of a procedure "Toto" nested in procedure "Titi" will result in a Java method named "Titi_Toto".

In Modula-2, a nested procedure has access to all constants and variables of the enclosing procedure. The compiler tries to analyse exactly what constants and variables are _actually_ accessed, and pass only those as additional arguments to the resulting nested procedure when converted in Java.


### Arguments by value (default) and by reference (using `VAR`)

Java is only by-value. However, Modula-2 records are converted to Java objects, and Modula-2 arrays are converted to Java arrays. Those are both references in Java. Although a reference itself is passed by value in Java, it correspond to by-reference semantics in Modula-2 because the _content_ is modifiable.

If a record or array is passed by reference in Modula-2 (using `VAR`), the Java code is straightforward.

If a record or array is passed by value in Modula-2, the Java code will usually add a call to copy / duplicate the object or array.

If a simple type (primitive type, pointer, enum, etc) is passed by value in Modula-2, the Java code is straightforward.

If a simple type is passed by reference in Modula-2 (using `VAR`), the compiler will wrap it into a subclass of the `ch.pitchtech.modula.runtime.Runtime.IRef` interface that is part of the runtime:

- For a member of a record, or a variable of a module, a `FieldRef` is used (or a `FieldExprRef` if accessing an element of an _expression_ of record type). The `FieldRef` uses the getter and setters of that field or variable, which is why getters and setters are always generated, even if the fields are `public`.
- For an array element, an `ArrayElementRef` is used
- For a variable local to a procedure, the variable itself is wrapped into a `Ref`. Basically this is equivalent to converting it to a POINTER and dereferencing it on every usage, except when passed as `VAR` argument.

Note: if the compiler can determine that a `VAR` argument is never written by the procedure, it will pass it by value if it is a simple type in order to simplify the generated Java code. Note that this analysis is limited in scope; for instance as soon as an argument is passed as `VAR` to another procedure, it will be passed by reference, even if the other procedure does not write it.

Note: The `IRef` interface is a basic implementation of a "reference" or "pointer" in Java, and provides only two methods: `get` to get the value, and `set` to change it. Modula-2 pointers are based on the `Ref` class (also defined in `ch.pitchtech.modula.runtime.Runtime`), which is a straightforward implementation of the `IRef` interface that accesses to a wrapped field. 


### Procedure types

Procedure types are supported; a procedure type is converted to a Java interface with a single method, and the `@FunctionalInterface` annotation.

A procedure used as an expression (when assigned to a variable of a procedure type), is converted to the Java 8 `module::procedure` syntax. Whenever a procedure is used as an expression, a `final` Java field with the `module::procedure` value is created next to it. This field is then used in the code instead of repeating the `module::procedure` expression everywhere. This is necessary to support equality/inequality comparison of variables of procedure type, because in Java every instance of the _same_ `module::procedure` expression results in a _different_ lambda (that are _not_ equal, even if they refer to the same class and method).


### CLOSE statement

This is the only non-standard extension that is supported. In a module or implementation module, the `BEGIN` block can be followed by a `CLOSE` block before the `END <Module>.`. It corresponds to code that must be executed on termination, like a Java `finally` block. The `CLOSE` block is executed when the execution reaches the end of the `BEGIN` block, when an exception occur, or if a `HALT` statement is reached. `CLOSE` blocks of multiple implementation modules are executed in reverse order, compared to their `BEGIN` blocks.

This can only be used in the module's body. It cannot be used in procedures.


### Memory management

`NEW` and `DISPOSE` are supported. Only the versions with one argument are supported (tag variants are not supported).

The semantics are slightly different in Java, as `DISPOSE` will only set the pointer to `null` (the underlying object will eventually be collected by the garbage collector).

Note that _incorrect_ Modula-2 code can behave very differently in Java. In particular, calling `DISPOSE` on the same pointer twice has no effect in Java (it will only set it to `null` twice).
Calling `DISPOSE` on a pointer whose referenced object is still referenced by another pointer will not result in a segmentation fault if the other pointer is dereferenced, because the referenced object will not be collected by the garbage collector.

With _correct_ Modula-2 code, the semantics are practically similar.

`ALLOCATE` and `DEALLOCATE` from `Storage` (implemented in the "Modula2-Library" project) are partially supported, but _only_ if the code properly uses `SIZE` or `TSIZE` exacly once before each call to `ALLOCATE` or `DEALLOCATE`. They also only work for pointers to `RECORD`s.
They result in Java code that is more complex, but equivalent to `NEW` and `DISPOSE`.

For example, the following is ok (assuming `point` is a `POINTER TO Point`, etc):
```
ALLOCATE(point, SIZE(Point));
ALLOCATE(rectangle, SIZE(Rectangle));
```

`SIZE` can be replaced by `TSIZE`, the compiler does not differentiate them and both can be applied to either a type or a variable.

Similarly, the following should also work:
```
size := SIZE(Point);
ALLOCATE(point, size);
size := SIZE(Rectangle);
ALLOCATE(rectangle, size);
```

However, the following will _not_ work:
```
size1 := SIZE(Point);
size2 := SIZE(Rectangle);
ALLOCATE(point, size1);
ALLOCATE(rectangle, size2);
```

And obviously, this will not work either because `SIZE` (or `TSIZE`) is not invoked:
```
ALLOCATE(point, 4);
ALLOCATE(rectangle, 8);
```

Behind the scene, `SIZE` (and `TSIZE`) are implemented by a Java method that returns an unsigned integer value to comply with the function's signature (the returned value is an approximate size of the underlying type generated at compile time - but you shouldn't really rely on it), and also stores the corresponding Java class in a `ThreadLocal`. The Java code for `ALLOCATE` then retrieves the stored Java class from the `ThreadLocal` so it can then create the corresponding object using introspection.

Pointer arithmetic is not supported.

Some pointer casts (for example casting a `POINTER TO XX` to `POINTER TO YY`, where `XX` is a record whose first field is of type `YY`) will not work either (altgough technically correct in Modula-2, but not very elegant), and result in an error at runtime (but the generated code will compile, and can be fixed manually).

Casting a pointer of a given type to an `ADDRESS`, and back to a pointer of the same type should work. The `ADDRESS` type is mapped to Java's `Object`.

A future version in which records are (optionally) mapped to Java `ByteBuffer` might support pointer arithmetic as well as all pointer casts.


### Promotion of `ARRAY OF CHAR` to Java `String`

The compiler tries to replace all `ARRAY OF CHAR` to Java `String`. A future version may only do that as an option.

This can change the semantics, and result in incorrect code in extreme cases.

For instance, if the Modula-2 array has bounds (`ARRAY [<min>..<max>] OF CHAR`), these bounds are ignored, and array elements can be accessed past the limit. The `HIGH` function will return the current size of the `String` rather than the upper bound of the original Modula-2 array.

The compiler also assumes that all `ARRAY OF CHAR` are either filled up to the upper-bound, or zero-terminated. The `String` in the Java code will only hold chars up to (and not including) the zero char.


## Internals

TODO
