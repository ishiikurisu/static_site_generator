# Static Site Generator

My personal static site generator. It is an evolution of
[Liberdade's Github Local Blog](https://github.com/liberdade-organizacao/github-local-blog).


## Build

```
make publish
```

This will generate a `ssg.jar` jarfile that can be executed.


## Usage

### Tools

The following tools are available:

- `wiki`
- `rss`
- `microblog`
- `help`

They should accept the following parameters:

- `-i`: input repository
- `-u`: reference URL
- `-t`: folder with template files
- `-o`: output path

### Example Templates

Basic `index.template.html`:

```
<html>
<head>
</head>
<body>
  <ul>
    %{content}
  </ul>
</body>
</html>
```

Basic `index.post.template.html`:

```
<li>
  <a href="./%{path}">
    %{title}
  </a>
</li>

```

Basic `post.template.html`:

```
<html>
<head></head>
<body>
  %{content}
</body>
</html>

```

Currently, the static site generator can convert the following file types into
HTML to be used as content inside the post template:

- Markdown
- Gemini
- CSV

### Index file format

The index is a JSON file containing a list of entry objects, each entry
indicating:

- `title`
- `description`
- `path`
  - During rendering, this path will be the path to the generated HTML file

Example `index.blog.json`:

```
[
  {
    "title": "Entry #1",
    "description": "First to appear on the list",
    "path": "zzz.html"
  },
  {
    "title": "Entry #2",
    "description": "Second entry to appear, will be converted to yyy.html",
    "path": "yyy.md"
  }
]
```

Currently, all files described in the index should be in the same
directory.

