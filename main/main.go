package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"os"
	"sort"
	"strings"
	"time"

	"git.sr.ht/~m15o/gmi2html"
	"github.com/gomarkdown/markdown"
	"github.com/gomarkdown/markdown/html"
	"github.com/gomarkdown/markdown/parser"
)

/* #################
   # MAIN FUNCTION #
   ################# */

type Note struct {
	Title           string `json:"title"`
	Description     string `json:"description"`
	Language        string `json:"language"`
	Path            string `json:"path"`
	CreationDate    string `json:"creation_date"`
	LastUpdatedDate string `json:"last_updated_date"`
	OriginalDate    string `json:"original_date"`
}

type RenderedNote struct {
	Path     string
	Contents string
}

func main() {
	// parsing args
	inputFolder := os.Args[1]
	outputFolder := os.Args[2]
	noteIndex, err := getNotes(inputFolder)
	if err != nil {
		panic(err)
	}

	// preparing data for rendering
	newLinks := generateNewLinks(noteIndex)

	// rendering notes
	var notesToIndex []Note
	noteTemplate := getTemplate("note.template.html")
	renderedNotes := make(map[string]*RenderedNote)
	for _, note := range noteIndex {
		renderedNote, err := renderNote(
			inputFolder,
			outputFolder,
			note,
			noteTemplate,
			newLinks,
		)
		if err != nil {
			fmt.Printf("error for '%s': %s\n", note.Path, err)
		} else {
			notesToIndex = append(notesToIndex, note)
			renderedNotes[note.Path] = renderedNote
		}
	}

	// rendering index
	indexTemplate := getTemplate("index.template.html")
	indexNoteTemplate := getTemplate("index.post.template.html")
	err = renderIndex(outputFolder, notesToIndex, indexTemplate, indexNoteTemplate)
	if err != nil {
		panic(err)
	}

	// rendering rss feed
	notesToFeed := take(selectRssFeedNotes(notesToIndex), 20)
	renderRssFeed(outputFolder, notesToFeed, renderedNotes)
}

/* ##########################
   # LINK REDIRECTION LOGIC #
   ########################## */

func generateNewLinks(noteIndex []Note) map[string]string {
	template := "href=\"./%s\""
	newLinks := make(map[string]string)

	for _, note := range noteIndex {
		fromLink := fmt.Sprintf(template, note.Path)
		toLink := fmt.Sprintf(template, filePathToHtml(note.Path))
		newLinks[fromLink] = toLink
	}

	return newLinks
}

// inlet should be the generated HTML string
func replaceInternalLinks(inlet string, newLinks map[string]string) string {
	outlet := inlet

	for fromLink, toLink := range newLinks {
		outlet = strings.ReplaceAll(outlet, fromLink, toLink)
	}

	return outlet
}

/* ##########################
   # NOTES GENERATION LOGIC #
   ########################## */

func getTemplate(filename string) string {
	filepath := fmt.Sprintf("./template/%s", filename)
	bytes, err := ioutil.ReadFile(filepath)
	if err != nil {
		return ""
	}
	return string(bytes)
}

func readNote(inputFolder, filename string) ([]byte, error) {
	filepath := fmt.Sprintf("./%s/%s", inputFolder, filename)
	bytes, err := ioutil.ReadFile(filepath)
	if err != nil {
		return nil, err
	}
	return bytes, nil
}

func getNotes(inputFolder string) ([]Note, error) {
	filepath := fmt.Sprintf("./%s/index.blog.json", inputFolder)
	bytes, err := ioutil.ReadFile(filepath)
	if err != nil {
		return nil, err
	}

	var noteIndex []Note
	err = json.Unmarshal(bytes, &noteIndex)
	if err != nil {
		return nil, err
	}

	return noteIndex, nil
}

func createFolderForFile(filepath string) error {
	parts := strings.Split(filepath, "/")
	usableParts := parts[0 : len(parts)-1]
	dirpath := strings.Join(usableParts, "/")
	return os.MkdirAll(dirpath, os.ModePerm)
}

func renderNote(
	inputFolder string,
	outputFolder string,
	note Note,
	noteTemplate string,
	newLinks map[string]string,
) (*RenderedNote, error) {
	// loading note data
	bodyBytes, err := readNote(inputFolder, note.Path)
	if err != nil {
		return nil, err
	}

	// prerendering note's contents
	htmlPath := filePathToHtml(note.Path)
	filePath := fmt.Sprintf("./%s/%s", outputFolder, htmlPath)
	var contents string
	if strings.HasSuffix(note.Path, ".md") {
		contents = string(renderMarkdown(bodyBytes))
	} else if strings.HasSuffix(note.Path, ".gmi") {
		contents = renderGemini(string(bodyBytes))
	} else if strings.HasSuffix(note.Path, ".csv") {
		contents = renderCsv(string(bodyBytes))
	} else {
		contents = string(bodyBytes)
	}

	// creating required directories if needed
	err = createFolderForFile(filePath)
	if err != nil {
		return nil, err
	}

	// writing output to file
	output := strings.ReplaceAll(noteTemplate, "{{content}}", contents)
	fp, err := os.Create(filePath)
	if err != nil {
		return nil, err
	}
	defer fp.Close()

	_, err = fp.WriteString(replaceInternalLinks(
		output,
		newLinks,
	))
	if err != nil {
		return nil, err
	}

	// generating summary of results
	renderedNote := RenderedNote{
		Path:     htmlPath,
		Contents: contents,
	}

	return &renderedNote, nil
}

