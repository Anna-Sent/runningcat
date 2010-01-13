program gen1;

{$APPTYPE CONSOLE}

uses
  SysUtils;


var
F: TextFile;
dat,dl,chis: integer;
k,in1,nom: string;

procedure Generate;
begin
  write('vvedite nomer generiruemogo testa: ');
  read(nom);
  write('vvedite naturalnoe 4islo (predel ryada): ');
  read(chis);
  write('vvedite naturalnoe 4islo K: ');
  read(dat);
  in1:='in'+nom+'.txt';
  AssignFile(F, in1);
  Rewrite(F);
  writeln(F, chis);
  write(F, dat);
  closefile(F);
end;

procedure OutK;
var
str: string;
i: integer;
kvadr: int64;

begin
  for i:=1 to chis do begin
      kvadr:=i*i;
      str:=str+inttostr(kvadr);
    end;
  writeln(str);
  assignfile(F, in1);
  Reset(f);
  Read(F, dat);
  dl:=length(str);
  k:=copy(str,dat,1);
  write('K-toe 4islo: ', k);
  closefile(f);
  readln;
end;

procedure WriteAnswer;
var
d: textfile;
out1: string;

begin
  out1:='out'+nom+'.txt';
  AssignFile(d, out1);
  rewrite(d);
  write(d, k);
  closefile(d);
end;

begin
  Generate;
  OutK;
  writeanswer;
  if (dat<1) or (dat>dl) or (chis<1) then
  abort();
  readln;
end.

