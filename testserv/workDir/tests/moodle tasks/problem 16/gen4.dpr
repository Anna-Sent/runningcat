program gen4;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var
F: TextFile;
in1, nom, s:String;
dat: integer;

procedure MyWriteln(const S: string);
var
    NewStr: string;
begin
//    SetLength(NewStr, Length(S));
//    CharToOem(PChar(S), PChar(NewStr));
    Writeln(S);//NewStr);
end;

procedure Generate;

begin
  write('vvedite nomer generiruemogo testa: ');
  read(nom);
  write('vvedite 4islo ot 1 do 1000: ');
  read(dat);
  in1:='in'+nom+'.txt';
  AssignFile(F, in1);
  Rewrite(F);
  write(F, dat);
  closefile(F);
end;

procedure chisla;
var
d100: integer;
d10: integer;
d1: integer;

begin
  assignfile(F, in1);
  reset(f);
  read(F, dat);
  d100:=dat div 100;
  d10:=dat div 10 mod 10;
  d1:=dat mod 10;
    case d100 of
      1:s:=s+'сто ';
      2:s:=s+'двести ';
      3:s:=s+'триста ';
      4:s:=s+'четыреста ';
      5:s:=s+'пятьсот ';
      6:s:=s+'шестьсот ';
      7:s:=s+'семьсот ';
      8:s:=s+'восемьсот ';
      9:s:=s+'девятьсот ';
    end;
    if d10=1 then
    begin
    case d1 of
      1:s:=s+'один';
      2:s:=s+'два';
      3:s:=s+'три';
      4:s:=s+'четыре';
      5:s:=s+'пять';
      6:s:=s+'шесть';
      7:s:=s+'семь';
      8:s:=s+'восемь';
      9:s:=s+'девять';
    end;
    if d1=0 then s:=s+'десять'
    else s:=s+'надцать';
    end;
    case d10 of
      2:s:=s+'двадцать ';
      3:s:=s+'тридцать ';
      4:s:=s+'сорок ';
      5:s:=s+'пять';
      6:s:=s+'шесть';
      7:s:=s+'семь';
      8:s:=s+'восемь';
      9:s:=s+'девяносто ';
    end;
    if (d10>=5) and (d10<=8) then s:=s+'десят ';
    if d10<>1 then
    case d1 of
      1:s:=s+'один';
      2:s:=s+'два';
      3:s:=s+'три';
      4:s:=s+'четыре';
      5:s:=s+'пять';
      6:s:=s+'шесть';
      7:s:=s+'семь';
      8:s:=s+'восемь';
      9:s:=s+'девять';
    end;
    if dat=1000 then
    begin
    writeln('тысяча');
    end;
  Mywriteln(s);
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
  write(d, s);
  closefile(d);
end;

begin
  generate;
  chisla;
  writeanswer;
  if (dat<1) or (dat>1000) then
  abort();
  readln;
end.
