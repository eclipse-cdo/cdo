This project contains the task-oriented documentation for CDO, currently split up into parts
for the target audiences users, (server) operators, and programmers, as well as a part for reference
information such as JavaDoc (API reference), and (extension point) schema reference.

Technically the task-oriented **documentation** is authored in the form of Java packages, classes, nested classes and methods:

* A package represents a **category** for related documentation articles. 
  A category is documented in the package-info.java file of the package.
  The ordering of categories within their parent category is specified with a @number JavaDoc block tag.
* A top-level class represents an **article** in the documentation.
  An article is a top-level chapter.
* Nested classes represent a tree of documentation **chapters**.

The JavaDoc of these structural elements represent the actual documentation text:

* The first JavaDoc sentence represents the **title** of the structural element (category, article, or chapter).
* The subsequent JavaDoc paragraphs represent a list of **sections**.
* The JavaDoc can use regular formatting tags.
* The JavaDoc can use regular {@link ...} syntax to refer to
  * other structural elements of the documentation (i.e., articles and chapters).
  * other Java elements of CDO (e.g., classes, interfaces, methods, constants).
* The JavaDoc can use certain custom tags (interpreted by a custom doclet):
  * The block tag "@snip" on a method indicates that the method body represents a code example (a snippet)
    for the documentation. The method header must start with "public void". Method parameters can be specified
    to provide required input for the code. Other structural elements can "embed" the code snippet by using the
    regular {@link ...} syntax to refer to method of the snippet.

Here are some general rules for writing the documentation:

* Wrap lines at column 120.
* Classes are always public, non static, and non final.
* Methods are always public void and non static.
* All Java types in links and code examples are written with simple names (not qualified with package names).
  The required Java import statements are added at the top of the file under the package declaration.
* The @snip block tag comes last in a Java code example method.
  It's usually the only tag on a snippet.

# Meta tags for Copilot

* The JavaDoc tag @explain is a hint for Copilot. The paragraph following @explain is an instruction for Copilot
  to write one or more sections for the topics listed in the instruction. The written sections fully replace
  the @explain tag and the following instruction.
  

