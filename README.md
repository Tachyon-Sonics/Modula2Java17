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
    - More generally, compiling a .def file (`DEFINITION MODULE`) that has no corrresponding .mod file (`IMPLEMENTATION MODULE`) will result in the creation a stub Java implementation (if it does not already exists). The idea is that you can then implement it manually in Java by completing the generated stub code.
    - This is the only way to interface Modula-2 code with Java. Inlining Java code is not supported
- There is no support for incremental compilation. You specify a .mod file that is the main MODULE, and all required dependencies will be detected and compiled
    - Although it is possible to compile an `IMPLEMENTATION MODULE`, mind that
        - It will be recompiled *again* if you compile a `MODULE` or `IMPLEMENTATION MODULE` that uses it;
        - This may result in *different* Java code. Indeed in some cases a global analysis is used to generate proper Java code.
- Conversion of arbitrary types to `WORD` or `BYTE` is not supported.
- Minimal support for type-casting is provided using the `VAL(<type>, <value>)` function only.
     - Conversion of arbitrary values to `ARRAY OF BYTE` arguments is provided but with extremely limited support (only basic types - no record or arrays). However, it strictly follows a 16-bit, big-endian data model.
- There is no graphical user interface. More generally, this tool is *not* suited for the developement of new Modula-2 projects (due to all the previous points). The purpose is to convert legacy Modula-2 code to Java once, and then to basically forget the original Modula-2 code and to continue working exclusively with the generated Java code.
- The compiler assumes that module names are PascalCase, and will use the camelCase version for the corresponding instances in the Java code.
    - This can result in name clashes (and hence Java code that does not compile) if the camelCase version corresponds to another identifier. For example if you have a module `Clock` and a variable `clock`, because `clock` will be used in the Java code for the instance of the Class.
    - If the Modula-2 code use camelCase module names... well rename then to PascalCase until I handle this case. Yeah, that sucks, but remind I only did this project to resurect two old games I wrote in Modula-2...
- `ARRAY [xx .. yy]`, where `xx` and `yy` are members of an enumeration is supported. However `ARRAY <EnumType>` and more generally `ARRAY <RangeType>` are not. `ARRAY [xx .. yy]` where `xx` and `yy` are integer constants works.
- I think `BITSET` is broken. But `SET OF [0 .. 15]` should work.
- Support for compile-time constants that involve arithmetic oprations (for instance in array bounds) is very limited.
- RECORD with CASE support is very limited (and probably buggy). No C-like union is created, instead separate fields are generated for each case.
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


### Examples

These examples assume you are using the release zip. When launching the compiler from an IDE, some paths must be adjusted.


#### Example 1:

```
java -jar Modula2Java17.jar -p org.example MyModule.mod
```

This will compile `MyModule.mod` (assumed to be in the current directory) and all its dependencies (assumed to be in the same directory), without using any standard library (hence not very useful, and likely to fail). The generated Java files are placed in a package named `org.example`.

*However*, if any `.def` file is included in the current directory *without* the corresponding `.mod` file (for instance the `.def` files corresponding to definition modules of a standard library), the above command will generate *stub java implementations* for them (in addition to compiling the `.mod` files).

The stub java implementations contain all methods, but without any implementation (just throwing an `UnsupportedOperationException`). The idea is that you can implement them in Java manually, starting from these generated stub implementations.

The stub java implementations are not generated again if they already exist. To generate them again, you must delete them.


#### Example 2:

For this example, it is necessary to first decompress the `Modula2-Library-sources.jar` file into a directory named `Modula2-Library` (or any other directory - but you may then need to adjust the corresponding paths in the command line below).

```
java -jar Modula2Java17.jar -s "Modula2-Library/modula-2" -ol "Modula2-Library/src" -pl ch.pitchtech.modula.library -p org.example MyModule.mod
```

Tis will compile `MyModule.mod` and all its dependencies, using the provided standard library (that is *very incomplete*, basically just `InOut` and `Storage`). The generated Java files are placed in a package named `org.example`.

