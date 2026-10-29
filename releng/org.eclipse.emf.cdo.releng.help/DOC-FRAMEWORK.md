# CDO/Net4j Documentation Framework Reference

This document describes the shared documentation framework and authoring conventions for the following four plug-ins:

- `org.eclipse.emf.cdo.doc`
- `org.eclipse.net4j.doc`
- `org.eclipse.net4j.util.doc`
- `org.eclipse.net4j.db.doc`

All four plug-ins use the same framework. The established CDO documentation provides mature examples of its intended use.

## Documentation Pipeline

The CDO/Net4j documentation is authored as Java source and Javadoc comments, not as standalone Markdown or HTML:

```text
Java documentation sources -> ArticleDoclet -> EMF article model -> generated documentation
```

The main framework entry point is `ArticleDoclet`. The article model is implemented under `org.eclipse.oomph.releng.doc.article`. Important processing classes include `DocumentationImpl`, `ChapterImpl`, `SectionImpl`, `BodyElementContainerImpl`, `UnresolvedBodyElement`, and `SnippetImpl`.

The model is normally built transiently and generates HTML, navigation, Javadoc links, schema documentation links, and copied resources. Checked-in HTML and related output are generated artifacts, not the authoring source.

### Documentation Generation Is Maintainer-Owned

**The documentation generator is proprietary, not open source, and is not available to automated coding agents. Documentation generation is performed exclusively by the project maintainer.**

The following rules are mandatory for all automated documentation work:

- **Never attempt to execute the documentation generator**, including `ArticleDoclet` or the associated Ant generation targets.
- Do not attempt to install, download, reconstruct, replace, or reverse-engineer the generator.
- Do not modify generated Ant scripts, HTML files, TOC files, or other generated documentation artifacts.
- Do not investigate or attempt to repair the proprietary generation infrastructure.
- Do not request that the maintainer run the generator as a prerequisite for completing a documentation task.
- Restrict modifications to documentation source files and their necessary source-level dependencies.
- Verify Java source compilation using the available Eclipse JDT MCP tools. If those tools are unavailable, stop rather than substituting another build mechanism.
- Review Javadoc structure, links, and snippet definitions statically, following the conventions in this document.

**Successful Java compilation does not establish successful documentation generation.** Automated agents must never claim that generated HTML, navigation, or embedded snippets have been verified unless the maintainer has explicitly supplied those generated results for review.

The maintainer independently handles generation, rendering verification, and publication. Automated documentation tasks are considered complete after the requested source changes and applicable source-level verification, without running the documentation generator.

## Structural Model

- `package-info.java` creates a `Category`.
- A public top-level documentation class creates an `Article`.
- A nested documentation class creates a `Chapter`.
- Every field returned by `ClassDoc.fields()` is collected as a `Section` automatically.
- A method becomes a `Section` only when it has `@section`.
- Fields and selected methods are sorted by source position, so source order determines section order.
- Nested classes are analyzed recursively.
- `@number` sets explicit ordering/numbering. Its value is parsed as a floating-point value, not an integer.
- `@default` marks the overview/default structural element.
- `@excluded` excludes a structural element from generated navigation/output.
- `@ignore` prevents a class or method from being analyzed into the documentation model.
- `@external` creates an external article for an absolute URL or a plug-in resource for a relative path.

The field/method distinction is important: do not add `@section` to fields merely to make them sections; add it to methods that should become sections.

## Documentation-Specific Author Attribution

**Only top-level Article classes must have an `@author` tag.**

The ArticleDoclet renders `@author` as a visible author line on the corresponding documentation element. Adding it to nested Chapter classes causes the author's name to appear repeatedly throughout the generated article.

For documentation sources, follow these rules:

- Add `@author` to public top-level classes representing Articles.
- Do not add `@author` to nested Chapter classes.
- Do not add `@author` to Section fields or methods.
- Do not add `@author` to methods or classes used exclusively as Snippets.
- Do not add `@author` to `package-info.java` files representing Categories.
- Do not propagate the Article's author tag to nested documentation elements.

Example:

```java
/**
 * Migrating the WebSocket Transport
 *
 * @author Eike Stepper
 */
public class Doc01_MigratingWebSocketTransport
{
  /**
   * Migrating WebSocket Clients
   * <p>
   * This chapter explains the client-side migration.
   */
  public class MigratingClients
  {
  }
}
```

The Article has an author; the Chapter does not.

**This is a documentation-specific exception to general Java source conventions.** It applies to the four documentation plug-ins listed above, not to ordinary production Java types.

## Body and Inline Constructs

Standard Javadoc constructs explicitly processed by the framework include:

