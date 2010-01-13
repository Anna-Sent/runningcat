program Project1;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var
  Chislo, A: integer;
  res,n:string;
  outFile: textfile;
  inFile: textfile;

function IsNumber(const n: String): Boolean;
var i, len: Integer;
begin
  len := Length(n);
  if len > 0 then begin
    Result := True;
    for i := 1 to len do
      if not (n[i] in ['0'..'9']) then begin
        Result := False;
        break;
      end;
  end
  else
    Result := False;
end;


Procedure Find;
var
 m, i, k, s,b:integer;
 simbol:string;
begin
  for i:=1 to A do begin
m:=i; k := 0; s := 0;
while m>0 do begin
b:= m mod 10;
if (b <> 0) and (i mod b=0) then s:=s+1;
k:=k+1;
m:=m div 10;
end;
simbol:=inttostr(i)+ ' ';
if k=s then begin write(outFile,simbol); write (' ', i);
end;
end; writeln;
     end;

procedure Generate;
var   kolvoTests,z:integer;
NameInFile,NameOutFile:string;
  begin
writeln('vvedite chislo neobhodimyh testov');
readln(kolvoTests);
for z:=1 to kolvoTests do
begin
  NameInFile:='in'+inttostr(z)+'.txt';
  writeln('vvedite naturalnoe chislo');
  read(n);
  AssignFile(inFile, NameInFile);
  Rewrite(inFile);
  write(inFile, n);
  closefile(inFile);
  assignfile(inFile, NameInFile);
  Reset(infile);
  Read(inFile, n);
  writeln('poisk do ', n);
  readln;
  closefile(infile);
  NameOutFile:='out'+inttostr(z)+'.txt';
  AssignFile(outFile, NameOutFile);
  rewrite(outFile);
  if (IsNumber(n)=true) then begin
  A:=strtoint(n);
  if (A>0)then
  Find
  else
  begin write(outFile, 'oshibka vvoda'); writeln('oshibka vvoda'); end;
  end
  else
  begin write(outFile, 'oshibka vvoda'); writeln('oshibka vvoda'); end;
  closefile(outFile);
end;
end;

begin
  Generate;
  readln;
end.