Note: file and directory paths can use either `/` or `\` on Windows.


#### Example 3:

```
java -jar Modula2Java17.jar -s "./lib" -p org.example.app -pl org.example.lib -o ./generated ./app/MyModule.mod
```

Assuming:
- The `./app` directory contains the `.mod` and `.def` files of the Modula-2 app to compile, including the main module `MyModule.mod`, but excluding the standard library (or any non-portable modules)
- The `./lib` directory contains the `.def` files for the standard library (or of non-portable modules), *without* the corresponding `.mod` files

The above will:
- Compile modula-2 files from the `./app` directory into the `./generated` directory, using the `org.example.app` package for the generated Java files
- Create *stub java implementations* for the standard library (or non-portable modules), using the `org.example.lib` package
    - The stub java implementations contain all methods, but without any implementation (just throwing an `UnsupportedOperationException`). The idea is that you can implement them in Java manually, starting from these generated stub implementations.
    - The stub java implementations are not generated again if they already exist. To generate them again, you must delete them.
    

## Invoking the compiler programmatically

The compiler generates Java source files, but does not compile them. As such, most users will use a Java IDE to compile and execute the resulting Java code.

As the compiler itself is implemented in Java, it can make sense to invoke the compiler from Java code, for example as a small Java class's main method.

As an example, please refer to the `CompileGrotte` or `CompileChaosCastle` classes in package `ch.pitchtech.modula2.converter.tool` of the "Modula2Java17-Test" project. There are the two Java classes that I use to compile / recompile my two games (assuming the corresponding github's repo (https://github.com/Tachyon-Sonics/ChaosCastle) has been checked out in the same place locally). Basically the code to invoke the compiler involves:

- Creating and initializing a `FileOptions` to specify where the Modula-2 sources are, and where to produce the Java sources
- Creating and initializing a `CompilerOptions` to specify compiler options
- Creating a `Compiler` with the above options and invoke the `compile` method to translate the code


## Features

### Data types

- Data types `SHORTINT`, `INTEGER` and `LONGINT` are mapped to Java `byte`, `short` and `int`
- Unsigned data types `SHORTCARD`, `CARDINAL` and `LONGCARD` are mapped to Java `short`, `int`, `long`

This is basically a 16-bit data model. It currently assumes no overflow and does not check for overflow. Mapping unsigned types to larger types allow the Java code to be natural, without requiring calls to `Integer.divideUnsigned` and similar. As long as the original Modula-2 code never overflows, the semantics are preserved. Note that unsigned numeric types `SHORTCARD`, `CARDINAL` and `LONGCARD` still use 1, 2 and 4 bytes respectively when converted to an argument of type `ARRAY OF BYTE`.

Note: This currently generates a *lot* of type-casts int the resulting code, because in Java any operation on `byte` or `short` result to an `int`. I plan to change that in the future so that only `int` and `long` are used for numeric types. This will make the Java code cleaner (although it will use more memory), and it would make it possible to have both a 16-bit and 32-bit model that result in compatible Java code.

Other simple types are either translated to the corresponding Java types, or to helper classes found in `ch.pitchtech.modula.runtime.Runtime`.
Modula-2 enumerated types are converted to Java enums. The generated code can be quite ackward when used in a `FOR` loop.


### Records

Record types result in Java classes (and *not* in Java records, which are immutable). All members of a record are public fields of the Java class. However, the compiler *also* generates getter and setter for every field.

The public fields allow the Java code to be as close as possible to the Modula-2 code, i.e. for example `item.x = item.y` instead of `item.setX(item.getY())`.
The getter and setter are required in some cases though, see later about arguments by reference.


### Nested Procedures

Nested procedures (`PROCEDURE` inside of another `PROCEDURE`) are supported. In the Java code they are flattened out. The name of a procedure "Toto" nested in procedure "Titi" will result in a Java method named "Titi_Toto".

In Modula-2, a nested procedure has access to all constants and variables of the enclosing procedure. The compiler tries to analyse exactly what constants and variables are _actually_ accessed, and pass only those as additional arguments to the resulting nested procedure when converted in Java.


### Arguments by value (default) and by reference (using `VAR`)

Java is only by-value. However, Modula-2 records are converted to Java objects, and Modula-2 arrays are converted to Java arrays. Those are both references in Java. Although a reference itself is passed by value in Java, it correspond to the by-reference semantics of Modula-2 because the _content_ is modifiable.

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

- Pass 1: Parsing. This pass delegates most of the work to the antlr library. See `Compiler.parse` and `Compiler.parseRecursive`. It makes use of the grammar file `m2pim4.g4`. Note that the parsing code and parse-tree classes (in package `ch.pitchtech.modula.converter.antlr.m2`) are auto-generated from this grammar file. Please refer to the documentation of the antlr library (org.antlr:antlr4).
    - Dependencies (imported modules) are detected and included for compilation in this pass.
- Pass 2: Abstraction. This pass transforms the parse-tree of the first pass to a more abstract model, based on classes of the `ch.pitchtech.modula.converter.model` package and sub-packages. If you are familiar with the Modula-2 language, you should find Java classes for basically every Modula-2 construct here. See `Compiler.createCompilationUnit`. Classes from the `ch.pitchtech.modula.converter.parser` package are responsible for this transformation pass.
    - Implementation note: the choice was made to implement this pass *after* the antlr parsing. Another implementation would be to merge this pass with the antlr parsing, by using antlr's listeners.
- Hidden Pass: Scope resolution. This is not implemented as an explicit pass, but rather computed on-the-fly whenever required. A future version may do it in an explicit pass. See classes from the `ch.pitchtech.modula.converter.model.scope` package, for example interfaces `IScope` and `IHasScope` (implemented by all Modula-2 constructs that have a scope), and `TypeResolver`.
- Pass 3: Analyze & Transform. This pass is actually a bag of multiple analyses and transforms. They are used whenever some Modula-2 construct is too far away from the corresponding Java construct, and need to be transformed to better match the Java version. See the `Transforms` class. This is used to analyse which variables are actually read or written, and to move nested PROCEDURE at top level for example. This pass is implemented by classes of the `ch.pitchtech.modula.converter.transform` package.
- Pass 4: Code generation. This last pass generates the resulting Java code. Implemented by classes of the `ch.pitchtech.modula.converter.generator` package. Note that many transformations are performed here on-the-fly rather than in the previous step if they are simple enough. Example is for-loop over an enumeration. This is currently the slowest pass, and it makes heavy use of the "Hidden Pass" to properly resolve variables, types, etc.

To understand the first passes, consider the following Modula-2 code fragment:

```
VAR
  p1, p2: Point;
  x: INTEGER;
