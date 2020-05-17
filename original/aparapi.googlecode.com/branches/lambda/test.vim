function! ToggleSyntax()
   if exists("g:syntax_on")
      syntax off
   else
      syntax enable
   endif
endfunction

nmap <silent>  ;s  :call ToggleSyntax()<CR>
nmap <silent>  ;t  :echo "junk"<CR>
function! LookUpwards()
   "Locate current column and preceding line from which to copy...
   let column_num      = virtcol('.')
   echo "col = " column_num
endfunction


imap <silent>  <C-Y> : call LookUpwards()<CR>
