# CDO Documentation Framework Reference

## Documentation Pipeline

The CDO documentation is authored as Java source and Javadoc comments, not as standalone Markdown or HTML:

```text
Java documentation sources -> ArticleDoclet -> EMF article model -> generated documentation
```

The main framework entry point is `ArticleDoclet`. The article model is implemented under `org.eclipse.oomph.releng.doc.article`. Important processing classes include `DocumentationImpl`, `ChapterImpl`, `SectionImpl`, `BodyElementContainerImpl`, `UnresolvedBodyElement`, and `SnippetImpl`.

The model is normally built transiently and generates HTML, navigation, Javadoc links, schema documentation links, and copied resources. Checked-in HTML and related output are generated artifacts, not the authoring source.

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

## Body and Inline Constructs

Standard Javadoc constructs explicitly processed by the framework include:

- `{@link ...}` resolves Java, API, documentation, and embeddable targets.
- `@see` is rendered as a See Also list and resolved through the link machinery.
- `@author` is rendered as an author line.
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
- `@description` supplies additional body content for a snippet.
- `@image` on a snippet supplies its title/editor image.
- `@image` on a method creates an `ImageFactory` for that method.

Java snippets use the bundled `CodeSnippet` implementation. It reads the source file containing the documented class or method, starts at the Javadoc element's source position, and extracts Java by following the method/class structure and balancing braces. It then applies Java syntax highlighting.

The verified implementation does not support arbitrary Java source regions, arbitrary line ranges, or selecting an unrelated method from another Java file. Java examples should therefore be real methods or classes in the documentation source and should use conventional, parseable brace structure.

XML snippets read a file supplied after `@snip xml`. XML callouts use:

```xml
<!--callout-->
```

Java callouts use `/* callout */`. The number and order of markers must match the `@callout` declarations.

Formatter-specific options are:

- `@style` for the image formatter's generated CSS class.
- `@expandTo` for tree expansion depth.
- `@expanded` for tree paths that should be expanded.
- `@selected` for the selected tree path.

## Linking and Embedding

The key semantic distinction is:

- ordinary documentation and API targets produce links;
- links to embeddable targets, such as snippets, can produce embedded content.

`{@link}` targets can resolve to CDO articles, chapters, sections, Java types and members in local or configured external Javadoc, snippet methods/classes, and package or other registered documentation targets.

Chapter and section targets use generated page anchors. Snippet links commonly use a reader-friendly label as the displayed filename, for example:

```java
{@link #createSession(IConnector, String) CreateSession.java}
```

Plug-in, schema, extension-point, product, and Javadoc documentation are represented by framework model elements and generated resources, generally loaded from project metadata and generated documentation resources rather than from a dedicated inline tag. Relative resources are resolved from the source documentation file or the relevant documentation project.

## Images and Other Resources

- Normal figures use direct relative `{@image file}` references.
- SVG images are emitted with `<embed>`; other image types use `<img>`.
- XML, tree, image, and HTML snippets are file-backed according to their formatter.
- Excel snippets use the bundled Excel table renderer and support workbook values, formulas, merged cells, and basic formatting.
- `@external` supports external URLs and relative plug-in resources.
- `@diagram` exists in the model/parser but is effectively nonfunctional in the current implementation. Do not use it unless the framework is repaired and verified.

## Established CDO Authoring Conventions

The Sessions and Views chapters are the primary mature examples.

- Nested classes define the documentation structure.
- Prose belongs in Javadoc on the corresponding class, field, or method.
- Use `{@toc}` for substantial articles.
- Prefer semantic `{@link ...}` references over hard-coded generated HTML links.
- Use real `@snip` methods for Java examples.
- Embed snippets through links with reader-friendly display filenames.
- Use `@callout` only when source markers and explanatory text are deliberately maintained together.
- Keep source order aligned with documentation order.
- Use direct relative `{@image ...}` references for normal figures.
- Treat framework capabilities that are unused in CDO as available capabilities, not automatically as new CDO conventions.

## Known Unsupported or Discouraged Constructs

- `@category` is not a supported authoring tag; categories come from documented packages.
- `@diagram` is currently nonfunctional.
- Arbitrary Java snippet regions, line ranges, and unrelated-method selection are unsupported.
- Specialized formatter features should not be introduced without a concrete need.
- Framework capabilities that are unused or rarely used in CDO should not automatically become new conventions.

## Failure Modes

Future documentation changes must account for:

- unresolved Java, documentation, or snippet targets;
- missing relative files or images;
- mismatched callout markers and `@callout` descriptions;
- invalid or missing snippet targets;
- source-sensitive Java snippet extraction;
- malformed numeric values for tags such as `@number` or `@toc` levels;
- unsupported nested embeddings;
- generated-document failures that are invisible to Java compilation;
- hard-coded absolute paths, which are not portable and do not follow the resource-resolution model.

Generated HTML and unresolved-link diagnostics must be validated in addition to compiling the Java sources.

## Authoring Rules

1. Use public top-level classes for articles and nested classes for chapters.
2. Remember that fields become sections automatically; add `@section` to methods that should become sections.
3. Keep documentation prose in Javadoc and preserve source order.
4. Use `@snip` on real, parseable Java methods or classes.
5. Embed snippets with semantic `{@link}` references and reader-friendly labels.
6. Use only the verified formatter names: `java`, `xml`, `tree`, `image`, and `html`.
7. Keep callout declarations and source markers exactly matched.
8. Use relative resource paths; never add machine-specific absolute paths.
9. Prefer semantic API/documentation links and direct relative image references.
10. Do not introduce `@category`, `@diagram`, arbitrary Java regions, or specialized formatter options without verified support and a concrete documentation need.
11. Validate generated documentation and unresolved-target diagnostics, not only Java compilation.
