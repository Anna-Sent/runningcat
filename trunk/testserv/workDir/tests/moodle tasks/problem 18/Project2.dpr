program Project2;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var
k,l,res:integer;
lstring,kstring:string ;
outFile: textfile;
inFile: textfile;

Procedure multiply;
var len,i,j,c,z,lnew:integer;
line:string;
begin
writeln;
k:=strtoint(kstring);
l:=strtoint(lstring);
res:=k*l;
len:=length(inttostr(res));
i:=len-length(kstring);
kstring:='';
for j:=1 to i do
kstring:=kstring+' ';
kstring:=kstring+inttostr(k);
writeln(outFile,kstring);
writeln(kstring);
i:=len-length(lstring);
lstring:='';
for j:=1 to i do
lstring:=lstring+ ' ';
lstring:=lstring+ inttostr(l);
writeln(outFile,lstring);
writeln(lstring);
for j:=1 to len do    begin
write(outFile,'-');
write('-');    end;
writeln(outFile);
writeln;
kstring:=inttostr(k);
lstring:=inttostr(l);
c:=1;
lnew:=l mod 10;
for j:=1 to (length(inttostr(l))) do
begin
z:=len-length(inttostr(lnew*k));
lstring:='';
for i:=1 to z do
lstring:=lstring+' ';
lstring:=lstring+(inttostr(lnew*k));
writeln(outFile,lstring);
writeln(lstring);
c:=c*10;
l:=l-lnew;
lnew:=l mod(10*c);
end;
for j:=1 to len do    begin
write('-');
write(outFile,'-');  end;
writeln; writeln(outfile);
writeln(res);
writeln(outFile,res);

end;



procedure Generate;
var   kolvoTests,z,zlast,i,j:integer;
NameInFile,NameOutFile:string;
simbol:char;
  begin
writeln('vvedite chislo neobhodimyh testov');
readln(kolvoTests);
z:=0;
repeat
  NameInFile:='in'+inttostr(z+1)+'.txt';
  writeln('vvedite neobhodimye chisla');
  readln(k); readln(l);
  AssignFile(inFile, NameInFile);
  Rewrite(inFile);
  writeln(inFile, k);
  write(inFile, l);
  closefile(inFile);
  assignfile(inFile, NameInFile);
  Reset(infile);
  i:=0;
  j:=0;
   while not Eof(inFile) do   begin j:=j+1;
   if j=1 then
  Readln(inFile, kstring)
  else    Readln(inFile, lstring);
 end;
  writeln(kstring,' x ', lstring);
  closefile(infile);
  NameOutFile:='out'+inttostr(z+1)+'.txt';
  AssignFile(outFile, NameOutFile);
  rewrite(outFile);
  k:=strtoint(kstring);
  l:=strtoint(lstring);
  if (K>0) and (L>0) and (k=((k*l)div(l)))then
  begin z:=z+1 ;
  multiply; end
  else begin
  z:=zlast; end;
  zlast:=z;
  closefile(outFile);
  until z=kolvotests ;
   readln;
end;



begin
Generate
end.

