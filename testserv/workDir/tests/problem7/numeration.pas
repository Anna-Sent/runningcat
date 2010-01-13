var b,n,r:longint;s:string;
begin
        readln(b,n);
        if (n=0)then
                writeln('0')
        else begin
                s:='';
                while(n>0)do begin
                        r := n mod b;
                        if (r<10) then
                                s := char(r+integer('0'))+s
                        else
                                s := char(r-10+integer('A'))+s;
                        n := n div b;
                end;
                writeln(s);
        end;
end.
