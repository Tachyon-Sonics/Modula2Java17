# Modula2Java17

## Introduction

This is a command-line tool that translates (or "compiles") working Modula-2 sources (PIM4) into Java sources (.java files).

Do not expect miracles. I only created this tool to convert two existing Modula-2 games I wrote years ago ([www.github.com/ChaosCastle/ChaosCastle](https://github.com/Tachyon-Sonics/ChaosCastle)). As such, the translator will handle any Modula-2 construct that appear in the sources of these games, but any other constructs are very likely *not* to work properly. This project does in no way attempt to achieve full compatibility or to implement any extensions like GNU gm2 for instance.

The translator generates Java source code that requires at least Java 17. The translator itself also requires at least Java 17.

There are four sub-projects (these are Eclipse projects, with gradle support):
- **Modula2Java17**: this is the Modula-2 to Java translator. It is also referred to as the "compiler", as it is implemented like a compiler behind the scene.
- **Modula2-Runtime**: a small runtime written in Java, that provides support for some Modula-2 constructs. Both the compiler and the produced Java code require it
- **Modula2-Library**: a *very incomplete* set of standard Modula-2 libraries (both Modula-2 .def files and the corresponding Java implementations)
    - Only the minimum to run the tests is provided. This can be used as a starting point though.
- **Modula2Java17-Tests**: automated tests (JUnit 4)


## Limitations

- Imports using the `FROM <module> IMPORT ...` syntax should work well. Imports using `IMPORT <module>` are not well tested as I never used them. `EXPORT` keyword (in `EXPORT QUALIFIED` for instance) is not supported.
- Handling of compilation errors is currently awful. If incorrect or unsupported Modula-2 code is encountered, this may result in any of the following:
    - Java code that does not compile or that is incorrect / crashes at execution
    - A strange exception in the compiler's sources
    - A meaningful error message that clearly explains the problem (yes, sometimes it happens!)
    - An unrelated error message, or an error message referring to the wrong file / line number
- There is no real standard library implementation, because the games I needed to convert used their own library that I rewrote in Java manually.
    - There is a *minimal* set of library-like definitions in the Modula2-Library subproject with Java implementations - these can be used as a starting point. Only the minimum to pass the tests have been implemented.
    - More generally, compiling a .def file (`DEFINITION MODULE`) that has no corrresponding .mod file (`IMPLEMENTATION MODULE`) will result in the creation of a stub Java implementation (if it does not already exists). The idea is that you can then implement it manually in Java by completing the generated stub code.
    - This is the *only* way to interface Modula-2 code with Java. Inlining Java code is not supported
- There is no support for incremental compilation. You specify a .mod file that is the main MODULE, and all required dependencies will be detected and compiled
    - Although it is possible to compile an `IMPLEMENTATION MODULE`, mind that
        - It will be recompiled *again* if you compile a `MODULE` or `IMPLEMENTATION MODULE` that uses it;
        - This may result in *different* Java code. Indeed in some cases a global analysis is used to generate proper Java code.
- Conversion of arbitrary types to `WORD` or `BYTE` is not supported.
- Minimal support for type-casting is provided using the `VAL(<type>, <value>)` function only.
     - Conversion of arbitrary values to `ARRAY OF BYTE` arguments is provided but with extremely limited support (only basic types - no record or arrays). However, it strictly follows a 32-bit (default) or 16-bit, big-endian data model.
- There is no graphical user interface. More generally, this tool is *not* suited for the developement of new Modula-2 projects (due to all the previous points). The purpose is to convert legacy Modula-2 code to Java once, and then to basically forget the original Modula-2 code and to continue working exclusively with the generated Java code.
- The compiler assumes that module names are PascalCase, and will use the camelCase version for the corresponding instances in the Java code.
    - This can result in name clashes (and hence Java code that does not compile) if the camelCase version corresponds to another identifier. For example if you have a module `Clock` and a variable `clock`, then lowercase `clock` will be used in the Java code for *both*, which is likely to result in incorrect Java code that does not compile.
    - If the Modula-2 code use camelCase module names... well rename then to PascalCase until I handle this case. Yeah, that sucks, but remind I only did this project to resurect two old games I wrote in Modula-2...
- `ARRAY [xx .. yy]`, where `xx` and `yy` are members of an enumeration is supported. However `ARRAY <EnumType>` and more generally `ARRAY <RangeType>` are not. `ARRAY [xx .. yy]` where `xx` and `yy` are integer constants works.
- Support for compile-time constants that involve arithmetic oprations (for instance in array bounds) is very limited.
- `RECORD` with `CASE` support is very limited (and probably buggy). No C-like union is created, instead separate fields are generated for each case.
- No support for corroutines
- No support for exceptions
- The compiler generates Java source files but does not compile and / or execute them. You must do it manually using `javac` and `java` or using any Java IDE. More generally this project is not really suited for people that do not know about Java.
- Many other limitations I forgot, or even Modula-2 constructs that I was never aware of (I vaguely heard of nested modules for example - not sure if it's part of the standard, but definitely not supported)...
- You need to install a Java Runtime Environment (JRE) or JDK version *17 or greater* to run the compiler. The generated Java code also requires at least Java 17.

 
## Command-line options

### Launching the compiler

When using the release .zip archive, the compiler can be invoked by dezipping the archive, going into the 'Modula2Java17' directory, and using:

```
java -jar Modula2Java17.jar <options> <MyModule.mod>
```

*Note*: the compiler requires a Java Runtime Environment (JRE) version *17 or greater*.

Please note that the compiler also requires the `Modula2-Runtime.jar` library (included in the release archive), so be sure to keep both `Modula2Java17.jar` and `Modula2-Runtime.jar` together. The generated Java code also requires `Modula2-Runtime.jar`.

If you got the sources from github and are using gradle, the release .zip archive can be build using the `release` gradle task of the `Modula2Java17` project.

If you got the sources from github and loaded them in a Java IDE, the main class to launch the compiler is `ch.pitchtech.modula.converter.Modula2JavaTranslator`.

The compiler is provided a command-line tool only. It accepts zero or more options, and a Modula-2 source file. In general you provide only the main Modula-2 module (as a `.mod` or `.mi` file) and all dependences will be detected and compiled automatically. The compiler except the modules and implementation modules to use the `.mod` or `.mi` extension, and the definition modules to use the `.def` or `.md` extension.

### Compiler options

- `-p` or `--package` <package>: name of the Java package to use for the generated Java source files. Default `org.modula2.generated`
- `-o` or `--output` <dir>: target directory in which to generate Java code. Defaults to current directory. Note that the Java package structure will be created inside of that directory
- `-pl` or `--package-library`: name of the Java package to use for the Java source files that are part of the "library" (Default `org.modula2.generated.library`)
    - The "library" corresponds to any .def files that have no corresponding .mod files. The assumption is that the implementation will be done manually in Java for these definitions.
    - If the Java files do not exist yet, the compiler will generate stub files (with method bodies that throw an `UnsupportedOperationException`) so that they can be completed manually.
    - If the Java files already exist, there are not modified.
- `-ol` or `--output-library`: target directory for the Java files of the "library". Default to the same as `-o`, but another value can be specified, for instance if you want to put them in a different Java project.
- `-s` or `--source` <dir>: specify an additional directory in which to look for Modula-2 source files. This options can be specified multiple times to add multiple source directories.
- `-dm` or `--data-model` `16|32`: choose 16-bit or 32-bit data model (size of the `INTEGER` Modula-2 type). Default 32-bit.


### Examples

These examples assume you are using the release zip. When launching the compiler from an IDE, some paths must be adjusted.


#### Example 1:

```
java -jar Modula2Java17.jar -p org.example MyModule.mod
```

This will compile `MyModule.mod` (assumed to be in the current directory) and all its dependencies (assumed to be in the same directory), without using any standard library. The generated Java files are placed in a package named `org.example`.

Because no standard library is used in this example, it is likely to fail.

*However*, if any `.def` file is included in the current directory *without* the corresponding `.mod` file (for instance the `.def` files corresponding to definition modules of a standard library), the above command will generate *stub java implementations* for them (in addition to compiling the `.mod` files).

The stub java implementations contain all methods, but without any implementation (just throwing an `UnsupportedOperationException`). The idea is that you can implement them in Java manually, starting from these generated stub implementations.

The stub java implementations are not generated again if they already exist. To generate them again, you must delete them.

The generated Java code may use helper classes from the `ch.pitchtech.modula.runtime` to support some Modula-2 constructs. These helper classes are found in `Modula2-Runtime.jar`, and their sources are in the `Modula2-Runtime` project. Hence to compile the generated Java file you must add `Modula2-Runtime.jar` to the Java classpath.


#### Example 2:

For this example, it is necessary to first decompress the `Modula2-Library-sources.jar` file into a directory named `Modula2-Library` (or any other directory - but you may then need to adjust the corresponding paths in the command line below).

```
java -jar Modula2Java17.jar -s "Modula2-Library/modula-2" -ol "Modula2-Library/src" -pl ch.pitchtech.modula.library -p org.example MyModule.mod
```

This will compile `MyModule.mod` and all its dependencies, using the provided standard library (that is *very incomplete*, basically just `InOut` and `Storage`). The generated Java files are placed in a package named `org.example`. The Java files will import classes of the standard library in the `ch.pitchtech.modula.library` package; hence you must add `Modula2-Library.jar` to the Java classpath to compile the resulting Java code. As usual, `Modula2-Runtime.jar` is required as well.

Note: file and directory paths can use either `/` or `\` on Windows.


#### Example 3:

```
java -jar Modula2Java17.jar -p org.example.app -s "./lib" -pl org.example.lib -o ./generated ./app/MyModule.mod
```

Assuming:
- The `./app` directory contains the `.mod` and `.def` files of the Modula-2 app to compile, including the main module `MyModule.mod`, but excluding the standard library (or any non-portable modules)
- The `./lib` directory contains the `.def` files for the standard library (or of non-portable modules), *without* the corresponding `.mod` files

The above will:
- Compile modula-2 files from the `./app` directory into the `./generated` directory, using the `org.example.app` package for the generated Java files
- Create *stub java implementations* for the standard library (or non-portable modules) into the `./generated` directory, using the `org.example.lib` package
    - The stub java implementations contain all methods, but without any implementation (just throwing an `UnsupportedOperationException`). The idea is that you can implement them in Java manually, starting from these generated stub implementations.
    - The stub java implementations are not generated again if they already exist. To generate one of them again, you must delete it and run the compilation again.
    

## Invoking the compiler programmatically

The compiler generates Java source files, but does not compile them. As such, most users will use a Java IDE to compile and execute the resulting Java code.

As the compiler itself is implemented in Java, it can make sense to invoke the compiler from Java code, for example as a small Java class's main method.

As an example, please refer to the `CompileGrotte` or `CompileChaosCastle` classes in package `ch.pitchtech.modula2.converter.tool` of the "Modula2Java17-Test" project. There are the two Java classes that I use to compile / recompile my two games (assuming the corresponding github's repo (https://github.com/Tachyon-Sonics/ChaosCastle) has been checked out in the same place locally). Basically the code to invoke the compiler involves:

- Creating and initializing a `FileOptions` to specify where the Modula-2 sources are, and where to produce the Java sources
- Creating and initializing a `CompilerOptions` to specify compiler options
- Creating a `Compiler` with the above options and invoke the `compile` method to translate the code


## Features

### Data models

#### 16-bit and 32-bit data models

The compiler provides 4 data models, that determine how to map Modula-2 types to Java types.

First, the following types are always mapped the same way, regardless of the data model (values for "Modula-2 size" are in bits):

| Modula-2 type | BYTE  | BOOLEAN | CHAR  | REAL  | LONGREAL | ADDRESS |
| ------------- | :---: | :-----: | :---: | :---: | :------: | :-----: |
| Modula-2 size | 8     | 8       | 16    | 32    | 64       |         |
| Java type     | byte  | boolean | char  | float | double   | Object  |


The following table shows the mapping for the other Modula-2 types, using the 32-bit (default) and 16-bit data models.
The 16-bit data model is enabled by passing `-dm 16` to the compiler.

| Modula-2 type | SHORTINT | SHORTCARD | INTEGER | CARDINAL | LONGINT | LONGCARD |
| ------------- | :------: | :-------: | :-----: | :------: | :-----: | :------: |
| Modula-2 size (32-bit) | 16       | 16        | 32      | 32       | 64      | 64       |
| Java (32-bit) | int      | int       | int     | u-int     | long    | u-long    |
| Modula-2 size (16-bit) | 8        | 8         | 16      | 16       | 32      | 32       |
| Java (16-bit) | int      | int       | int     | int      | long    | long     |

Notice that any Modula-2 numeric type that is less than 4 bytes is mapped to Java 4-bytes type `int`.

There are multiple reasons for this:

- This makes the Java code clearer, with less type casts. For instance, Modula-2 `a := b + c;` (where `a`, `b` and `c` are of a 2 bytes type) results in `a = b + c;` instead of `a = (short) (b + c)`. Indeed, Java only defines arithmetic operators on `int` and `long`, which means that a lot of type casts would be required if using `short` or `byte` instead of `int`.
- The compiler assumes the code has no overflow. Hence if the code does not overflow with 1-byte or 2-byte integer types, it won't overflow when those are converted to 4 bytes.
- Even if overflow is enabled (might be an option in a future version of the compiler), detecting overflow on `byte` and `short` can easily be done using `int`.

The above table mentions `u-int` and `u-long`. These types do not exist is Java. This just means that `int` and `long` are used respectively, and that the compiler will generate code to properly simulate unsigned types; namely:

- The `+`, `-`, `*`, `=` and `<>` operators use the corresponding Java operators, like for signed types. Indeed, the result of these operators are the same regardless of whether the arguments are interpreted as signed or unsigned.
- The `/` and `MOD` operators use the `divideUnsigned()` and `remainderUnsigned()` methods of the `Integer` (for `u-int`) or `Long` (for `u-long`) class.
- The `<`, `<=`, `>` and `>=` operators use the `compareUnsigned()` methods of the `Integer` (for `u-int`) or `Long` (for `u-long`) class.
- Assigning an unsigned variable to another variable of a larger size (e.g. `CARDINAL` to `LONGCARD` or `LONGINT`) will use the `toUnsignedInt()` or `toUnsignedLong()` methods of the `Byte`, `Short` or `Integer` classes as appropriate.

Notice that `LONGINT` and `LONGCARD` both use the Java `long` type in the 16-bit model, although `int` would be sufficient. The reason is that it allows the standard library to be the same for both memory models. Indeed, observe that the Java types for all numeric types are exactly the same in both data models.

In the 16-bit data model, `CARDINAL`, which is 2 bytes, is mapped to `int` rather than `u-int`. Indeed, the Java `int` type already covers the range of an unsigned 2-bytes number, and hence the compiler does not need to use unsigned helper methods. This again assumes that the Modula-2 code is free from overflows.

The values on the "Modula-2 size" rows in the above tables are only used:

- When the `SIZE` or `TSIZE` functions are used. However, code depending on the result of these functions is likely to be non-portable.
- When converting to an argument of type `ARRAY OF BYTE`. Note however that this conversion currently only works when an individual variable is used. It does not work with records or arrays.

`BITSET` is mapped to `Runtime.RangeSet(0, 31)` in the 32-bit data model, and to `Runtime.RangeSet(0, 15)` in the 16-bit data model. The `Runtime.RangeSet` class itself is based on `java.util.BitSet`. Hence `BITSET` (and more generally all Modula-2 SETs) are using Java objects behind the scene.


#### Strict data models

In addition to the 32-bit and 16-bit data models, the compiler also provides the "strict" 32-bit and the "strict" 16-bit data models. They are enabled using `-dm s32` and `-dm s16`, respectively. The following table shows the mapping to Java types:

| Modula-2 type | SHORTINT | SHORTCARD | INTEGER | CARDINAL | LONGINT | LONGCARD |
| ------------- | :------: | :-------: | :-----: | :------: | :-----: | :------: |
| Modula-2 size (strict 32-bit) | 16       | 16        | 32      | 32       | 64      | 64       |
| Java (strict 32-bit) | short    | u-short   | int     | u-int    | long    | u-long   | int   |
| Modula-2 size (strict 16-bit) | 8        | 8         | 16      | 16       | 32      | 32       |
| Java (strict 16-bit) | byte     | u-byte    | short  | u-short   | int     | u-int    | short |

Unlike the non-strict models, the "strict" models always use Java types whose size are exactly those of the Modula-2 type.

These "strict" data models are *not* recommanded, and are not actively supported. The reasons are:

- They potentially result in a *lot* of type casts in the generated Java code, especially when 1 byte and 2 bytes types are used, because Java promotes every operators to 4 bytes.
- The generated code is in general *not* compatible with the standard library, because the later is based on the non-strict 32-bit and 16-bit data models. Using the strict data models will usually require you to manually adapt the code from the standard library as well.


#### Loose unsigned types

Depending on the data model, the use of unsigned types (`SHORTCARD`, `CARDINAL` and `LONGCARD`) can result in a lot of boilerplate in the generated code, because the Java code has to use helper methods to handle signed Java types as unsigned.

If you known that one of the unsigned type will never be used with values greater than that of the corresponding signed type in your Modula-2 code, you can tell the compiler to use signed arithmetic instead, in order to simplify the generated Java code.

For instance, if you use a 64-bit model and you know that in your Modula-2 code, a `LONGCARD` will never be greater than 2<sup>63</sup> - 1 (the maximum value for the corresponding signed type `LONGINT`), the compiler can emit simpler code by treating unsigned 64 bit types as signed. This makes the unsigned type effectively one bit less, 63 bits in that case.

This feature is enabled by the `-lu` command-line option, with a comma-separated list of sizes (in bits) for which unsigned numbers of those sizes must be treated as signed. For example `-lu 32,64` will use `int` for `CARDINAL` and `long` for `LONGCARD` (assuming 32-bit data model), without using any helper methods like `compareUnsigned()` or `divideUnsigned()`.

Obviously, when this option is used, if a value of an unsigned type exceed the maximum value of its signed counterpart, undefined behavior will result as the value might become negative. This option generates simpler Java code, but is less compatible and should be used with care.


### Records, Arrays and Sets

Record types result in Java classes (and *not* in Java records, which are immutable). All members of a record are public fields of the Java class. However, the compiler *also* generates getter and setter for every field.

The public fields allow the Java code to be as close as possible to the Modula-2 code, i.e. for example `item.x = item.y` instead of `item.setX(item.getY())`.
The getter and setter are required in some cases though, see later about arguments by reference.

A variable of a `RECORD` type and a variable that is a `POINTER` to the same record type will result in the same Java declaration, namely a variable of the Java class corresponding to the record. However, different code is generated when these variables are assigned. For the `POINTER TO RECORD` variable, a Modula-2 assignment results in a simple Java assignment. However, for the `RECORD` variable, the Modula-2 assignment results in a call to the `copyFrom` helper methods that is generated in every Java classes corresponding to a `RECORD`.

Similarly, the `RECORD` variable uses the `newCopy` helper method when passed to a non-`VAR` argument.

Modula-2 arrays are mapped to Java arrays. Note that in Java, array indexes exclusively use the `int` type. As such, the maximum size of an array is limited to 2<sup>31</sup> - 1. If your Modula-2 code uses arrays larger than that, this will result in non-working Java code.

`SET OF` over an interval and `BITSET` are mapped to the `Runtime.RangeSet` class from the Modula-2 runtime. This class is similar to a `java.util.BitSet`, but with strictly enforced bounds, passed to the constructor. For instance, `BITSET` is mapped to a `new Runtime.RangeSet(0, 31)` in the 32-bit data model, and `new Runtime.RangeSet(0, 15)` in the 16-bit data model. `SET OF` over an enumerated type is mapped to the `Runtime.EnumSet` class.

Enumerations and sets are quite different in Modula-2 and Java, hence quite a lot of boilerplate code and helper methods can be generated in the Java code when used. Note that Modula-2 enumerated types are mapped to Java `enum` types.


### Nested Procedures

Nested procedures (`PROCEDURE` inside of another `PROCEDURE`) are supported. In the Java code they are flattened out. The name of a procedure "Toto" nested in procedure "Titi" will result in a Java method named "Titi_Toto".

In Modula-2, a nested procedure has access to all constants and variables of the enclosing procedure. The compiler tries to analyse exactly what constants and variables are _actually_ accessed, and pass only those as additional arguments to the resulting nested procedure when converted in Java.


### Arguments by value (default) and by reference (using `VAR`)

Java is only by-value. However, Modula-2 records are converted to Java objects, and Modula-2 arrays are converted to Java arrays. Those are both references in Java. Although a reference itself is passed by value in Java, it corresponds to the by-reference semantics of Modula-2 because the _content_ is modifiable.

If a record or array is passed by reference in Modula-2 (using `VAR`), the Java code is straightforward.

If a record or array is passed by value in Modula-2, the Java code will usually add a call to copy / duplicate the object or array.

If a simple type (primitive type, pointer, enum, etc) is passed by value in Modula-2, the Java code is straightforward.

If a simple type is passed by reference in Modula-2 (using `VAR`), the compiler will wrap it into a subclass of the `ch.pitchtech.modula.runtime.Runtime.IRef` interface that is part of the runtime:

- For a member of a record, or a variable of a module, a `FieldRef` is used (or a `FieldExprRef` if accessing an element of an _expression_ of record type). The `FieldRef` uses the getter and setters of that field or variable, which is why getters and setters are always generated, even if the fields are `public`.
- For an array element, an `ArrayElementRef` is used
- For a variable local to a procedure, the variable itself is wrapped into a `Ref`. Basically this is equivalent to converting it to a POINTER and dereferencing it on every usage, except when passed as `VAR` argument.

Note: if the compiler can determine that a `VAR` argument is never written by the procedure, it will pass it by value if it is a simple type in order to simplify the generated Java code. Note that this analysis is limited in scope; for instance as soon as an argument is passed as `VAR` to another procedure, it will be passed by reference, even if the other procedure does not write it.

Note: The `IRef` interface is a basic implementation of a "reference" or "pointer" in Java, and provides only two methods: `get` to get the value, and `set` to change it. Modula-2 pointers are based on the `Ref` class (also defined in `ch.pitchtech.modula.runtime.Runtime`), which is a straightforward implementation of the `IRef` interface that accesses a wrapped field. 


### Procedure types

Procedure types are supported; a procedure type is converted to a Java interface with a single method, and the `@FunctionalInterface` annotation.

A procedure used as an expression (when assigned to a variable of a procedure type), is converted to the Java 8 `module::procedure` syntax. Whenever a procedure is used as an expression, a `final` Java field with the `module::procedure` value is created next to it. This field is then used in the code instead of repeating the `module::procedure` expression everywhere. This is necessary to support equality/inequality comparison of variables of procedure type, because in Java every instance of the _same_ `module::procedure` expression results in a _different_ lambda (that are _not_ equal, even if they refer to the same class and method).


### Memory management

`NEW` and `DISPOSE` are supported. Only the versions with one argument are supported (tag variants are not supported).

The semantics are slightly different in Java, as `DISPOSE` will only set the pointer to `null` (the underlying object will eventually be collected by the garbage collector).

Note that *incorrect* Modula-2 code can behave very differently in Java. In particular, calling `DISPOSE` on the same pointer twice has no effect in Java (it will only set it to `null` twice).
Calling `DISPOSE` on a pointer whose referenced object is still referenced by another pointer will not result in a segmentation fault if the other pointer is dereferenced, because the referenced object will not be collected by the garbage collector.

With *correct* Modula-2 code, the semantics are practically similar.

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

A future version in which records are (optionally) mapped to Java `ByteBuffer`s might support pointer arithmetic as well as all pointer casts.


### Promotion of `ARRAY OF CHAR` to Java `String`

The compiler tries to replace all `ARRAY OF CHAR` to Java `String`. A future version may only do that as an option.

This can change the semantics, and result in incorrect code in extreme cases.

For instance, if the Modula-2 array has bounds (`ARRAY [<min>..<max>] OF CHAR`), these bounds are ignored, and array elements can be accessed past the limit. The `HIGH` function will return the current size of the `String` rather than the upper bound of the original Modula-2 array.

The compiler also assumes that all `ARRAY OF CHAR` are either filled up to the upper-bound, or zero-terminated. The `String` in the Java code will only hold chars up to (and not including) the zero char.


## Internals

This section is only useful if you plan to modify or contribute to the code of the compiler.

The compiler is implemented in multiple passes:

- Pass 1: Parsing. This pass delegates most of the work to the *antlr* library. See `Compiler.parse` and `Compiler.parseRecursive`. It makes use of the grammar file `m2pim4.g4`. Note that the parsing code and parse-tree classes (in package `ch.pitchtech.modula.converter.antlr.m2`) are auto-generated from this grammar file. Please refer to the documentation of the antlr library (org.antlr:antlr4).
    - Dependencies (imported modules) are detected and included for compilation in this pass.
- Pass 2: Abstraction. This pass transforms the parse-tree of the first pass to a more abstract model, based on classes of the `ch.pitchtech.modula.converter.model` package and sub-packages. If you are familiar with the Modula-2 language, you should find Java classes for basically every Modula-2 construct here. See `Compiler.createCompilationUnit`. Classes from the `ch.pitchtech.modula.converter.parser` package are responsible for this transformation pass.
    - Implementation note: the choice was made to implement this pass *after* the antlr parsing. Another implementation would be to merge this pass with the antlr parsing, by using antlr's listeners.
- Hidden Pass: Scope resolution. This is not implemented as an explicit pass, but rather computed on-the-fly whenever required. A future version may do it in an explicit pass. See classes from the `ch.pitchtech.modula.converter.model.scope` package, for example interfaces `IScope` and `IHasScope` (implemented by all Modula-2 constructs that have a scope), and `TypeResolver`.
- Pass 3: Analyze & Transform. This pass is actually a bag of multiple analyses and transforms. They are used whenever some Modula-2 construct is too far away from the corresponding Java construct, and need to be transformed to better match the Java version. See the `Transforms` class. This is used for example to analyse which variables are actually read or written, and to move nested PROCEDURE at top level. This pass is implemented by classes of the `ch.pitchtech.modula.converter.transform` package.
- Pass 4: Code generation. This last pass generates the resulting Java code. Implemented by classes of the `ch.pitchtech.modula.converter.generator` package. Note that many transformations are performed here on-the-fly rather than in the previous step if they are simple enough. Example is for-loop over an enumeration. This is currently the slowest pass, and it makes heavy use of the "Hidden Pass" to properly resolve variables, types, etc.
    - Note that code generation uses the `ResultContext` class, that is mostly based on Java's `StringBuilder`. Hence code generation generates Java source code directly as text. Code generation heavily uses the Modula-2 model of the code from the `ch.pitchtech.modula.converter.model` package (created in pass 2), that, thanks to pass 3, is close enough to the Java code to generate.

To understand the different passes, consider the following Modula-2 code fragment:

```
VAR
  p1, p2: Point;
  x: INTEGER;
```

- After pass 1, there is a "VAR" block, with two declaration lists. The first declaration list declares two variables "p1" and "p2" of type "Point", and the second list declares one variable "x" of type "INTEGER". "Point" is just an identifier at this stage, defined only by the `"Point"` string.
    - The code is modelled by the antlr-generated classes of the `ch.pitchtech.modula.converter.antlr.m2` package. The model is close to the exact syntax of the original code.
- After pass 2, the structure of the original code is abstracted. For instance, the fact there was two declaration lists is lost. What remains is that the enclosing module or procedure has three local variables: "p1" of type "Point", "p2" of type "Point" and "x" of type "INTEGER". "Point" is still just an identifier at this stage.
    - The code is now modelled by the classes of the `ch.pitchtech.modula.model` package and subpackages.
- After the "Hidden pass" (scope resolution), the nature of the "Point" type is now known (for example a RECORD with all its fields - it depends on the remainder of the code that is not shown here). After this pass, for every occurrences of "p1", "p2" or "x" in the code, it is also known where these variables are declared and what their types are. This pass also resolves ambiguities (such as a field in a "WITH" statement having priority over a variable of the same name).
- After pass 3, the compiler may for example know that a given procedure never reads "x", or never writes "p1", which can be used to optimize the generated code (such as "VAR" arguments, or nested procedures accessing variables of the enclosing one).
