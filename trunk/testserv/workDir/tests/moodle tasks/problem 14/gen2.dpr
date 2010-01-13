program gen2;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var
dat: integer;
F: textfile;
in1,nom,txt: string;


procedure Generate;

begin
  Write('Vvedite nomer generiruegomo testa: ');
  read(nom);
  write('vvedite 4islo M (ot 1 do 1000): ');
  read(dat);
  in1:='in'+nom+'.txt';
  AssignFile(F, in1);
  Rewrite(F);
  write(F, dat);
  closefile(F);
end;

procedure found;
var
b,dl,a: integer;
out1: string;

begin
out1:='out'+nom+'.txt';
assignfile(F, in1);
reset(f);
read(f, dat);
closefile(f);
dl:=length(inttostr(dat));
case dl of
1: for a:=1 to dat do
  begin
    b:=a*a;
    if b mod 10=a then
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
  end;
2: for a:=2 to dat do
  begin
    b:=a*a;
    if b mod 100=a then  begin
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
    end
    else
    if b mod 10=a then begin
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
    end;
  end;
3: for a:=1 to dat do
  begin
    b:=a*a;
    if b mod 1000=a then begin
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
    end
    else
     if b mod 100=a then begin
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
    end
    else
     if b mod 10=a then begin
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
    end;
    end;
4: for a:=1 to dat do
  begin
    b:=a*a;
     if b mod 10000=a then begin
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
    end
    else
     if b mod 1000=a then begin
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
    end
     else
     if b mod 100=a then begin
    writeln(a);
   txt:= txt + ' ' + IntToStr(a);
    end
     else
     if b mod 10=a then begin
    writeln(a);
    txt:= txt + ' ' + IntToStr(a);
    end;
    end;
  end;
  readln;
end;

procedure WriteAnswer;
var
D: textfile;
out1: string;

begin
  out1:='out'+nom+'.txt';
  AssignFile(D, out1);
  rewrite(d);
  write(d, txt);
  closefile(d);
end;


begin
generate;
found;
writeanswer;
if (dat<1) or (dat>1000) then
abort();
readln;
end.
