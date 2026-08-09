# Graph Report - /Users/brian/Projects/Masking  (2026-08-01)

## Corpus Check
- Corpus is ~13,939 words - fits in a single context window. You may not need a graph.

## Summary
- 311 nodes · 661 edges · 22 communities (18 shown, 4 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 68 edges (avg confidence: 0.81)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Renderer Implementation
- Sensitive Core Type
- EIN Test Suite
- SSN Test Suite
- Project Metadata & Tools
- Segmented Array Type
- Renderer Interface
- Invalid TIN Exception
- Masking & Truncation
- Module Architecture
- Invalid TIN Tests
- Build Wrapper Script
- Documentation Rendering
- JPMS Specification

## God Nodes (most connected - your core abstractions)
1. `EINTest` - 18 edges
2. `SSNTest` - 18 edges
3. `Renderer` - 16 edges
4. `Sensitive` - 16 edges
5. `Delimit` - 16 edges
6. `Formattable` - 15 edges
7. `Formattable` - 15 edges
8. `RenderersTest` - 13 edges
9. `Gradle Build System` - 13 edges
10. `Segmented` - 12 edges

## Surprising Connections (you probably didn't know these)
- `Claude Code Project Guide` --references--> `GitHub Pages Deployment Workflow`  [INFERRED]
  CLAUDE.md → .github/workflows/pages.yml
- `Contributor Covenant Code of Conduct` --references--> `GitHub Pages Deployment Workflow`  [INFERRED]
  CODE_OF_CONDUCT.md → .github/workflows/pages.yml
- `Sensitive Data Masking README` --references--> `GitHub Pages Deployment Workflow`  [INFERRED]
  README.md → .github/workflows/pages.yml
- `UsTIN` --inherits--> `Segmented`  [EXTRACTED]
  tin/src/main/java/com/maybeitssquid/tin/us/UsTIN.java → sensitive/src/main/java/com/maybeitssquid/sensitive/Segmented.java
- `Dependabot Configuration` --configures--> `Gradle Build System`  [EXTRACTED]
  .github/dependabot.yml → CLAUDE.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **GitHub Actions CI Testing Matrix** — github_workflows_gradle, java_17, java_21, java_25 [EXTRACTED 1.00]
- **Sensitive Data Container Hierarchy** — sensitive_t, segmented_t, us_tin, ssn, ein [EXTRACTED 1.00]
- **GitHub Pages Site Content** — github_workflows_pages, readme, code_of_conduct, claude, github_pandoc_mermaid_init [EXTRACTED 1.00]

## Communities (22 total, 4 thin omitted)

### Community 0 - "Renderer Implementation"
Cohesion: 0.10
Nodes (13): IntPredicate, Concatenate, Constants, Delimit, Nested, Test, MaskedDefault, MaskedWithCustomChar (+5 more)

### Community 1 - "Sensitive Core Type"
Cohesion: 0.11
Nodes (11): Formattable, Formatter, DoNotSerialize, Override, Sensitive, DoNotSerializeInterface, Nested, SuppressWarnings (+3 more)

### Community 2 - "EIN Test Suite"
Cohesion: 0.13
Nodes (3): EINTest, Formattable, Test

### Community 3 - "SSN Test Suite"
Cohesion: 0.13
Nodes (3): Formattable, Test, SSNTest

### Community 4 - "Project Metadata & Tools"
Cohesion: 0.09
Nodes (28): AssertJ Fluent Assertions, Claude Code Project Guide, Contributor Covenant Code of Conduct, GitHub Actions CI/CD Platform, Dependabot Configuration, GitHub Pages Deployment, Mermaid Initialization Script, Gradle CI Workflow (+20 more)

### Community 5 - "Segmented Array Type"
Cohesion: 0.16
Nodes (9): Override, Segmented, Construction, Exposed, GetValue, Nested, SuppressWarnings, Test (+1 more)

### Community 6 - "Renderer Interface"
Cohesion: 0.12
Nodes (9): FunctionalInterface, SuppressWarnings, Renderer, Renderers, Test, RendererTest, NationalTIN, Override (+1 more)

### Community 7 - "Invalid TIN Exception"
Cohesion: 0.16
Nodes (7): InvalidTINException, EIN, Pattern, Pattern, SSN, Nested, Nested

### Community 9 - "Module Architecture"
Cohesion: 0.21
Nodes (14): com.maybeitssquid:sensitive Artifact, com.maybeitssquid:tin Artifact, DoNotSerialize Inner Class, Employer Identification Number (EIN) Implementation, Format String Integration via Formattable, Stateless Immutable Renderer Pattern, Renderer<T> Functional Interface, Renderers Factory (+6 more)

### Community 11 - "Build Wrapper Script"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **20 isolated node(s):** `Dependabot Configuration`, `Mermaid Initialization Script`, `Claude Code Project Guide`, `Contributor Covenant Code of Conduct`, `Sensitive Data Masking README` (+15 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Sensitive` connect `Sensitive Core Type` to `Segmented Array Type`?**
  _High betweenness centrality (0.274) - this node is a cross-community bridge._
- **Why does `Renderer` connect `Renderer Interface` to `Renderer Implementation`, `Masking & Truncation`, `Segmented Array Type`, `Sensitive Core Type`?**
  _High betweenness centrality (0.141) - this node is a cross-community bridge._
- **Why does `Formattable` connect `SSN Test Suite` to `Sensitive Core Type`, `Invalid TIN Exception`?**
  _High betweenness centrality (0.129) - this node is a cross-community bridge._
- **What connects `Dependabot Configuration`, `Mermaid Initialization Script`, `Claude Code Project Guide` to the rest of the system?**
  _20 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Renderer Implementation` be split into smaller, more focused modules?**
  _Cohesion score 0.1006993006993007 - nodes in this community are weakly interconnected._
- **Should `Sensitive Core Type` be split into smaller, more focused modules?**
  _Cohesion score 0.1092436974789916 - nodes in this community are weakly interconnected._
- **Should `EIN Test Suite` be split into smaller, more focused modules?**
  _Cohesion score 0.12643678160919541 - nodes in this community are weakly interconnected._