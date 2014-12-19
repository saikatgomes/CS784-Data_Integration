
all: report.pdf
	@true

.PHONY: report.pdf
report.pdf: report.tex refs.bib
	@pdflatex report.tex
	@bibtex report.aux
	@pdflatex report.tex
	@pdflatex report.tex

clean:
	rm -f *.aux *.log *.pdf *.blg *.bbl