- `{@link ...}` resolves Java, API, documentation, and embeddable targets.
- `@see` is rendered as a See Also list and resolved through the link machinery.
- `@author` is rendered as an author line, subject to the documentation-specific placement rules above.
- `{@code ...}`, `{@literal ...}`, and `{@value ...}` are parsed as inline body elements.

Framework-specific body constructs include:

- `{@toc}` inserts a table of contents; `{@toc n}` limits the displayed hierarchy levels.
- `{@key A+B}` renders a keyboard shortcut as styled key caps.
- `{@select A | B | C}` renders a selection path with arrows.
- `{@image file}` resolves a relative image and emits an image or SVG embed.
- `{@excel file#sheet}` renders an Excel worksheet as an HTML table.
- `{@diagram file}` creates a diagram model element, but diagram loading and generation are currently disabled.

The link-label modifier `{@link Target @inline}` inlines the target body when the target is a body element instead of producing a hyperlink.

Unknown or otherwise unhandled body tags remain unresolved body elements and may be reported by the doclet or appear literally in generated output. The custom Javadoc parser recognizes many standard tags, but recognition by the parser does not imply dedicated article-framework rendering.

## Snippets and Source Formatting

`@snip` creates an embeddable `Snippet`. The first token selects the formatter; no token defaults to Java:

- `java`
- `xml`
- `tree`
- `image`
- `html`

Other snippet-related tags are:

- `@callout` declares ordered explanatory callouts for a snippet.
- `@title` sets a snippet title.
- `@description` supplies additional body content.
- `@image` on a snippet supplies its title/editor image.
- `@image` on a method creates an `ImageFactory` for that method.

Java snippets use the bundled `CodeSnippet` implementation. It reads the source file containing the documented class or method, starts at the Javadoc element's source position, and extracts Java by following the method/class structure and balancing braces. It then applies Java syntax highlighting.

The verified implementation does not support arbitrary Java source regions, arbitrary line ranges, or selecting an unrelated method from another Java file. Java examples should therefore be real methods or classes in the documentation source and should use conventional, parseable brace structure.

### Java Snippet Method Body Rules

**When a Java method is annotated with `@snip`, the generated snippet displays its method body, not its method declaration.**

In particular, the generated example does not display:

- The method name.
- The declared return type.
- The parameter list and parameter types.
- The method's `throws` clause.
- Other method-level modifiers or annotations.

This distinction is critical: **the Java source may compile successfully while the generated code example is misleading or incomplete.**

#### Mandatory rules

1. **Use `void` for Java methods annotated with `@snip`.** Their purpose is to hold example statements, not to implement value-returning functions.

2. **Do not return values from `@snip` methods.** A statement such as `return connector;` appears in the generated example without the method declaration that would explain its meaning.

3. **Avoid method-level `return` statements altogether**, including bare `return;` statements used for early exit. Write examples as straightforward sequences of statements wherever possible. A return belonging to a fully visible nested lambda or anonymous implementation is a different case, but should still be used only when needed.

4. **Show resulting values through local variables and their use**, rather than returning them from the snippet method.

5. **Make the displayed body understandable on its own.** Prefer declaring and initializing important variables within the displayed body. Avoid unnecessary dependencies on invisible method parameters, instance fields, or surrounding implementation details.

6. If parameters or surrounding context are genuinely necessary, explain those prerequisites in the article text immediately before the snippet. Do not assume readers can see the hidden method signature.

7. Do not rely on an invisible `throws` clause to make the example understandable. Where exception handling matters, demonstrate it explicitly or explain the required handling in the surrounding prose.

8. Keep examples valid Java source. Do not sacrifice compilation correctness for presentation, but do not treat successful compilation as proof that the displayed snippet is correct.

9. Ordinary helper methods may return values when necessary. If a helper is used solely to support an example, it need not itself be annotated with `@snip`. The visible snippet should demonstrate how its returned value is used.

10. When reviewing a Java snippet, **mentally remove its method signature and enclosing method braces**. Review the remaining statements as the reader will see them.

#### Incorrect example: returning a value

```java
/**
 * @snip
 */
public IFactory createProvider()
{
  HttpClient client = HttpClient.newBuilder().build();
  return JDKWSUtil.createMessageConnectionFactory("custom", client, Duration.ofSeconds(10));
}
```

Although the source compiles, the generator removes the method declaration. The reader sees a `return` statement with no visible method return type or surrounding method context.

**Do not use this pattern for documentation snippets.**

#### Correct example: demonstrating statements

```java
/**
 * @snip
 */
public void configureProvider()
{
  IManagedContainer container = ContainerUtil.createInitializedContainer();

  try
  {
    HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    IFactory provider = JDKWSUtil.createMessageConnectionFactory("custom", client, Duration.ofSeconds(10));
    container.registerFactory(provider);
  }
  finally
  {
    LifecycleUtil.deactivate(container);
  }
}
```