func filePathToHtml(path string) string {
	// corner cases
	if strings.HasSuffix(path, "map_data.json") {
		return fmt.Sprintf("/util/map?mapdataurl=/maps/map_data/%s", path)
	}
	if strings.HasSuffix(path, "map_data.geojson") {
		return fmt.Sprintf("/util/map?mapdataurl=/maps/map_data/%s", path)
	}

	// regular cases
	possibleSuffixes := []string{".md", ".gmi", ".csv"}

	for _, suffix := range possibleSuffixes {
		if strings.HasSuffix(path, suffix) {
			return strings.ReplaceAll(path, suffix, ".html")
		}
	}

	return path
}

// https://github.com/gomarkdown/markdown
func renderMarkdown(md []byte) []byte {
	// create markdown parser with extensions
	extensions := parser.CommonExtensions | parser.AutoHeadingIDs | parser.NoEmptyLineBeforeBlock
	p := parser.NewWithExtensions(extensions)
	doc := p.Parse(md)

	// create HTML renderer with extensions
	htmlFlags := html.CommonFlags | html.HrefTargetBlank
	opts := html.RendererOptions{Flags: htmlFlags}
	renderer := html.NewRenderer(opts)

	return markdown.Render(doc, renderer)
}

func renderGemini(gmi string) string {
	return gmi2html.Convert(gmi)
}

func renderCsv(rawCsv string) string {
	// TODO properly render CSV file
	var outlet strings.Builder
	isHeader := true
	lines := strings.Split(rawCsv, "\n")

	outlet.WriteString("<table>")

	for _, line := range lines {
		fields := strings.Split(line, ",")
		cellOpen := "<td>"
		cellClose := "</td>"

		if isHeader {
			cellOpen = "<th>"
			cellClose = "</th>"
			isHeader = false
		}

		outlet.WriteString("<tr>")
		for _, field := range fields {
			outlet.WriteString(cellOpen)
			outlet.WriteString(field)
			outlet.WriteString(cellClose)
		}
		outlet.WriteString("</tr>")
	}

	outlet.WriteString("</table>")

	return outlet.String()
}

func renderIndex(outputFolder string, notes []Note, indexTemplate, indexNoteTemplate string) error {
	filepath := fmt.Sprintf("./%s/index.html", outputFolder)
	fp, err := os.Create(filepath)
	if err != nil {
		return err
	}
	defer fp.Close()

	var noteIndexHtmlBuilder strings.Builder
	for _, note := range notes {
		noteIndexHtml := strings.ReplaceAll(indexNoteTemplate, "{{path}}", filePathToHtml(note.Path))
		noteIndexHtml = strings.ReplaceAll(noteIndexHtml, "{{description}}", note.Description)
		noteIndexHtml = strings.ReplaceAll(noteIndexHtml, "{{title}}", note.Title)
		noteIndexHtml = strings.ReplaceAll(noteIndexHtml, "{{dateLabel}}", buildDateLabel(note))
		noteIndexHtml = strings.ReplaceAll(noteIndexHtml, "{{language}}", buildLanguageLabel(note))
		noteIndexHtmlBuilder.WriteString(noteIndexHtml)
	}

	indexHtml := strings.ReplaceAll(indexTemplate, "{{content}}", noteIndexHtmlBuilder.String())
	_, err = fp.WriteString(indexHtml)
	if err != nil {
		return err
	}

	return nil
}

func buildDateLabel(note Note) string {
	var dateLabelBuilder strings.Builder
	var fromDate string = note.OriginalDate

	if fromDate == "" {
		fromDate = note.CreationDate
	}

	dateLabelBuilder.WriteString("<span> 🆕 ")
	dateLabelBuilder.WriteString(formatDate(fromDate))
	if fromDate != note.LastUpdatedDate {
		dateLabelBuilder.WriteString(" ➕ ")
		dateLabelBuilder.WriteString(formatDate(note.LastUpdatedDate))
	}
	dateLabelBuilder.WriteString("</span>")

	return dateLabelBuilder.String()
}

