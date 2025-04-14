# Static Site Generator

My personal static site generator. It is an evolution of
[Liberdade's Github Local Blog](https://github.com/liberdade-organizacao/github-local-blog).



## Build

```
make
```

This will generate a `build_notes.exe` executable.



## Usage

Command line options:

```
./build_notes.exe <input_folder> <output_folder>
```

During usage, the application expects a `template` folder in the same
directory of usage containing the template files.

The input folder should contain an `index.blog.json` file listing all files
that should be included in the site.

The output folder will contain:

- The generated files, including an index one
- An RSS feed file listing files based on date of last updated, limited to
  the last 20 updates


### Template file format

The static site generator expects 3 template files:

- `index.template.html`
  - Main page of the static site
  - The `content` handlebar will be filled using the contents of 
    `index.post.template.html` using the data listed on `index.blog.json`
- `index.post.template.html`
  - Entry for each post, as listed on the index page
  - To be filled with information from `index.blog.json`
- `post.template.html`
  - Page for each post on the static site
  - One page for each entry on `index.blog.json`

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