The generated snippet consists of meaningful Java statements. It does not rely on a hidden return type, and the relevant variables are visible in the code.

The example method's `void` declaration is an implementation detail of the documentation source, not part of the displayed example.

These rules concern Java **method** snippets. Class-level snippets may have different extraction behavior and should be reviewed according to their actual generated representation.

### XML Snippets and Callouts

XML snippets read a file supplied after `@snip xml`. XML callouts use:

```xml
<!--callout-->
```

Java callouts use `/* callout */`. The number and order of markers must match the `@callout` declarations.

For XML snippets, an empty `void` method acts only as the documentation target and does not contribute Java code to the displayed XML.

### Formatter-Specific Options

- `@style` for the image formatter's generated CSS class.
- `@expandTo` for tree expansion depth.
- `@expanded` for tree paths that should be expanded.
- `@selected` for a tree path.

## Linking and Embedding

The key semantic distinction is:

- Ordinary documentation and API targets produce links.
- Links to embeddable targets, such as snippets, can produce embedded content.

`{@link}` targets can resolve to CDO articles, chapters, sections, Java types and members in local or configured external Javadoc, snippet methods/classes, and package or other registered documentation targets.

Chapter and section targets use generated page anchors. Snippet links commonly use a reader-friendly label as the displayed filename, for example:

```java
{@link #createSession(IConnector, String) CreateSession.java}
```

Plug-in, schema, extension-point, product, and Javadoc documentation are represented by framework model elements and generated resources, generally loaded from project metadata and generated documentation resources rather than from a dedicated inline tag. Relative resources are resolved from the source documentation file or the relevant documentation project.

## Mandatory Snippet Integration

**Declaring a method or class with `@snip` does not automatically include its source code in the generated article.**

A snippet is an embeddable documentation element. To make it visible to readers, it must be referenced from the appropriate article, chapter, or section Javadoc using a `{@link}` targeting that snippet.

Every snippet intended for publication must have such a reference. An unreferenced `@snip` method can remain completely invisible in the generated documentation without causing a Java compilation error.

### Preferred Authoring Pattern

The preferred pattern is demonstrated by:

`org.eclipse.emf.cdo.doc.programmers.client.Doc03_WorkingWithSessions.ViewManagement.OpeningViewsAndTransactions`

The enclosing chapter or section should:

1. Explain the relevant concepts and APIs.
2. Introduce the example with a natural sentence describing what it demonstrates.
3. Embed the snippet using a `{@link}` reference with a meaningful Java filename.
4. Continue the explanation with relevant observations or additional details when useful.

For example:

```java
/**
 * Opening Views and Transactions
 * <p>
 * Views and transactions are created from a session using the
 * various openView() and openTransaction() methods.
 * <p>
 * Here is an example of how to open views and transactions
 * with different configurations:
 * {@link #openingViewsAndTransactions(CDOSession, ResourceSet) OpeningViewsAndTransactions.java}
 * <p>
 * Note that views and transactions are lightweight objects
 * that should be closed when no longer needed.
 */
```

The snippet method is defined separately and annotated with `@snip`:

```java
/**
 * @snip
 */
public void openingViewsAndTransactions(CDOSession session, ResourceSet resourceSet)
{
  // Example implementation...
}
```

The snippet reference belongs in the explanatory Javadoc, not merely in the snippet method's own Javadoc.

**Remember that the method signature shown above is not part of the rendered snippet.** If the example body refers to `session` or `resourceSet`, the surrounding explanation must establish what those variables represent. For new examples, prefer self-contained bodies that introduce their own context where practical.

### Integration Rules

- Every intended `@snip` example must be referenced from an appropriate article, chapter, or section.
- References must resolve to the actual snippet target, including the correct method name, parameter types, and enclosing class where applicable.
- Use a meaningful `.java` filename as the link label for Java examples.
- Prefer placing the reference on its own Javadoc line, immediately after the sentence introducing the example.
- Integrate examples into the natural explanatory flow rather than collecting unrelated snippets at the end of a chapter.
- Introduce snippets with concise descriptions of what they demonstrate.
- Continue the discussion after a snippet when additional explanation is useful.
- Avoid repeating information already evident from the example.
- Normally embed each example once, unless repeated use serves a specific documentation purpose.
- Do not create `@snip` methods without considering where and how their content will appear in the generated article.
- For Java method snippets, review the extracted method body rather than the complete method declaration.
- Never use a value-returning Java `@snip` method to display an operation's result.
- When adding or revising documentation, account for all intended snippets in the affected articles, including snippets in nested classes.

The guiding principle is that **snippets support the explanatory text, not the other way around**. Their placement should follow the logical progression of the article.

## Images and Other Resources

