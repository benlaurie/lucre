all: theory2.dvi

pdf: theory2.pdf

.SUFFIXES: .dvi .tex .pdf

.tex.dvi:
	latex $<
	latex $<

.tex.pdf:
	pdflatex $<

web: theory2.pdf
	cp theory2.pdf ../html
