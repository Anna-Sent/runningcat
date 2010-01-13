program Project1;

{$APPTYPE CONSOLE}

uses
  SysUtils;

const
  RomeDigits: array [1..13] of string[2] = ('I', 'IV', 'V', 'IX', 'X', 'XL', 'L', 'XC', 'C', 'CD', 'D', 'CM', 'M');
  ArabicNumbers: array [1..13] of integer = (1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000);
var
  arabic: integer;
  res,n:string;

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

Procedure ArabicToRome;
var
 i:integer;
begin
  res:='';
  i:=13;
  while arabic>0 do begin
    while ArabicNumbers[i]>arabic do
     i:=i-1;
  res:= res+RomeDigits[i];
  arabic:=arabic-ArabicNumbers[i];
  end;
end;



procedure Generate;
var   kolvoTests,z,zlast:integer;
NameInFile,NameOutFile:string;
outFile: textfile;
inFile: textfile;
  begin
writeln('vvedite chislo neobhodimyh testov');
readln(kolvoTests);
z:=0;
zlast:=0;
repeat
  NameInFile:='in'+inttostr(z+1)+'.txt';
  writeln('vvedite naturalnoe 4islo v diapazone ot 1 do 3999');
  read(n);
  AssignFile(inFile, NameInFile);
  Rewrite(inFile);
  write(inFile, n);
  closefile(inFile);
  assignfile(inFile, NameInFile);
  Reset(infile);
  Read(inFile, n);
  closefile(infile);
  NameOutFile:='out'+inttostr(z+1)+'.txt';
  AssignFile(outFile, NameOutFile);
  rewrite(outFile);
  if (IsNumber(n)=true) then begin
  arabic:=strtoint(n);
  if (arabic>0) and (arabic<4000)then
  begin
  writeln('arabskoe 4islo: ', n);
  arabic:=strtoint(n);
  ArabicToRome; z:=z+1;
  zlast:=z;
  write(outFile, res);   writeln('rimskoe chislo: ', res);
  end
   else
  begin z:=zlast end;
  end
  else
  begin z:=zlast end;
  closefile(outFile);
  readln;
until z=kolvotests;
end;

begin
  Generate;
  readln;
end.
