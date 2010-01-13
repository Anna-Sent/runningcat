program gen3;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var
F: TextFile;
in1, data, chislo, nom: string;

procedure Generate;
begin
 write('vvedite nomer generiruemogo testa: ');
  readln(nom);
  write('vvedite naturalnoe 4islo (ot 1 do 2147483647): ');
  readln(data);
  in1:='in'+nom+'.txt';
  AssignFile(F, in1);
  Rewrite(F);
  write(F, data);
  closefile(F);
end;

procedure swap;
var
i: array of integer;
dlina,k,g,a,b: integer;


begin
  assignfile(F, in1);
  reset(f);
  read(f, data);
  k:=strtoint(data);
  dlina:=length(data);
  setlength(i, dlina);
  g:=k;
  write('maximalno vozmojnoe chislo: ');
    if dlina > 1 then
    for a:= 10 downto 0 do
      begin
        repeat
          b:=k mod 10;
          k:=k div 10;
            If b=a then
              begin
               i[dlina]:=b;
               write(inttostr(i[dlina]));
               dlina:=dlina-1;
               chislo:=chislo+inttostr(b);
              end;
        until k=0;
      k:=g;
      end
     else
     writeln(inttostr(k));
     closefile(f);
end;

procedure writeanswer;
var
D: textfile;
out1: string;

begin
  out1:='out'+nom+'.txt';
  AssignFile(D, out1);
  rewrite(d);
  write(d, chislo);
  closefile(d);
end;

begin
  generate;
  swap;
  writeanswer;
  if (strtoint(data)<1) then
  abort();
  readln;
end.