```

- After pass 1, there is a "VAR" block, with two declaration lists. The first declaration list declares two variables "p1" and "p2" of type "Point", and the second list declares one variable "x" of type "INTEGER". "Point" is just an identifier at this stage, defined only by the `"Point"` string.
    - The code is modelled by the antlr-generated classes of the `ch.pitchtech.modula.converter.antlr.m2` package. The model is close to the exact syntax of the original code.
- After pass 2, the structure of the original code is abstracted. For instance, the fact there was two declaration lists is lost. What remains in that the enclosing module or procedure has three local variables: "p1" of type "Point", "p2" of type "Point" and "x" of type "INTEGER". "Point" is still just an identifier at this stage.
    - The code is now modelled by the classes of the `ch.pitchtech.modula.model` package and subpackages.
- After the "Hidden pass" (scope resolution), the nature of the "Point" type is now known (for example a RECORD with all its fields - it depends on the remainder of the code that is not shown here). After this pass, for every occurrences of "p1", "p2" or "x" in the code, it is also known where these variables are declared and what their types are. This pass also resolves ambiguities (such as a field in a "WITH" statement having priority over a variable of the same name).
- After pass 3, the compiler may for example know that a given procedure never reads "x", or never writes "p1", which can be used to optimize the generated code (such as "VAR" arguments, or nested procedures accessing variables of the enclosing one).