func buildLanguageLabel(note Note) string {
	flagEmoji := ""

	switch note.Language {
	case "en":
		flagEmoji = "🇬🇧"
	case "pt-br":
		flagEmoji = "🇧🇷"
	case "ja":
		flagEmoji = "🇯🇵"
	case "de":
		flagEmoji = "🇩🇪"
	}

	if flagEmoji == "" {
		return ""
	}

	return fmt.Sprintf("<span>%s</span>", flagEmoji)
}

/* ####################################
   # LOGIC TO SORT NOTES FOR RSS FEED #
   #################################### */

type By func(a, b *Note) bool

type noteSorter struct {
	notes []Note
	by    By
}

func (sorter *noteSorter) Len() int {
	return len(sorter.notes)
}

func (sorter *noteSorter) Swap(i, j int) {
	sorter.notes[i], sorter.notes[j] = sorter.notes[j], sorter.notes[i]
}

func (sorter *noteSorter) Less(i, j int) bool {
	return sorter.by(&sorter.notes[i], &sorter.notes[j])
}

func (by By) Sort(notes []Note) {
	sorter := &noteSorter{
		notes: notes,
		by:    by,
	}
	sort.Sort(sorter)
}

func selectRssFeedNotes(notes []Note) []Note {
	lastUpdatedDate := func(a, b *Note) bool {
		aLastUpdatedDate := a.LastUpdatedDate
		bLastUpdatedDate := b.LastUpdatedDate

		if aLastUpdatedDate == "" {
			aLastUpdatedDate = a.CreationDate
		}
		if bLastUpdatedDate == "" {
			bLastUpdatedDate = b.CreationDate
		}

		// doing this less comparison on purpose so
		// the RSS feed is generated with more recent
		// notes appearing first
		return aLastUpdatedDate > bLastUpdatedDate
	}

	By(lastUpdatedDate).Sort(notes)
	return notes
}

func take(inlet []Note, howMany int) []Note {
	limit := len(inlet)
	if howMany < limit {
		limit = howMany
	}

	outlet := make([]Note, limit)
	for i := 0; i < limit; i++ {
		outlet[i] = inlet[i]
	}

	return outlet
}

/* #############################
   # RSS FEED GENERATION LOGIC #
   ############################# */

func formatDate(rawDate string) string {
	dt, _ := time.Parse("2006-01-02T15:04:05Z07:00", rawDate)
	return dt.Format("2006-01-02")
}

func adaptNoteForRssFeed(inlet string) string {
	// TODO make this more generic?
	outlet := strings.ReplaceAll(
		inlet,
		"src=\"./",
		"src=\"http://www.crisjr.eng.br/notes/",
	)
	outlet = strings.ReplaceAll(outlet, "[", "%#91;")
	outlet = strings.ReplaceAll(outlet, "]", "%#93;")
	outlet = fmt.Sprintf("<![CDATA[%s]]>", outlet)

	return outlet
}

func renderRssFeed(
	outputFolder string,
	notes []Note,
	renderedNotes map[string]*RenderedNote,
) error {
	filepath := fmt.Sprintf("./%s/feed.rss", outputFolder)
	fp, err := os.Create(filepath)
	if err != nil {
		return err
	}
	defer fp.Close()

	var builder strings.Builder
	now := time.Now().String()

	builder.WriteString(`<?xml version="1.0" encoding="UTF-8" ?>`)
	builder.WriteString(`<rss version="2.0">`)
	builder.WriteString(`<channel>`)
	builder.WriteString(`<title>Cris Silva Jr.'s Notes</title>`)
	builder.WriteString(`<link>https://www.crisjr.eng.br/notes</link>`)
	builder.WriteString(`<description>Notes from my personal digital garden</description>`)
	builder.WriteString(`<author>Cris Silva Jr.</author>`)
	builder.WriteString(fmt.Sprintf(`<lastBuildDate>%s</lastBuildDate>`, now))
	for _, note := range notes {
		noteContents := renderedNotes[note.Path].Contents

		title := fmt.Sprintf(`<title>%s</title>`, note.Title)
		link := fmt.Sprintf(`<link>https://www.crisjr.eng.br/notes/%s</link>`, renderedNotes[note.Path].Path)
		description := fmt.Sprintf(`<description>%s</description>`, adaptNoteForRssFeed(noteContents))
		pubDate := fmt.Sprintf(`<pubDate>%s</pubDate>`, note.LastUpdatedDate)

		builder.WriteString(`<item>`)
		builder.WriteString(title)
		builder.WriteString(link)
		builder.WriteString(description)
		builder.WriteString(pubDate)
		builder.WriteString(`</item>`)
	}
	builder.WriteString(`</channel>`)
	builder.WriteString(`</rss>`)

	_, err = fp.WriteString(builder.String())
	if err != nil {
		return err
	}

	return nil
}