- Normal figures use direct relative `{@image file}` references.
- SVG images are emitted with `<embed>`; other image types use `<img>`.
- XML, tree, image, and HTML snippets are file-backed according to their formatter.
- Excel snippets use the bundled Excel table renderer and support workbook values, formulas, merged cells, and basic formatting.
- `@external` supports external URLs and relative plug-in resources.
- `@diagram` exists in the model/parser but is effectively nonfunctional in the current implementation. Do not use it unless the framework is repaired and verified.

## Established CDO/Net4j Authoring Conventions

The CDO Sessions and Views chapters are the primary mature examples.

- Nested classes define the documentation structure.
- Prose belongs in Javadoc on the corresponding class, field, or method.
- Use `@author` only on top-level Article classes, never on nested Chapter classes or subordinate documentation elements.
- Use `{@toc}` for substantial articles.
- Prefer semantic `{@link ...}` references over hard-coded generated HTML links.
- Use real `@snip` methods for Java examples.
- Write Java `@snip` methods as `void` methods whose displayed bodies require no method-level return statements.
- Ensure that the rendered snippet remains understandable without its method signature.
- Embed every intended snippet through a suitable `{@link}` reference with a reader-friendly display filename.
- Introduce snippets naturally and place them where they best support the explanation.
- Use `@callout` only when source markers and explanatory text are deliberately maintained together.
- Keep source order aligned with documentation order.
- Use direct relative `{@image ...}` references for normal figures.
- Treat framework capabilities that are unused in CDO/Net4j as available capabilities, not automatically as new authoring conventions.

## Known Unsupported or Discouraged Constructs

- `@category` is not a supported authoring tag; categories come from documented packages.
- `@diagram` is currently nonfunctional.
- Arbitrary Java snippet regions, line ranges, and unrelated-method selection are unsupported.
- Value-returning Java `@snip` methods are prohibited by the documentation authoring conventions.
- Java snippets that rely on their hidden method signatures for understanding are discouraged.
- `@author` on nested Chapters or other subordinate documentation elements is prohibited.
- Specialized formatter features should not be introduced without a concrete need.
- Framework capabilities that are unused or rarely used in CDO/Net4j should not automatically become new conventions.

## Failure Modes

Future documentation changes must account for:

- Unresolved Java, documentation, or snippet targets.
- Missing relative files or images.
- Mismatched callout markers and `@callout` descriptions.
- Invalid or missing snippet targets.
- Unreferenced `@snip` methods that do not appear in the generated articles.
- Java snippets containing method-level `return` statements whose return types are absent from the generated output.
- Java snippets that depend on invisible parameter declarations, return types, or `throws` clauses to be understood.
- Duplicate author lines caused by `@author` tags on nested Chapter classes.
- Source-sensitive Java snippet extraction.
- Malformed numeric values for tags such as `@number` or `@toc` levels.
- Unsupported nested embeddings.
- Generated-document failures that are invisible to Java compilation.
- Hard-coded absolute paths, which are not portable and do not follow the resource-resolution model.

In particular, successful JDT compilation verifies the Java source, **not the completeness or readability of the extracted snippet body**.

Content authors should check snippet source against the documented body-only extraction rules. Final generated rendering must be inspected separately through the established documentation-generation workflow.

## Authoring Rules

1. Use public top-level classes for Articles and nested classes for Chapters.
2. **Place `@author` only on top-level Article classes.** Do not place it on Categories, nested Chapters, Sections, or Snippets.
3. Remember that fields become Sections automatically; add `@section` to methods that should become Sections.
4. Keep documentation prose in Javadoc and preserve source order.
5. Use `@snip` on real, parseable Java methods or classes.
6. **Declare Java `@snip` methods as `void` and do not return values from them.** Prefer bodies without method-level `return` statements of any kind.
7. **Design Java `@snip` method bodies for display without their signatures.** Introduce important variables in the visible body, avoid hidden assumptions, and explain unavoidable context.
8. Show results through visible local variables and subsequent usage rather than through the Snippet method's return value.
9. Every intended snippet must be embedded through a valid `{@link}` reference in the appropriate explanatory Javadoc.
10. Introduce snippets naturally, use reader-friendly filenames, and position examples where they best support the text.
11. Use only the verified formatter names: `java`, `xml`, `tree`, `image`, and `html`.
12. Keep callout declarations and source markers exactly matched.
13. Use relative resource paths; never add machine-specific absolute paths.
14. Prefer semantic API/documentation links and direct relative image references.
15. Do not introduce `@category`, `@diagram`, arbitrary Java regions, or specialized formatter options without verified support and a concrete documentation need.
16. **Do not equate successful Java compilation with successful documentation rendering.** Review the extracted snippet body and its explicit embedding separately.
