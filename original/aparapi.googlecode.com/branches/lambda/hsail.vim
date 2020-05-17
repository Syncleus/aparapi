" Vim syntax file
" Language:	HSAIL 
" Maintainer:	Gary Frost <frost.gary [at] gmail.com.nl>
" Last Revision:	2013 Aug 25
"
" This is incomplete. Feel free to contribute...
"
" Don't forget to add following to %VIM_HOME%/vim73/filetype.vim 
" HSAIL (.hsail and .hsa)
" au BufNewFile,BufRead *.hsail,*.hsa  setf hsail
"
" http://vim.wikia.com/wiki/Creating_your_own_syntax_files
" http://www.openlogic.com/wazi/bid/188101/Create-Your-Own-Syntax-Highlighting-in-Vim
" http://www.ibm.com/developerworks/library/l-vim-script-1/
" http://www.ibm.com/developerworks/library/l-vim-script-2/
" http://www.ibm.com/developerworks/library/l-vim-script-3/
" http://www.ibm.com/developerworks/library/l-vim-script-4/
" http://www.ibm.com/developerworks/library/l-vim-script-5/
" Quit when a syntax file was already loaded
if exists("b:current_syntax")
  finish
endif

syn case ignore

" Partial list of register symbols
syn match hsailReg  "\$[cds][0-9]*"
syn match hsailReg "%_arg[0-9]*"
syn match hsailReg "%spillseg"
syn match hsailReg "%_this"

syn match hsailStackLinePc "@[0-9]+" 
syn keyword hsailColon : 
syn region hsailStackTrace start="={" end="}" fold transparent contains=hsailStackLinePc,hsailColon

" All matches - order is important!
syn match hsailOpcode "cmov_[fbusd]\(64\|32\|16\|8\)"
syn match hsailOpcode "mov_[fbusd]\(64\|32\|16\|8\)"
syn match hsailOpcode "kernarg_[fbusd]\(64\|32\|16\|8\)"
syn match hsailOpcode "ld_kernarg_[fbusd]\(64\|32\|16\|8\)"
syn match hsailOpcode "\(ld\|st\)_\(global\|local\|spill\)_[fbusd]\(64\|32\|16\|8\)"
syn match hsailOpcode "\(add\|sub\|rem\|div\|mul\|mad\)_[fbusd]\(32\|64\|16\|8\)"
syn match hsailOpcode "cvt_[fbusd]\(64\|32\|16\|8\)_[fbusd]\(64\|32\|16\|8\)"
syn match hsailOpcode "workitemabsid_[busd]32"
syn match hsailOpcode "\(cbr\|ret\|brn\)"
syn match hsailOpcode "cmp_\(ne\|geu\|gt\|ge\|leu\|le\|lt\|eq\)_b1_[fbusd]\(64\|32\|16\|8\)"

" Various number formats
syn match hsaildecNumber    "[+-]\=[0-9]\+\>"
syn match hsaildecNumber    "^d[0-9]\+\>"
syn match hsailhexNumber    "^x[0-9a-f]\+\>"
syn match hsailoctNumber    "^o[0-7]\+\>"
syn match hsailbinNumber    "^b[01]\+\>"
syn match hsailfloatNumber  "[-+]\=[0-9]\+E[-+]\=[0-9]\+"
syn match hsailfloatNumber  "[-+]\=[0-9]\+\.[0-9]*\(E[-+]\=[0-9]\+\)\="

" Valid labels
syn match hsailLabel        "@[a-z_$.][a-z0-9_]*:"
syn match hsailLabel        "@[a-z_$.][a-z0-9_]*"

syn match hsailkeyword       "\]"
syn match hsailkeyword       "\["
syn match hsailkeyword       "\$full"
syn match hsailkeyword       "\$large"
syn keyword hsailKeyword      kernel
syn keyword hsailKeyword      version
syn keyword hsailKeyword      align
syn match hsailKeyword        "spill_u\(64\|32\|16\|8\)"

" Character string constants
"       Too complex really. Could be "<...>" but those could also be
"       expressions. Don't know how to handle chosen delimiters
"       ("^<sep>...<sep>")
" syn region hsailString		start="<" end=">" oneline

" Operators
syn match hsailOperator	"[-+*/!{}()\\]"
syn match hsailOperator	"&[a-z][a-z0-9_]*"


" Special items for comments
syn keyword hsailInline	contained inlined

" Comments
syn match hsailComment		"//.*" contains=hsailInline

syn case match

" Define the default highlighting.
" For version 5.7 and earlier: only when not done already
" For version 5.8 and later: only when an item doesn't have highlighting yet
if version >= 508 || !exists("did_macro_syntax_inits")
  if version < 508
    let did_macro_syntax_inits = 1
    command -nargs=+ HiLink hi link <args>
  else
    command -nargs=+ HiLink hi def link <args>
  endif

  HiLink hsailComment		Comment
  HiLink hsailTodo		Todo

  HiLink hsailhexNumber		Number
  HiLink hsailoctNumber		Number
  HiLink hsailbinNumber		Number
  HiLink hsaildecNumber		Number
  HiLink hsailfloatNumber	Number
  HiLink hsailReg		Number
  HiLink hsailOperator		Identifier
  HiLink hsailKeyword   	Special
  HiLink hsailStackLinePc    	Comment
  HiLink hsailOpcode		Statement
  HiLink hsailLabel		Type
  delcommand HiLink
endif

let b:current_syntax = "hsail"

" vim: ts=8 sw=2
